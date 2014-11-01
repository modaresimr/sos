package sos.base;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.registry.Registry;
import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.Property;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.AmbulanceUtils;
import sos.ambulance_v2.base.AmbulanceConstants.ATstates;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.ambulance_v2.tools.SOSParticleFilter;
import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Center;
import sos.base.entities.Civilian;
import sos.base.entities.Edge;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;
import sos.base.entities.PoliceForce;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.entities.VirtualCivilian;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.message.structure.channel.VoiceChannel;
import sos.base.message.system.MessageSystem;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.reachablity.tools.EdgeElement;
import sos.base.update.GoodCommStrategy;
import sos.base.update.LowCommunicationStarategy;
import sos.base.update.NullStrategy;
import sos.base.update.UpdateStrategy;
import sos.base.util.SOSGeometryTools;
import sos.base.util.Utils;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.base.util.sosLogger.TableLogger;
import sos.base.worldGraph.VirtualGraphEdge;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.worldModel.SearchBuilding;
import sos.tools.GraphEdge;

public class Updater implements MessageXmlConstant {
	private SOSAgent<? extends StandardEntity> me;
	private final SOSLoggerSystem msgUpdateLog;
	//	public TreeSet<Road> changedRoadsReachabilityByMsg = new TreeSet<Road>(new IDComparator());
	//	public TreeSet<Road> changedRoadsReachabilityBySense = new TreeSet<Road>(new IDComparator());
	private UpdateStrategy messageUpdater;
	private boolean isBlockadeExistInMap = false;
	private HashSet<Road> changedRoadsForReCalculateReachablity = new HashSet<Road>();
	private SOSLoggerSystem worldModelLog;
	private static int HP_PRECISION;
	private HashSet<EntityID> changedEntityForLog=new HashSet<EntityID>();
	public Updater(SOSAgent<? extends StandardEntity> me) {
		this.me = me;
		HP_PRECISION = me.getConfig().getIntValue("perception.los.precision.hp", 800);
		msgUpdateLog = new SOSLoggerSystem(me.me(), "MessageUpdate", SOSConstant.CREATE_BASE_LOGS, OutputType.File);
		msgUpdateLog.setFullLoggingLevel();
		this.me.sosLogger.addToAllLogType(msgUpdateLog);
		worldModelLog = new SOSLoggerSystem(me.me(), "MyWorldModel", true, OutputType.File,true);
		this.me.sosLogger.addToAllLogType(worldModelLog);
		
		if (me.messageSystem.type == Type.LowComunication)
			messageUpdater = new LowCommunicationStarategy(me);
		else if (me.messageSystem.type == Type.NoComunication)
			messageUpdater = new NullStrategy(me);
		else
			messageUpdater = new GoodCommStrategy(me);
	}

	private HashMap<String, StandardEntity> cachedEntity = new HashMap<String, StandardEntity>();

	public StandardEntity getCachedEntity(String urn) {
		StandardEntity entity;
		if (cachedEntity.containsKey(urn))
			entity = cachedEntity.get(urn);
		else {
			entity = (StandardEntity) Registry.getCurrentRegistry().createEntity(urn, new EntityID(-99999999));
			//			entity.setNotValid(true);
			cachedEntity.put(urn, entity);
		}
		for (Property propery : entity.getProperties()) {
			propery.undefine();
		}
		return entity;
	}

	/**
	 * UPDATE WORLD MODEL BY SENSE -> developed by
	 * 
	 * @r@mik
	 */
	public void update(ChangeSet changeSet, int time) {
		log().heavyTrace(changeSet);
		me.model().setTime(time);
		me.model().UpdateHumanRectangles();
		if (time < 2) // correct properties values send since cycle 2
			return;
		long start = System.currentTimeMillis();
		messageUpdater.updatingBySenseStart();
		changedRoadsForReCalculateReachablity.clear();
		HashSet<Area> changedAreas = new HashSet<Area>();// for updating graphEdge states
		deleteDeletedEntities(changeSet);
		ArrayList<EntityID> sortedChangedList = getSortedChangedList(changeSet);
		boolean finishAreasUpdateAndDidNessesaryActivity = false;
		long newingItemTime = 0;
		computeOldRoadCosts();
		for (EntityID enID : sortedChangedList) {
			changedEntityForLog.add(enID);
			StandardEntity oldObj = me.model().getEntity(enID);
			long t1 = System.currentTimeMillis();
			newingItemTime += System.currentTimeMillis() - t1;
			log().logln("-----------------------------------");
			if (oldObj != null)
				log().logln("old =" + (oldObj).fullDescription());
			//Ali			log().logln("new =" + (newObj).fullDescription());
			StandardEntity newObj;
			if (oldObj == null) {
				newObj = (StandardEntity) Registry.getCurrentRegistry().createEntity(changeSet.getEntityURN(enID), enID);
				me.model().addEntity(newObj);
			} else
				newObj = getCachedEntity(changeSet.getEntityURN(enID));

			updateProperties(newObj, changeSet.getChangedProperties(enID), time); // update properties
				
			//Ali			log().logln("after assigning properties!");
			log().logln("new =" + (newObj).fullDescription());
			
			if (newObj instanceof Building) {// ----------------------Building
				handleBuildingChange((Building) oldObj, (Building) newObj, changeSet.getChangedProperties(enID), time);
				changedAreas.add((Building) oldObj);
			} else if (newObj instanceof Road) {// -------------------Road
				handleRoadChange((Road) oldObj, (Road) newObj, changeSet.getChangedProperties(enID), time);
				changedAreas.add((Area) oldObj);
			} else if (newObj instanceof Blockade) {// ---------------Blockade
				isBlockadeExistInMap = true;
				handleBlockadeChange((Blockade) oldObj, (Blockade) newObj, changeSet.getChangedProperties(enID), time);
			} else if (newObj instanceof Human) {// ------------------Human
				if (!finishAreasUpdateAndDidNessesaryActivity) {//at first blockade and areas has been updated then agent it self updated and then other humans will be update.
					doAfterAreasAndBeforeAgentUpdateActivities(changedAreas);
					if (enID.equals(me.me().getID()) || me.me() instanceof Center) {
						finishAreasUpdateAndDidNessesaryActivity = true;
					} else
						log().warn("why i'm not the first entity to update???");

				}
				handleHumanChange((Human) oldObj, (Human) newObj, changeSet.getChangedProperties(enID), time);
			} else
				log().warn("UNKOWN Entity" + newObj);

			if (oldObj != null) {
				log().logln("final =" + (oldObj).fullDescription());
			}
		}
		log().logln("changed Areas=" + changedAreas);
		me.sosLogger.base.debug("worldmodel newing Item Time=" + newingItemTime + " ms");
		// --------------------------------- changed Areas
		//		doChangeReachablity();
		//		updateMsgRoads();
		finishUpdate();
		doAfterUpdateAllAgentActivites(changedAreas);
		updateAgentReachableEdges();
		messageUpdater.updatingBySenseFinished(changeSet);
		log().logln("-----------------------------------" + time + " :got: " + (System.currentTimeMillis() - start) + "ms");
		checkNoBlockadeMap();
		noOrLowCommunictionActivities();
		
		logWorldModel();
		changedEntityForLog.clear();
	}

	private void logWorldModel() {
		if(SOSConstant.IS_CHALLENGE_RUNNING)
			return;
		HashMap<Class<? extends StandardEntity>,TableLogger> tablelogs=new HashMap<Class<? extends StandardEntity>,TableLogger>();
		for (EntityID eid : changedEntityForLog) {
			StandardEntity entity=me.model().getEntity(eid);
			if(entity instanceof Blockade)
				continue;
			TableLogger tableLogger = tablelogs.get(entity.getClass());
			if(tableLogger==null){
				tableLogger=new TableLogger(30);
				tablelogs.put(entity.getClass(), tableLogger);
			}
			String rowTitle = entity+"";
			
			
			tableLogger.addScore(rowTitle,"UpdateTime" , entity.updatedtime()+"");
			tableLogger.addScore(rowTitle,"SenseTime" , entity.getLastSenseTime()+"");
			tableLogger.addScore(rowTitle,"MsgTime" , entity.getLastMsgTime()+"");
			tableLogger.addScore(rowTitle,"LastReachableTime" , entity.getLastReachableTime()+"");
			if(entity instanceof Area){
				tableLogger.addScore(rowTitle,"AreaIndex" , ((Area)entity).getAreaIndex()+"");
			}
			if(entity instanceof Road){
				tableLogger.addScore(rowTitle,"RoadIndex" , ((Road)entity).getRoadIndex()+"");
				if(((Road)entity).isBlockadesDefined()){
					tableLogger.addScore(rowTitle,"BlockadesID" , ((Road)entity).getBlockadesID()+"");
					tableLogger.addScore(rowTitle,"Blockades" , ((Road)entity).getBlockades()+"");
				}
			}
			if(entity instanceof Building){
				
				tableLogger.addScore(rowTitle,"BuildingIndex" , ((Building)entity).getBuildingIndex()+"");
				
				if(((Building)entity).isFierynessDefined())
					tableLogger.addScore(rowTitle,"Fieryness" , ((Building)entity).getFieryness()+"");
				tableLogger.addScore(rowTitle,"VFieryness" , ((Building)entity).virtualData[0].getFieryness()+"");
				if(((Building)entity).isTemperatureDefined())
					tableLogger.addScore(rowTitle,"Temp" , ((Building)entity).getTemperature()+"");
				tableLogger.addScore(rowTitle,"VTemp" , ((Building)entity).virtualData[0].getTemperature()+"");
				
				if(((Building)entity).isBrokennessDefined())
					tableLogger.addScore(rowTitle,"Brokenness" , ((Building)entity).getBrokenness()+"");
				tableLogger.addScore(rowTitle,"BuildingCode" , ((Building)entity).getBuildingCodeEnum()+"");
				tableLogger.addScore(rowTitle,"Floors" , ((Building)entity).getFloors()+"");
				tableLogger.addScore(rowTitle,"Importance" , ((Building)entity).getImportance()+"");
				tableLogger.addScore(rowTitle,"TotalArea" , ((Building)entity).getTotalArea()+"");
			}
			if(entity instanceof Human){
				tableLogger.addScore(rowTitle,"AgentIndex" , ((Human)entity).getAgentIndex()+"");
				if(((Human)entity).isBuriednessDefined())
					tableLogger.addScore(rowTitle,"Buriedness" , ((Human)entity).getBuriedness()+"");
				if(((Human)entity).isDamageDefined())
					tableLogger.addScore(rowTitle,"Damage" , ((Human)entity).getDamage()+"");
				if(((Human)entity).isHPDefined())
					tableLogger.addScore(rowTitle,"HP" , ((Human)entity).getHP()+"");
				if(((Human)entity).isPositionDefined())
					tableLogger.addScore(rowTitle,"Position" , ((Human)entity).getPosition()+"");
				if(((Human)entity).isHPDefined()&&((Human)entity).isDamageDefined())
				tableLogger.addScore(rowTitle,"SimpleDeathTime" , SimpleDeathTime.getEasyLifeTime(((Human)entity).getHP(), ((Human)entity).getDamage(), ((Human)entity).updatedtime()));
				if(me instanceof AmbulanceTeamAgent){
					tableLogger.addScore(rowTitle,"DeathTime" , ((Human)entity).getRescueInfo().getDeathTime()+"");
					tableLogger.addScore(rowTitle,"InjuryDeath" , ((Human)entity).getRescueInfo().getInjuryDeathTime()+"");
					tableLogger.addScore(rowTitle,"FireDeath" , ((Human)entity).getRescueInfo().getFireDeathTime()+"");
					tableLogger.addScore(rowTitle,"TimeToRefuge" , ((Human)entity).getRescueInfo().getTimeToRefuge()+"");
					tableLogger.addScore(rowTitle,"IgnoreReason" , ((Human)entity).getRescueInfo().getIgnoreReason()+"");
					tableLogger.addScore(rowTitle,"IgnoredUntil" , ((Human)entity).getRescueInfo().getIgnoredUntil()+"");
				}
			}
			if(entity instanceof FireBrigade){
				tableLogger.addScore(rowTitle,"FireIndex" , ((FireBrigade)entity).getFireIndex()+"");
				if(((FireBrigade)entity).isWaterDefined())
					tableLogger.addScore(rowTitle,"Water" , ((FireBrigade)entity).getWater()+"");
			}
			if(entity instanceof AmbulanceTeam){
				tableLogger.addScore(rowTitle,"AmbIndex" , ((AmbulanceTeam)entity).getAmbIndex()+"");
			}
			if(entity instanceof PoliceForce){
				tableLogger.addScore(rowTitle,"PoliceIndex" , ((PoliceForce)entity).getPoliceIndex()+"");
			}
			if(entity instanceof Civilian){
				tableLogger.addScore(rowTitle,"FirstPosition" , ((Civilian)entity).getFirstPosition()+"");
				tableLogger.addScore(rowTitle,"FoundTime" , ((Civilian)entity).getFoundTime()+"");
			}
		}
		for (Entry<Class<? extends StandardEntity>, TableLogger> classTable : tablelogs.entrySet()) {
			worldModelLog.logln(classTable.getValue().getTablarResult("UpdateTime"));
		}
	}

	HashMap<Road, Integer> oldRoadCosts = new HashMap<Road, Integer>();

	private void computeOldRoadCosts() {
		for (Road road : me.getVisibleEntities(Road.class)) {
			if (!road.isBlockadesDefined() || road.getLastSenseTime() <= me.model().getLastAfterShockTime())
				oldRoadCosts.put(road, -1);
			else {
				int cost = 0;
				for (Blockade blockade : road.getBlockades()) {
					cost += blockade==null?0:blockade.getRepairCost();
				}
				oldRoadCosts.put(road, cost);
			}
		}
	}

	private void finishUpdate() {
		//		HashSet<Human> humanInSense = new HashSet<Human>(me.getVisibleEntities(Human.class));
		HashSet<Building> buildingInSense = new HashSet<Building>(me.getVisibleEntities(Building.class));
		Shape visibleShape = me.lineOfSightPerception.getThisCycleVisibleShapeFromMe();
		ArrayList<Human> humanInSenesedBuildings = new ArrayList<Human>();
		for (Human human : me.model().humans()) {
			if (!human.isPositionDefined())
				continue;

			if (buildingInSense.contains(human.getAreaPosition()))
				humanInSenesedBuildings.add(human);
		}
		humanInSenesedBuildings.removeAll(me.getVisibleEntities(Human.class));
		Area building = me.me().getAreaPosition();

		if (visibleShape.contains(building.getX(), building.getY())) {
			for (Human human : humanInSenesedBuildings) {
				if (visibleShape.contains(human.getX(), human.getY())) {
					human.setBuriedness(0);
					human.setDamage(0);
					
					if(!(human.getPosition() instanceof AmbulanceTeam)){  
//						if(me.model().refuges().isEmpty())
							human.setPosition(me.model().roads().get(0).getID());//to handle bug when agent think a civilian is in a building but is not really in building.
							human.setLastSenseTime(me.time());//to handle bug when agent think a civilian is in a building but is not really in building.
//						else
//							human.setPosition(me.model().refuges().get(0).getID());
						
					}
				}	
			}
		}

		doAfterShocks();
		disableDeathTimesIfNeeded();
	}

	private void disableDeathTimesIfNeeded() {
		for (Civilian civ : me.model().civilians()) {
			civ.getRescueInfo().disableDeathTimesIfNeeded();
		}
	}

	private void doAfterShocks() {
		int aftershocktimePassed = me.model().time() - me.model().getLastAfterShockTime();
		if (aftershocktimePassed >= 0 && aftershocktimePassed < 3) {
			for (VirtualCivilian vc : me.model().getVirtualCivilians()) {
				vc.setReallyReachable(false);
			}
			for (Human hm : me.model().humans()) {
				hm.getImReachableToEdges().clear();
				
			}
			if (me instanceof PoliceForceAgent) {
				for (Road road : me.model().roads()) {
					if (me.model().time() - road.getLastSenseTime() > aftershocktimePassed) {
						for (short ge_ind : road.getGraphEdges()) {
							GraphEdge ge = me.model().graphEdges().get(ge_ind);
							if (ge.getState() == GraphEdgeState.Open)
								ge.setState(GraphEdgeState.FoggyOpen);
							if (ge.getState() == GraphEdgeState.Block)
								ge.setState(GraphEdgeState.Block);
							else
								ge.setState(GraphEdgeState.FoggyBlock);
							me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
						}
					}
				}
			} else {
				HashSet<Road> visibleRoads = new HashSet<Road>(me.getVisibleEntities(Road.class));
				for (Building building : me.model().buildings()) {
					building.setIsReallyReachable(false);
					for (Area neighbor : building.getNeighbours()) {
						if (neighbor instanceof Road) {
							if (visibleRoads.contains(neighbor))
								continue;
							for (short ge_ind : neighbor.getGraphEdges()) {
								GraphEdge ge = me.model().graphEdges().get(ge_ind);
								if (ge.getState() == GraphEdgeState.Block)
									ge.setState(GraphEdgeState.Block);
								else
									ge.setState(GraphEdgeState.FoggyBlock);
								me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
							}
						}
					}
				}
			}
			for (Human hum : me.model().humans()) {
				
				if (hum.getPositionArea() instanceof Building||me instanceof PoliceForceAgent) {
					hum.setIsReallyReachable(false);
				}
			}
		}

		if (aftershocktimePassed == 0)
			log().warn("Aftershock detected....================================================");
	}

	private void deleteDeletedEntities(ChangeSet changeSet) {
		for (EntityID entityId : changeSet.getDeletedEntities()) {
			StandardEntity entity = me.model().getEntity(entityId);
			if (entity instanceof Blockade) {
				Blockade blockadeEntity = (Blockade) entity;
				ArrayList<Road> rs = blockadeEntity.removed();
				for (Road r : rs) {
					if (r.isBlockadesDefined()) {
						//							changedRoadsForReCalculateReachablity.remove(r);
						changedRoadsForReCalculateReachablity.add(r);
					}
				}
				changedRoadsForReCalculateReachablity.add(blockadeEntity.getPosition());
				me.model().removeBlockade(blockadeEntity);
			}
		}
	}

	private void doAfterAreasAndBeforeAgentUpdateActivities(HashSet<Area> changedAreas) {
		long t1 = System.currentTimeMillis();
		doChangeReachablity();
		updateChangedAreas(changedAreas);
		me.sosLogger.base.debug("reacablity and graph edge update got:" + (System.currentTimeMillis() - t1) + " ms");
		me.sosLogger.move.debug("Should not any dijkstra called till here!!!");
	}

	private void doAfterUpdateAllAgentActivites(HashSet<Area> changedAreas) {
		for (Area ar : changedAreas) {
			if (ar instanceof Building) {
				Building b = (Building) ar;
				if (canSeeInside(b)) {
					removeVirtualCivilians(b);
					setSearchForCivilian(b);
					if (b.equals(me.me().getAreaPosition())) {
						//					if (canSeeInside(b)) {
						ArrayList<Civilian> viscivs = me.getVisibleEntities(Civilian.class);
						ArrayList<Civilian> civ = b.getCivilians();
						for (Civilian civilian : civ) {
							if (!viscivs.contains(civilian)) {
								if (!me.model().refuges().isEmpty())
									civilian.setPosition(me.model().refuges().get(0).getID());
								else
									me.model().removeEntity(civilian.getID());
							}
						}
						//					}
					}
				}
			}
		}

	}

	public boolean canSeeInside(Building b) {
		if (me.me().getAreaPosition().equals(b) && (!b.getShape().contains(b.getX(), b.getY())))
			return true;
		if (me.lineOfSightPerception.getThisCycleVisibleShapeFromMe().contains(b.getX(), b.getY()))
			return true;

		if (!b.isSearchedForCivilian()) {
			Point positionPoint = new Point(me.me().getPositionPoint().getIntX(), me.me().getPositionPoint().getIntY());
			for (ShapeInArea searchArea : b.getSearchAreas()) {
				if (searchArea.getArea(me.model()).equals(me.me().getAreaPosition()) && searchArea.contains(positionPoint)) {
					return true;
				}
			}
		}
		return false;
	}

	private void checkHasbeenSeenBySelf(Building b) {
		if (me.model().searchWorldModel.getSearchBuilding(b).isHasBeenSeenBySelf())
			return;
		if (me instanceof CenterAgent)
			return;
		if (me.me().getAreaPosition().equals(b)) {
			me.model().searchWorldModel.getSearchBuilding(b).setHasBeenSeenBySelf(true);
		} else {
			if (me.lineOfSightPerception.getThisCycleVisibleShapeFromMe().contains(b.getX(), b.getY())) {
				me.model().searchWorldModel.getSearchBuilding(b).setHasBeenSeenBySelf(true);
			}
			if (!me.model().searchWorldModel.getSearchBuilding(b).isHasBeenSeenBySelf()) {
				Point positionPoint = new Point(me.me().getPositionPoint().getIntX(), me.me().getPositionPoint().getIntY());
				for (ShapeInArea searchArea : b.getSearchAreas()) {
					if (searchArea.getArea(me.model()).equals(me.me().getAreaPosition()) && searchArea.contains(positionPoint)) {
						me.model().searchWorldModel.getSearchBuilding(b).setHasBeenSeenBySelf(true);
						break;
					}
				}
			}
		}

		me.sosLogger.search.debug("checking " + b + " for has been seen==>" + me.model().searchWorldModel.getSearchBuilding(b).isHasBeenSeenBySelf());
	}

	private void doChangeReachablity() {
		for (Road r : changedRoadsForReCalculateReachablity) {
			if (r.isBlockadesDefined()) {
				r.setReachablityChanges();
				log().trace("setting reachablity changes for road:" + r.fullDescription());
			} else
				log().warn("how a road added to reachablityRecalc with undefined blockade?");
		}
		changedRoadsForReCalculateReachablity.clear();
		//		changedRoadsReachabilityByMsg.removeAll(changedAreas);
	}

	private void updateAgentReachableEdges() {
		if (me.me() instanceof Center)
			return;
		if (me.time() < me.FREEZE_TIME)
			return;
		ArrayList<Human> sensedHuman = me.getVisibleEntities(Human.class);
		for (Human human : sensedHuman) {
			if (human.getAreaPosition() instanceof Road && !(human instanceof Civilian)) {
				Road myPosition = (Road) human.getAreaPosition();
				for (int i = 0; i < myPosition.getPassableEdges().length; i++) {
					boolean reachablity = Reachablity.isReachableAgentToEdge(human, myPosition, myPosition.getPassableEdges()[i]) == ReachablityState.Open;
					if (reachablity)
						human.addImReachableToEdge(myPosition.getPassableEdges()[i]);
				}
			} else {
				Area myPosition = human.getAreaPosition();
				for (int i = 0; i < myPosition.getPassableEdges().length; i++) {
					human.addImReachableToEdge(myPosition.getPassableEdges()[i]);
				}
			}
		}

	}

//	public static final int[] resetTime = { 70, 130, 180, 220, 260, 300, 350, 400, 450, 1000 };
	int i = 0;

	private void noOrLowCommunictionActivities() {
		if (me instanceof PoliceForceAgent)
			return;

		if (me.messageSystem.type == Type.NoComunication || me.messageSystem.type == Type.LowComunication) {
//			if (me.time() == resetTime[i]) {
//				i++;
				for (Road road : me.model().roads()) {
					if(road.getLastSenseTime()>me.model().time()-60)
						continue;
					me.model().block.removeMiddleBlockadesOfRoad(road);
					clearAllBlockadeOfRoad(road);

					//					road.setBlockades(new ArrayList<EntityID>());
					for (short ge_ind : road.getGraphEdges()) {
						GraphEdge ge = me.model().graphEdges().get(ge_ind);
						if (ge.getState() == GraphEdgeState.Open)
							ge.setState(GraphEdgeState.Open);
						else
							ge.setState(GraphEdgeState.FoggyBlock);
						me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
					}
					//					road.setReachablityChanges();
				}
//			}
		}
	}

	private void checkNoBlockadeMap() {
		if ((me.me() instanceof Center || ((Human) me.me()).getBuriedness() == 0) && me.time() > (/* Math.random()*2+ */15/* baraye inke hame yehoo nakhan process konan */) && !isBlockadeExistInMap) {
			if (me.time() == 16)
				log().consoleInfo("No Blockade MAP!!!!");

			for (Road road : me.model().roads()) {
				me.model().block.removeMiddleBlockadesOfRoad(road);
				road.setBlockades(new ArrayList<EntityID>());
				for (short ge_ind : road.getGraphEdges()) {
					GraphEdge ge = me.model().graphEdges().get(ge_ind);
					ge.setState(GraphEdgeState.Open);
					me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
				}
				road.setReachablityChanges();
			}
		}
	}

	private ArrayList<EntityID> getSortedChangedList(final ChangeSet changeSet) {
		ArrayList<EntityID> changed = new ArrayList<EntityID>(changeSet.getChangedEntities());

		try {
			Collections.sort(changed, new ChangesetComparator(changeSet, me, false));
		} catch (Exception e) {
			for (EntityID entityID : changed) {
				log().heavyTrace(entityID + ":" + changeSet.getEntityURN(entityID));
			}
			Collections.sort(changed, new ChangesetComparator(changeSet, me, true));
			log().error(e);
		}
		return changed;
	}

	// *************************************************************************/
	private void updateChangedAreas(HashSet<Area> changedAreas) {
		TreeSet<Short> usedGraphEdgesIndex = new TreeSet<Short>();
		for (Area ar : changedAreas) {
			//			if (ar instanceof Road) {
			//				log().trace("set ReachablityChanges for :" + ar);
			//				((Road) ar).setReachablityChanges();
			//			}
			boolean hasBeenChanged = false, isAllOpen = true;
			for (Short ind : ar.getGraphEdges()) {
				GraphEdge ge = me.model().graphEdges().get(ind);
				Edge one = me.model().nodes().get(ge.getHeadIndex()).getRelatedEdge();
				Edge two = me.model().nodes().get(ge.getTailIndex()).getRelatedEdge();
				if (ge instanceof WorldGraphEdge) {
					if (ar instanceof Building) {
						ge.setState(GraphEdgeState.Open);
					} else {
						GraphEdgeState result = Utils.convertReachabilityStatesToGraphEdgeStates(Reachablity.isReachable((Road) ar, one, two));
						if (result != ge.getState())
							hasBeenChanged = true;
						if (result == GraphEdgeState.Block)
							isAllOpen = false;
						ge.setState(result);
					}
				} else if (ge instanceof VirtualGraphEdge && !usedGraphEdgesIndex.contains(ge.getIndex())) {
					// Area next = me.model().areas().get(one.getMyAreaIndex() == ar.getAreaIndex() ? two.getMyAreaIndex() : one.getMyAreaIndex());
					ge.setState(GraphEdgeState.Open);// TODO
				}
				if (ge.getState() == GraphEdgeState.Open)
					me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
			}
			if (hasBeenChanged)
				messageUpdater.senseRoadReachablityChanged(ar, isAllOpen);

		}
		// if (ar instanceof Road) {//FIXME TODO
		// for (ArrayList<EdgeElement> arr : ar.getReachableEdges()) {
		// for (EdgeElement ee : arr) {
		// if (!usedEdgesNodesIndex.contains(ee.getEdge().getNodeIndex()) && validateEdgeElement(ee)) {
		// GraphEdge ge = me.model().graphEdges().get(me.model().getWorldGraph().edgeIndexBetween(ee.getEdge().getNodeIndex(), ee.getEdge().getTwin().getNodeIndex()));
		// if (canAgentPassThrough(ee)) {
		// ge.setState(GraphEdgeState.Open);
		// } else {
		// ge.setState(GraphEdgeState.Block);
		// log().logln("%% BLOCKED " + ge + " between " + ar + " , " + me.model().areas().get(ee.getEdge().getTwin().getMyAreaIndex()));
		// }
		// usedEdgesNodesIndex.add(ee.getEdge().getNodeIndex());
		// usedEdgesNodesIndex.add(ee.getEdge().getTwin().getNodeIndex());
		// }
		// }
		// }
		//
		// }
	}

	// *************************************************************************/
	@SuppressWarnings("unused")
	private GraphEdgeState canAgentPassThrough(EdgeElement ee) {
		Road rd = (Road) me.model().areas().get(ee.getEdge().getTwin().getMyAreaIndex());
		for (ArrayList<EdgeElement> arr : rd.getReachableEdges()) {
			for (EdgeElement bb : arr) {
				if (ee.getEdge().getTwin().edgeEquals(bb.getEdge()) && validateEdgeElement(bb)) {
					int disEE = (int) GeometryTools2D.getDistance(ee.getStart(), ee.getEnd());
					int disBB = (int) GeometryTools2D.getDistance(bb.getStart(), bb.getEnd());
					EdgeElement longee, shortee;
					if (disEE > disBB) {
						longee = ee;
						shortee = bb;
					} else {
						longee = bb;
						shortee = ee;
						int temp = disEE;
						disEE = disBB;
						disBB = temp;
					}
					int X = (int) GeometryTools2D.getDistance(shortee.getStart(), longee.getStart());
					int Y = (int) GeometryTools2D.getDistance(shortee.getStart(), longee.getEnd());
					int X2 = (int) GeometryTools2D.getDistance(shortee.getEnd(), longee.getStart());
					int Y2 = (int) GeometryTools2D.getDistance(shortee.getEnd(), longee.getEnd());
					if (Math.abs(X + Y + X2 + Y2 - 2 * disEE) <= 15) {// shortee is completely inside longee
						if (disBB > 995)
							return GraphEdgeState.Open;
					}
					Point2D inside = null, outside = null;
					if (Math.abs(X + Y - disEE) <= 10 && Math.abs(X2 + Y2 - disEE) > 10) {
						inside = shortee.getStart();
						outside = shortee.getEnd();
					} else if (Math.abs(X + Y - disEE) > 10 && Math.abs(X2 + Y2 - disEE) <= 10) {
						inside = shortee.getEnd();
						outside = shortee.getStart();
					}
					if (inside != null && outside != null) {
						Point2D base = GeometryTools2D.getDistance(outside, longee.getStart()) > GeometryTools2D.getDistance(outside, longee.getEnd()) ? longee.getEnd() : longee.getStart();
						if (GeometryTools2D.getDistance(base, inside) > 995)
							return GraphEdgeState.Open;
					}
				}
			}
		}
		return GraphEdgeState.FoggyOpen;
	}

	// *************************************************************************/
	private boolean validateEdgeElement(EdgeElement ee) {
		if (!ee.getEdge().isPassable() || !(me.model().areas().get(ee.getEdge().getTwin().getMyAreaIndex()) instanceof Road))
			return false;
		if (!me.model().areas().get(ee.getEdge().getTwin().getMyAreaIndex()).hasBeenSeen())
			return false;
		if (ee.getEdge().getLine().getLength() - SOSGeometryTools.distance((int) ee.getStart().getX(), (int) ee.getStart().getY(), (int) ee.getEnd().getX(), (int) ee.getEnd().getY()) < 300)
			return false;
		return true;
	}

	// *************************************************************************/
	private void updateProperties(Entity entity, Set<Property> newProperties, int time) {
		for (Property p : newProperties) {
			entity.getProperty(p.getURN()).takeValue(p);
		}
		((StandardEntity) entity).setLastSenseTime(time);// set hasbeenSeen

	}

	// *************************************************************************
	public void handleBuildingChange(Building oldB, Building newB, Set<Property> properties, int time) {
		if (!oldB.isOnFire() && newB.isOnFire()) {// new fire building
			me.model().fieryBuildings().add(oldB);
			me.model().lastCycleSensedFieryBuildings().add(oldB);
		} else if (oldB.isOnFire() && !newB.isOnFire()) {
			me.model().fieryBuildings().remove(oldB);
		}
		//		System.out.println("Building "+oldB+"   t1 "+oldB.getTemperature()+"   t2"+newB.getTemperature());

		messageUpdater.senseBuildingChanged(oldB, newB, properties);

		//////////////////////////////////////////////////////////////////////////////////////////////////////
		updateProperties(oldB, properties, time); // update properties
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		updateVisualData(oldB, time);
	}

	private void updateVisualData(Building b, int time) {
		if (b.virtualData[0] != null) {
			boolean temp = b.virtualData[0].update();
			if (temp) {
				b.virtualData[0].reset(me.model().estimatedModel);
			}
		}
	}

	// *************************************************************************
	public void handleRoadChange(Road oldRd, Road newRd, Set<Property> properties, int time) {

		log().trace("oldRd.blockadeid=" + oldRd.getBlockadesID());
		log().trace("newRd.blockadeid=" + newRd.getBlockadesID());

		log().trace("before update oldRd.blockades=" + oldRd.getBlockades());
		log().trace("before update newRd.blockades=" + newRd.getBlockades());

		messageUpdater.senseRoadChange(oldRd, newRd, properties);

		if (!oldRd.isBlockadesDefined() && newRd.isBlockadesDefined()) {
			//			changedRoadsForReCalculateReachablity.remove(oldRd);
			changedRoadsForReCalculateReachablity.add(oldRd);
		}
		if (oldRd.isBlockadesDefined() && !(oldRd.getBlockadesID().containsAll(newRd.getBlockadesID()) && newRd.getBlockadesID().containsAll(oldRd.getBlockadesID()))) {
			for (EntityID a : oldRd.getBlockadesID()) {
				if (!newRd.getBlockadesID().contains(a)) {
					Blockade blockadeEntity = ((Blockade) me.model().getEntity(a));
					ArrayList<Road> rs = blockadeEntity.removed();
					for (Road r : rs) {
						if (r.isBlockadesDefined()) {
							//							changedRoadsForReCalculateReachablity.remove(r);
							changedRoadsForReCalculateReachablity.add(r);
						}
					}
					changedRoadsForReCalculateReachablity.add(oldRd);
					me.model().removeBlockade(blockadeEntity);
				}
			}
		}
		if (oldRd.isBlockadesDefined() && newRd.isBlockadesDefined()) {
			int oldCost = oldRoadCosts.get(oldRd), newCost = 0;
			if (oldCost > 0) {
				for (Blockade block : newRd.getBlockades())
					newCost += block==null?0:block.getRepairCost();

				if (newCost > oldCost) {
					log().debug(oldRd + " cause aftershock!oldCost:" + oldCost + " newCost:" + newCost + " oldblockades:" + oldRd.getBlockadesID() + " new blockades:" + newRd.getBlockadesID());
					me.model().setLastAfterShockTime(time);
					changedRoadsForReCalculateReachablity.add(oldRd);
				}
			}
		}
		//		if (oldRd.isBlockadesDefined() && newRd.isBlockadesDefined() && oldRd.getBlockadesID().size() != newRd.getBlockadesID().size()) {
		//			for (EntityID a : oldRd.getBlockadesID()) {
		//				if (!newRd.getBlockadesID().contains(a)) {
		//					Blockade blockadeEntity = ((Blockade) me.model().getEntity(a));
		//					ArrayList<Road> rs = blockadeEntity.removed();
		//					for (Road r : rs) {
		//						if (r.isBlockadesDefined()) {
		//							//							changedRoadsForReCalculateReachablity.remove(r);
		//							changedRoadsForReCalculateReachablity.add(r);
		//						}
		//					}
		//					me.model().removeBlockade(blockadeEntity);
		//				}
		//			}
		//		}
		me.model().block.removeMiddleBlockadesOfRoad(oldRd);

		//////////////////////////////////////////////////////////////////////////////////////////////
		updateProperties(oldRd, properties, time); // update properties
		//////////////////////////////////////////////////////////////////////////////////////////////
		log().trace("after update oldRd.blockades=" + oldRd.getBlockades());
		log().trace("after update newRd.blockades=" + newRd.getBlockades());
	}

	// *************************************************************************
	public void handleBlockadeChange(Blockade oldBk, Blockade newBk, Set<Property> properties, int time) {
		boolean isBlockadeRepairSizeChanged = (oldBk == null) || (oldBk.getRepairCost() != newBk.getRepairCost());
		//////////////////////////////////////////////////////////////////////////////////////////////
		if (oldBk != null)
			updateProperties(oldBk, properties, time); // update properties
		Blockade current = oldBk == null ? newBk : oldBk;
		//////////////////////////////////////////////////////////////////////////////////////////////
		me.model().block.removedBlockades().remove(current);
		if (isBlockadeRepairSizeChanged) {
			//			if (oldBk == null)
			current.setExpandedBlock(true);
			//			else
			//				current.setExpandedBlock(false);
			ArrayList<Road> rs = new ArrayList<Road>();
			rs.addAll(current.checkNeighborRoads());

			//			ArrayList<Road> rs = current.removed();
			//rs.addAll(current.removed());
			for (Road r : rs) {
				if (r.isBlockadesDefined()) {
					if (!changedRoadsForReCalculateReachablity.contains(r))
						changedRoadsForReCalculateReachablity.add(r);
				}
			}
			//			changedRoadsForReCalculateReachablity.removeAll(rs);
			//			changedRoadsForReCalculateReachablity.addAll(rs);
		}
	}

	// *************************************************************************
	public void handleHumanChange(Human oldHu, Human newHu, Set<Property> properties, int time) {
		int burriedness = newHu.isBuriednessDefined() ? newHu.getBuriedness() : 0;
		int hp = newHu.isHPDefined() ? newHu.getHP() : -1;
		if (hp >= SOSParticleFilter.getRealSensedValue(10000, HP_PRECISION)) {
			hp = 10000;
			newHu.setHP(hp);
		}

		int damage = newHu.isDamageDefined() ? newHu.getDamage() : -1;
		StandardEntity position = newHu.isPositionDefined() ? newHu.getPosition() : null;

		if (position != null && position instanceof Building) {

			//			me.model().searchWorldModel.getSearchBuilding((Building) position).setReallyUnReachableInLowCom(false);
			//			me.model().searchWorldModel.getSearchBuilding((Building) position).setHasBeenSeenBySelf(true);
			//			log().trace(position+" has been seen now");
			removeVirtualCivilians((Area) position);
			setSearchForCivilian((Building) position);
		}

		if (newHu.isBuriednessDefined() && newHu.getBuriedness() > 0 && newHu.getDamage() == 0) {
			damage = me.getConfig().getIntValue("perception.los.precision.damage") / 3;
		}
		try{
    		if(oldHu!=null	&&	oldHu.getID().getValue() != me.getID().getValue()){
    			if (oldHu != null) {
    				if (!(position != null && position.getAreaPosition() instanceof Refuge))
    					damage = Math.max(oldHu.getDamage(), damage);
    			}
    			if (!(position != null && position.getAreaPosition() instanceof Refuge))
    				damage = Math.max(SimpleDeathTime.getEstimatedDamage(hp, me.time()), damage);
    			newHu.setDamage(damage);
    		}
		}catch(Exception e){
			e.printStackTrace();
		}
		log().log("buried=" + burriedness + " hp=" + hp + " dmg=" + damage + " position id=" + newHu.getPositionID());
		log().logln("  pos" + position + "");

		messageUpdater.senseHumanChange(oldHu, newHu, properties);

		//////////////////////////////////////////////////////////////////////////////////////////////
		if (oldHu != null) {
			updateProperties(oldHu, properties, time); // update properties
			if (hp == 10000)
				oldHu.setHP(hp);
		}
		//////////////////////////////////////////////////////////////////////////////////////////////

		Human current = oldHu == null ? newHu : oldHu;
		current.setDamage(newHu.getDamage());

		//			if (!(oldObj instanceof Civilian)) {
		//				boolean isReallyReachable = !me.move.isReallyUnreachableXYPolice(oldObj.getAreaPosition().getPositionPair());
		//				oldObj.setIsReallyReachable(isReallyReachable);
		//			}
		//
		if (!(me instanceof PoliceForceAgent || me instanceof CenterAgent)) {///police handle itself because in some case it is not really reachable
			boolean oldReachablity = current.isReallyReachable(false);
			boolean newReachablity = current.getPosition().isReallyReachable(true);
			if (oldReachablity != newReachablity) {

			}
			if (current.getPosition() instanceof Building) {
				ArrayList<Civilian> civinb = ((Building) current.getPosition()).getCivilians();
				for (Civilian civilian : civinb) {
					civilian.isReallyReachable(true);
				}
			}
		}

		doHumanDeathTime(current);

		//			updateForAmbulance(current);

		if (me.me().equals(current)) {
			for (Building b : me.getVisibleEntities(Building.class)) {
				checkHasbeenSeenBySelf(b);
			}
		}

		if (current.equals(me.me())) {
			removeBuggySensibleAndSearchAreas();
		}
	}

	private void setSearchForCivilian(Building b) {
		if(!b.isSearchedForCivilian())
			messageUpdater.buildingIsSearchForCivilian(b);
		b.setSearchedForCivilian(me.time());
	}

	private void removeVirtualCivilians(Area position) {
		log().debug("Removing Virtual Civilians in " + position);
		for (Iterator<VirtualCivilian> iterator = me.model().getVirtualCivilians().iterator(); iterator.hasNext();) {
			VirtualCivilian vciv = iterator.next();
			if (vciv.getPosition().equals(position))
				iterator.remove();
		}
	}

	public void setPositionToRefuge(Human hum) {
		if (me.model().refuges().isEmpty())
			hum.setPosition(me.model().roads().get(0).getID());
		else
			hum.setPosition(me.model().refuges().get(0).getID());
	}

	public void doHumanDeathTime(Human hm) {
		hm.getRescueInfo().updateProperties();
	}

	//	private void updateForAmbulance(Human current) {
	//		if (me.me() instanceof AmbulanceTeam) {
	//			AmbulanceDecisionUtils.updateHuman(current, ((AmbulanceTeamAgent) me).getInfoModel());
	//
	//		} else if ((me instanceof CenterAgent) && ((CenterAgent) me).isWorkingAsAmbulanceCenter()) {
	//			AmbulanceDecisionUtils.updateHuman(current, (AbstractAmbulanceInfoModel) ((CenterAgent) me).getInfoModel());
	//		}
	//	}

	private void removeBuggySensibleAndSearchAreas() {
		Collection<Building> buildingInRange = me.model().getObjectsInRange(me.me(), me.VIEW_DISTANCE, Building.class);
		buildingInRange.removeAll(me.getVisibleEntities(Building.class));
		Point myPosition = me.me().getPositionPoint().toGeomPoint();
		for (Building target : buildingInRange) {
			ArrayList<ShapeInArea> searchAreas = target.getSearchAreas();
			for (int i = searchAreas.size() - 1; i > -1; i--) {
				if (searchAreas.get(i).contains(myPosition)) {
					//log("removeBuggySearchArea: Removed SearchArea " + searchAreas.get(i) + " of " + target);
					searchAreas.remove(i);
				}
			}
			if (searchAreas.isEmpty())
				searchAreas.add(new ShapeInArea(target.getApexes(), target));
			searchAreas = target.fireSearchBuilding().sensibleAreasOfAreas();
			for (int i = searchAreas.size() - 1; i > -1; i--) {
				if (searchAreas.get(i).contains(myPosition)) {
					//log("removeBuggySearchArea: Removed sensibleAreasOfAreas " + searchAreas.get(i) + " of " + target);
					searchAreas.remove(i);
				}
			}
			if (searchAreas.isEmpty())
				searchAreas.add(new ShapeInArea(target.getApexes(), target));
		}

	}

	// **********************************************************************************************************/
	/**
	 * update worldmodel by Message
	 * developed by Aramik
	 * 
	 * @param dynamicBitArray
	 */

	public void updateByMessage(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {

		if (sender.getID().getValue() == me.getID().getValue())
			return;
		msgUpdateLog.logln("time=" + me.time() + "\t--> Header:" + header + " Data:" + data + " From:" + sender + " Channel:" + channel);
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		if (header.equalsIgnoreCase(HEADER_DISABLE_FIRE_SITE)) {
			int index = data.get(DATA_BUILDING_INDEX);
			Building building = me.model().buildings().get(index);
			if (building.getEstimator() != null) {
				building.getEstimator().setDisable(true, me.time(),false);
				for (Building b : building.getEstimator().getAllBuildings()) {
					changedEntityForLog.add(building.getID());
					b.setLastMsgTime(me.time()-2);
					b.virtualData[0].disable();
				}
			}
			else
				log().warn("no fire site with this building to disable" + building);
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_IGNORED_TARGET)) {
			int id = data.get(DATA_ID);
			Human hu = (Human) me.model().getEntity(new EntityID(id));
			if (hu == null) { //sinash - added before IO 2013
				hu = new Civilian(new EntityID(id));
				me.model().addEntity(hu);
				hu.setLastMsgTime(me.time() - me.messageSystem.getNormalMessageDelay());
			}
			changedEntityForLog.add(hu.getID());
			hu.getRescueInfo().setIgnoredUntil(IgnoreReason.IgnoredTargetMessageReceived, 1000);
			log().logln("msg -> Header:" + header + " Data:" + data + " From:" + sender + " Channel:" + channel);
			if (channel.getChannelId() == 0)
				me.sosLogger.noComunication.debug("IGNORE TARGET RECEIVED..." + hu + " ignoreUntil:" + hu.getRescueInfo().getIgnoredUntil() + " " + hu.getRescueInfo().getIgnoreReason() + " " + hu.fullDescription() + " from:" + sender);
			//			logToAmbDecision("IGNORE_MESSAGE Received: Target: [" + hu.getID() + "] dt: " + hu.getRescueInfo().getDeathTime());
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_OPEN_ROAD)) {
			int index = data.get(DATA_ROAD_INDEX);
			Road road = me.model().roads().get(index);
			if (road.updatedtime() > me.time() - 2)
				return;
			road.setLastMsgTime(me.time() - 2);
			for (short ge_ind : road.getGraphEdges()) {
				GraphEdge ge = me.model().graphEdges().get(ge_ind);
				ge.setState(GraphEdgeState.Open);
				me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
			}
			clearAllBlockadeOfRoad(road);
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_FIRE)) {
			int time = data.get(DATA_TIME);
			Building bu = me.model().buildings().get(data.get(DATA_BUILDING_INDEX));
			if (time <= bu.updatedtime())
				return;
			if ((!bu.isFierynessDefined() || bu.getFieryness() == 0) && data.get(DATA_FIERYNESS) > 0 && data.get(DATA_FIERYNESS) != 4)
				me.model().lastCycleSensedFieryBuildings().add(bu);

			bu.setFieryness(data.get(DATA_FIERYNESS));
			if (bu.isOnFire())
				me.model().fieryBuildings().add(bu);
			else
				me.model().fieryBuildings().remove(bu);

			bu.setTemperature(data.get(DATA_HEAT) * 3);
			changedEntityForLog.add(bu.getID());
			bu.setLastMsgTime(time);
			updateVisualData(bu, time);
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_SENSED_CIVILIAN)) {
			int id = data.get(DATA_ID);
			Area pos = me.model().areas().get(data.get(DATA_AREA_INDEX));
			int hp = data.get(DATA_HP) * 322;
			int dmg = data.get(DATA_DAMAGE) * 10;
			Civilian civ = (Civilian) me.model().getEntity(new EntityID(id));
			if (civ == null) { // new civilian
				civ = new Civilian(new EntityID(id));
				me.model().addEntity(civ);
			}
			if (civ.updatedtime() >= data.get(DATA_TIME))
				return;
			civ.setBuriedness(data.get(DATA_BURIEDNESS));
			if (civ.getBuriedness() > 0 && dmg == 0)
				civ.setDamage(me.getConfig().getIntValue("perception.los.precision.damage") / 3);
			else
				civ.setDamage(dmg);
			boolean isReallyReachable = data.get(DATA_IS_REALLY_REACHABLE) == 1;
			civ.setIsReallyReachable(isReallyReachable);
			civ.setHP(hp);
			civ.setLastMsgTime(data.get(DATA_TIME));
			changedEntityForLog.add(civ.getID());
			civ.setPosition(pos.getID(), getRndpos(pos.getX(), 1000), getRndpos(pos.getY(), 1000));
			if (pos instanceof Building)
				((Building) pos).setSearchedForCivilian(data.get(DATA_TIME));
			doHumanDeathTime(civ);
		}
		else if (header.equalsIgnoreCase(HEADER_LOW_SENSED_AGENT)) {
			Area pos = me.model().areas().get(data.get(DATA_AREA_INDEX));
			Human hu = me.model().agents().get(data.get(DATA_AGENT_INDEX));
			if (hu.getID().getValue() == me.getID().getValue() || hu.updatedtime() >= me.time()-2)
				return;
			int lowBuriedness = data.get(DATA_LOW_BURIEDNESS);
			int buriedness;
			if(lowBuriedness==0)
				buriedness=0;
			else if(lowBuriedness==1)
				buriedness=20;
			else if(lowBuriedness==2)
				buriedness=40;
			else 
				buriedness=60;
			hu.setBuriedness(buriedness);
			hu.setLastMsgTime(me.time()-2);
			changedEntityForLog.add(hu.getID());
			hu.setPosition(pos.getID(), pos.getX(), pos.getY());
			////////////////////////////////////////////////////
			//			Collection<StandardEntity> vis = me.lineOfSightPerception.getVisibleEntities(hu);
			//			for (StandardEntity s : vis) {
			//				if (s instanceof Building) {
			//					if (!((Building) s).isFierynessDefined()) {
			//						((Building) s).setFieryness(0);
			//						((Building) s).setTemperature(0);
			//						((Building) s).setLastCycleUpdated(data.get(DATA_TIME));
			//						s.setLastMsgTime(data.get(DATA_TIME));
			//						vdUpdater.update((Building) s);
			//					}
			//
			//				}
			//			}
			////////////////////////////////////////////////////
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_SENSED_AGENT)) {
			Area pos = me.model().areas().get(data.get(DATA_AREA_INDEX));
			int hp = data.get(DATA_HP) * 322;//TODO changed 200 ::> 322
			int dmg = data.get(DATA_DAMAGE) * 10;
			Human hu = me.model().agents().get(data.get(DATA_AGENT_INDEX));
			if (hu.getID().getValue() == me.getID().getValue() || hu.updatedtime() >= data.get(DATA_TIME))
				return;
			hu.setBuriedness(data.get(DATA_BURIEDNESS));
			hu.setDamage(dmg);
			hu.setHP(hp);
			hu.setLastMsgTime(data.get(DATA_TIME));
			changedEntityForLog.add(hu.getID());
			hu.setPosition(pos.getID(), pos.getX(), pos.getY());
			if (hu.getBuriedness() == 0 && hu.getPosition() instanceof Building) {
				hu.getPosition().setIsReallyReachable(true);
				ArrayList<Civilian> civinb = ((Building) hu.getPosition()).getCivilians();
				for (Civilian civilian : civinb) {
					civilian.setIsReallyReachable(true);
				}
			}
			doHumanDeathTime(hu);
			////////////////////////////////////////////////////
			//			Collection<StandardEntity> vis = me.lineOfSightPerception.getVisibleEntities(hu);
			//			for (StandardEntity s : vis) {
			//				if (s instanceof Building) {
			//					if (!((Building) s).isFierynessDefined()) {
			//						((Building) s).setFieryness(0);
			//						((Building) s).setTemperature(0);
			//						((Building) s).setLastCycleUpdated(data.get(DATA_TIME));
			//						s.setLastMsgTime(data.get(DATA_TIME));
			//						vdUpdater.update((Building) s);
			//					}
			//
			//				}
			//			}
			////////////////////////////////////////////////////
		} else if (header.equalsIgnoreCase(HEADER_LOWCOM_FIRE)) {
			Building bu = me.model().buildings().get(data.get(DATA_BUILDING_INDEX));
			if (me.time() - bu.updatedtime() < 2)
				return;
			if ((!bu.isFierynessDefined() || bu.getFieryness() == 0) && data.get(DATA_FIERYNESS) > 0 && data.get(DATA_FIERYNESS) != 4) {
				me.model().lastCycleSensedFieryBuildings().add(bu);
			}
			bu.setFieryness(data.get(DATA_FIERYNESS));
			if (bu.isOnFire())
				me.model().fieryBuildings().add(bu);
			else
				me.model().fieryBuildings().remove(bu);
			if (bu.getFieryness() == 1)
				bu.setTemperature(70);
			else if (bu.getFieryness() == 2)
				bu.setTemperature(170);

			else if (bu.getFieryness() == 3)
				bu.setTemperature(200);
			else
				bu.setTemperature(20);

			bu.setLastMsgTime(me.model().time() - 2);
			changedEntityForLog.add(bu.getID());
			updateVisualData(bu, me.time() - 2);
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_POSITION)) {
			Area ar = me.model().areas().get(data.get(DATA_AREA_INDEX));
			Human ag = me.model().agents().get(data.get(DATA_AGENT_INDEX));
			if (ag.updatedtime() >= me.time() - 2)
				return;
			if (ar instanceof Building) {
				ag.setPosition(ar.getID(), ar.getX(), ar.getY());

				((Building) ar).setSearchedForCivilian(me.time() - 2);///TODO ADDED BY ALI
			} else {
				Road rd = (Road) ar;
				ag.setPosition(ar.getID(), (int) Math.round(rd.getPositionBase().getX() + data.get(DATA_X) * 10), (int) Math.round(rd.getPositionBase().getY() + data.get(DATA_Y) * 10));
			}
			changedEntityForLog.add(ag.getID());
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_DEAD_AGENT)) {
			Human ag = me.model().agents().get(data.get(DATA_AGENT_INDEX));
			if (ag.updatedtime() >= me.time() - 2)
				return;
			ag.setHP(0);
			changedEntityForLog.add(ag.getID());
		}

		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_FIRE_ZONE)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			int fiery = data.get(DATA_FIERYNESS);
			Building b = me.model().buildings().get(indx);
			b.setFieryness(fiery);
			if ((!b.isFierynessDefined() || b.getFieryness() == 0) && data.get(DATA_FIERYNESS) > 0 && data.get(DATA_FIERYNESS) != 4) {
				me.model().lastCycleSensedFieryBuildings().add(b);
			}
			changedEntityForLog.add(b.getID());
		}/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_IM_HEALTHY_AND_CAN_ACT)) {
			int agentindx = data.get(DATA_AGENT_INDEX);
			Human agent = me.model().agents().get(agentindx);
			if (!agent.isHPDefined())
				agent.setHP(10000);
			if (!agent.isBuriednessDefined() || agent.getBuriedness() != 0)
				agent.setBuriedness(0);
			if (!agent.isDamageDefined())
				agent.setDamage(0);
			changedEntityForLog.add(agent.getID());
		} else if (header.equalsIgnoreCase(HEADER_SEARCHED_FOR_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			Building b = me.model().buildings().get(indx);
			removeVirtualCivilians(b);
			b.setSearchedForCivilian(me.model().time() - 2);
			changedEntityForLog.add(b.getID());
		} else if (header.equalsIgnoreCase(HEADER_NO_COMM_SEARCHED_FOR_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			Building b = me.model().buildings().get(indx);
			b.setSearchedForCivilian(data.get(DATA_TIME));
			removeVirtualCivilians(b);
			changedEntityForLog.add(b.getID());
		} else if (header.equalsIgnoreCase(HEADER_LOWCOM_1_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			Building b = me.model().buildings().get(indx);
			boolean isReachable = data.get(DATA_IS_REALLY_REACHABLE) == 1;
			if(b instanceof Refuge){
				
				b.setIsReallyReachable(isReachable);
				return;
			}
			removeVirtualCivilians(b);
			int buriedness = (data.get(DATA_BURIEDNESS_LEVEL)) * 8;

			int deathTime = (data.get(DATA_DEATH_TIME_LOSSY1) + 1) * 8;
			VirtualCivilian vc = new VirtualCivilian(b, buriedness, deathTime, isReachable);
			addVirtualCivilian(vc);
//			changedEntityForLog.add(bu.getID());
		} else if (header.equalsIgnoreCase(HEADER_LOWCOM_2_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			int buriedness = (data.get(DATA_BURIEDNESS_LEVEL)) * 8;
			Building b = me.model().buildings().get(indx);
			removeVirtualCivilians(b);
			boolean isReachable = data.get(DATA_IS_REALLY_REACHABLE) == 1;

			int deathTime1 = (data.get(DATA_DEATH_TIME_LOSSY1) + 1) * 8;
			int deathTime2 = (data.get(DATA_DEATH_TIME_LOSSY2) + 1) * 8;
			VirtualCivilian vc = new VirtualCivilian(b, buriedness, deathTime1, isReachable);
			VirtualCivilian vc2 = new VirtualCivilian(b, buriedness, deathTime2, isReachable);
			addVirtualCivilian(vc);
			addVirtualCivilian(vc2);
		} else if (header.equalsIgnoreCase(HEADER_LOWCOM_MORE_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			int buriedness = (data.get(DATA_BURIEDNESS_LEVEL)) * 8;
			Building b = me.model().buildings().get(indx);
			removeVirtualCivilians(b);
			boolean isReachable = data.get(DATA_IS_REALLY_REACHABLE) == 1;
			int civCount = getCivilianCountLossyFromMessage(data.get(DATA_CIVILIAN_COUNT_LOSSY));

			int deathTime1 = (data.get(DATA_DEATH_TIME_LOSSY1) + 1) * 8;
			int deathTime2 = (data.get(DATA_DEATH_TIME_LOSSY2) + 1) * 8;
			int deathTime3 = (data.get(DATA_DEATH_TIME_LOSSY3) + 1) * 8;
			VirtualCivilian vc = new VirtualCivilian(b, buriedness, deathTime1, isReachable);
			VirtualCivilian vc3 = new VirtualCivilian(b, buriedness, deathTime3, isReachable);
			addVirtualCivilian(vc);
			addVirtualCivilian(vc3);
			for (int i = 0; i < civCount; i++) {
				VirtualCivilian vc2 = new VirtualCivilian(b, buriedness, deathTime2, isReachable);
				addVirtualCivilian(vc2);
			}
		} else if (header.equalsIgnoreCase(HEADER_LOWCOM_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			int validCivilianCount = data.get(DATA_VALID_CIVILIAN_COUNT);
			boolean isReallyUnReachable = data.get(DATA_IS_REALLY_UNREACHABLE) == 1 ? true : false;

			Building b = me.model().buildings().get(indx);
			b.setSearchedForCivilian(me.model().time() - 1);
			if (me instanceof PlatoonAgent<?>) {
				SearchBuilding searchB = me.model().searchWorldModel.getSearchBuilding(b);
				if (!searchB.isReallyUnReachableInLowCom(false))
					isReallyUnReachable = false;
				searchB.setValidCivilianCountInLowCom(validCivilianCount, isReallyUnReachable, me.time());
			}
			/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		} else if (header.equalsIgnoreCase(HEADER_ROAD_STATE)) {
			Road road = me.model().roads().get(data.get(MessageXmlConstant.DATA_ROAD_INDEX));
			if (road.updatedtime() >= me.time() - 2)
				return;
			if (road.isBlockadesDefined() && road.getBlockades().isEmpty())
				return;
			road.setLastMsgTime(me.time() - 2);
			
			for (int j = 0; j < road.getGraphEdges().length; j++) {
				GraphEdge ge = me.model().graphEdges().get(road.getGraphEdges()[j]);
				if (j < road.getWorldGraphEdgesSize()) {// check if it is worldgraph edge
					GraphEdgeState state = dynamicBitArray.get(j) ? GraphEdgeState.Block : GraphEdgeState.Open;
					if(ge.getState() == GraphEdgeState.Open )//To remove update delay bug... 
						continue;
					if (!(channel instanceof VoiceChannel && state == GraphEdgeState.Block))
						ge.setState(state);
				} else
					ge.setState(GraphEdgeState.Open);// it is visual graph edge
				if (ge.getState() == GraphEdgeState.Open)
					me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
			}
		} else if (header.equalsIgnoreCase(HEADER_AGENT_TO_EDGES_REACHABLITY_STATE)) {
			Road road = me.model().roads().get(data.get(MessageXmlConstant.DATA_ROAD_INDEX));
			Human humAgent = me.model().agents().get(data.get(MessageXmlConstant.DATA_AGENT_INDEX));
			for (int i = 0; i < dynamicBitArray.length(); i++) {
				if (dynamicBitArray.get(i))
					humAgent.addImReachableToEdge(road.getPassableEdges()[i]);

			}
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_AMBULANCE_INFO)) {
			ATstates state = AmbulanceUtils.convertStateIndexToState(data.get(DATA_AT_STATE));
			log().logln("Ambulance Info --> Header:" + header + " Data:" + data + " From:" + sender + " state=" + state);
			AmbulanceTeam at = me.model().ambulanceTeams().get(data.get(DATA_AMBULANCE_INDEX));
			if (me instanceof AmbulanceTeamAgent && at.getAmbIndex() == ((AmbulanceTeam) me.me()).getAmbIndex())
				return;
			if (state == ATstates.MOVE_TO_REFUGE && data.get(DATA_ID) != 0) { //data.get(DATA_ID)==0 --> means no target
				Human hmn = (Human) me.model().getEntity(new EntityID(data.get(DATA_ID)));
				if (hmn == null) {
					hmn = newHuman(data.get(DATA_ID), me.time() - me.messageSystem.getNormalMessageDelay(), 9000, 20, 20, null);
				}
				if (!me.model().refuges().isEmpty())
					hmn.setPosition(me.model().refuges().get(0).getID());
				else
					hmn.setPosition(me.model().roads().get(0).getID());
			}
		}
		// ****************************************************************
		else if (header.equalsIgnoreCase(HEADER_AMBULANCE_STATUS)) { // usage only in say
			if (me.time() - data.get(DATA_TIME) > 2)
				return;
			ATstates state = AmbulanceUtils.convertStateIndexToState(data.get(DATA_AT_STATE));
			log().logln("ambulance status --> Header:" + header + " Data:" + data + " From:" + sender + " Channel:" + channel);

			AmbulanceTeam at = me.model().ambulanceTeams().get(data.get(DATA_AMBULANCE_INDEX));
			if (me instanceof AmbulanceTeamAgent && at.getAmbIndex() == ((AmbulanceTeam) me.me()).getAmbIndex())
				return;
			if (data.get(DATA_ID) == 0)
				return;
			Area position = me.model().areas().get(data.get(DATA_AREA_INDEX));
			Human hmn = (Human) me.model().getEntity(new EntityID(data.get(DATA_ID)));
			if (hmn == null)
				hmn = newHuman(data.get(DATA_ID), me.time() - me.messageSystem.getNormalMessageDelay(), 9000, 20, 20, position);

			if (state == ATstates.MOVE_TO_REFUGE && data.get(DATA_ID) != 0) { //data.get(DATA_ID)==0 --> means no target
				if (!me.model().refuges().isEmpty())
					hmn.setPosition(me.model().refuges().get(0).getID());
				else
					hmn.setPosition(me.model().roads().get(0).getID());
			}
		}

	}

	private boolean isNoOrLow() {
		return me.messageSystem.type == MessageSystem.Type.NoComunication
				|| me.messageSystem.type == MessageSystem.Type.LowComunication;
	}

	private void addVirtualCivilian(VirtualCivilian vc) {
		if (vc.isReallyReachable())
			vc.getPosition().setIsReallyReachable(true);

		if (!(vc.getPosition() instanceof Building))
			return;

		if (vc.isReallyReachable())
			for (Civilian civ : ((Building) vc.getPosition()).getCivilians())
				civ.setIsReallyReachable(true);

		if (((Building) vc.getPosition()).isSearchedForCivilian())
			return;
		msgUpdateLog.debug(vc + " added");
		me.model().getVirtualCivilians().add(vc);
	}

	private int getCivilianCountLossyFromMessage(int size) {
		if (size == 0)
			return 3;
		if (size == 1)
			return 4;
		if (size == 2)
			return 7;
		return 10;
	}

	public void clearAllBlockadeOfRoad(Road road) {
		HashSet<Road> rs = new HashSet<Road>();
		rs.add(road);
		if (road.isBlockadesDefined()) {
			ArrayList<Blockade> blockOfRoad = new ArrayList<Blockade>(road.getBlockades());
			for (Blockade block : blockOfRoad) {
				if(block==null)
					continue;
				ArrayList<Road> r = block.removed();
				for (Road road2 : r) {
					if (road2.isBlockadesDefined())
						rs.add(road2);
				}
				me.model().removeBlockade(block);
			}

		}
		road.setBlockades(new ArrayList<EntityID>());
		road.getNeighborBlockades().clear();
		for (Road r : rs) {
			r.setReachablityChanges();
		}
	}

	//	// *************************************************************************/
	//	private void updateMsgRoads() {
	//		for (Road rd : changedRoadsReachabilityByMsg) {
	//			//			rd.setReachablityChanges();
	//			for (short indx : rd.getGraphEdges()) {
	//				GraphEdge ge = me.model().graphEdges().get(indx);
	//				Edge one = me.model().nodes().get(ge.getHeadIndex()).getRelatedEdge();
	//				Edge two = me.model().nodes().get(ge.getTailIndex()).getRelatedEdge();
	//				if (ge instanceof WorldGraphEdge) {
	//					ge.setState(Utils.convertReachabilityStatesToGraphEdgeStates(Reachablity.isReachable(rd, one, two)));
	//				} else {
	//					ge.setState(GraphEdgeState.Open);// TODO maybe add later
	//				}
	//				if (ge.getState() == GraphEdgeState.Open)
	//					me.move.getNodesUnion().setUnion(ge.getHeadIndex(), ge.getTailIndex());
	//			}
	//		}
	//		changedRoadsReachabilityByMsg.clear();
	//	}

	// for better view of civilian position in world model viewer (aramik)
	private int getRndpos(int base, int limit) {
		boolean sign = Math.random() > 0.5 ? true : false;
		if (sign)
			return ((int) (Math.random() * -10000) % limit) + base;
		else
			return ((int) (Math.random() * 10000) % limit) + base;
	}

	// **********************************************************************************************************/
	/**
	 * add new humanoid info getting from strategy msg
	 * 
	 * @param id
	 * @param time
	 * @param hp
	 * @param dmg
	 * @param bur
	 * @param mlpos
	 */
	public Human newHuman(int id, int time, int hp, int dmg, int bur, Area pos) {
		Human civ = new Civilian(new EntityID(id));
		me.model().addEntity(civ);
		civ.setBuriedness(bur);
		civ.setDamage(dmg);
		civ.setHP(hp);
		civ.setLastMsgTime(time);
		if (pos != null)
			civ.setPosition(pos.getID(), getRndpos(pos.getX(), 1000), getRndpos(pos.getY(), 1000));
		return civ;
	}

	/**
	 * logger
	 * 
	 * @return
	 */
	private SOSLoggerSystem log() {
		return me.sosLogger.worldModel;
	}

	// ---------------------------------------------------------------------------------------------------------//
	public static final class SortComparator implements java.util.Comparator<StandardEntity>, java.io.Serializable {
		private static final long serialVersionUID = -123456789123525L;

		@Override
		public int compare(StandardEntity ro1, StandardEntity ro2) {
			if (ro1 instanceof Human)
				if (ro1.getID().getValue() > ro2.getID().getValue())
					return 1;
			if (!(ro1 instanceof Human))
				if (ro1.getLocation().first() > ro2.getLocation().first() || ro1.getLocation().first() == ro2.getLocation().first() && ro1.getLocation().second() > ro2.getLocation().second())
					return 1;
			return -1;
		}
	}

	public static final class ChangesetComparator implements java.util.Comparator<EntityID>, java.io.Serializable {
		private static final long serialVersionUID = -123456789123525L;
		private final ChangeSet changeSet;
		private final SOSAgent<? extends StandardEntity> me;
		private final boolean loggingEnable;

		public ChangesetComparator(ChangeSet changeSet, SOSAgent<? extends StandardEntity> me, boolean loggingEnable) {
			this.changeSet = changeSet;
			this.me = me;
			this.loggingEnable = loggingEnable;

		}

		@Override
		public int compare(EntityID o1, EntityID o2) {
			StandardEntityURN o1URN = StandardEntityFactory.INSTANCE.getEnum(changeSet.getEntityURN(o1));
			StandardEntityURN o2URN = StandardEntityFactory.INSTANCE.getEnum(changeSet.getEntityURN(o2));
			int compare = compare(o1, o1URN, o2, o2URN);

			if (loggingEnable)
				me.sosLogger.worldModel.debug(o1 + ":" + o1URN + "****" + compare + "****" + o2 + ":" + o2URN);

			return compare;

		}

		private int compare(EntityID o1, StandardEntityURN o1URN, EntityID o2, StandardEntityURN o2URN) {
			if (o1.getValue() == o2.getValue())
				return 0;

			if (o1URN == StandardEntityURN.BLOCKADE && o2URN == StandardEntityURN.BLOCKADE)
				return 0;

			if (o1URN == StandardEntityURN.BLOCKADE)
				return -1;

			if (o2URN == StandardEntityURN.BLOCKADE)
				return 1;

			if (isArea(o1URN) && isArea(o2URN))
				return 0;

			if (isArea(o1URN))
				return -1;

			if (isArea(o2URN))
				return 1;

			if (o1.equals(me.me().getID()))
				return -1;

			if (o2.equals(me.me().getID()))
				return 1;

			//			if (o1URN == o2URN)
			//				return 0;

			return 0;

		}

		private boolean isArea(StandardEntityURN urn) {
			switch (urn) {

			case BUILDING:
			case AMBULANCE_CENTRE:
			case FIRE_STATION:
			case POLICE_OFFICE:
			case REFUGE:
			case ROAD:
			case HYDRANT:
			case GAS_STATION:
				return true;
			case BLOCKADE:
			case AMBULANCE_TEAM:
			case CIVILIAN:
			case FIRE_BRIGADE:
			case POLICE_FORCE:
			case WORLD:
			default:
				return false;
			}
		}

	}

	public static final class IDComparator implements java.util.Comparator<StandardEntity>, java.io.Serializable {
		private static final long serialVersionUID = -123456789123525L;

		@Override
		public int compare(StandardEntity ro1, StandardEntity ro2) {
			if (ro1.getID().getValue() > ro2.getID().getValue())
				return 1;
			else if (ro1.getID().getValue() < ro2.getID().getValue())
				return -1;
			return 0;
		}
	}
}
