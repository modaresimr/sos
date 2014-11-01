package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import rescuecore2.registry.Registry;
import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.Property;
import sample.update.SampleGoodCommStrategy;
import sample.update.SampleLowCommunicationStarategy;
import sample.update.SampleUpdateStrategy;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Center;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.search_v2.worldModel.SearchBuilding;

public class SampleUpdater implements MessageXmlConstant {
	private final SOSLoggerSystem msgUpdateLog;
	//	public TreeSet<Road> changedRoadsReachabilityBySense = new TreeSet<Road>(new IDComparator());
	private SampleUpdateStrategy messageUpdater;
	private boolean isBlockadeExistInMap = false;
	private SOSAbstractSampleAgent<?> me;

	public SampleUpdater(SOSAbstractSampleAgent<?> me) {
		this.me = me;
		msgUpdateLog = me.sosLogger;

		if (me.messageSystem.type == Type.LowComunication)
			messageUpdater = new SampleLowCommunicationStarategy(me);
		else
			messageUpdater = new SampleGoodCommStrategy(me);
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

		TreeSet<Area> changedAreas = new TreeSet<Area>(new IDComparator());// for updating graphEdge states
		ArrayList<EntityID> sortedChangedList = getSortedChangedList(changeSet);
		for (EntityID enID : sortedChangedList) {

			StandardEntity oldObj = me.model().getEntity(enID);
			StandardEntity newObj = (StandardEntity) Registry.getCurrentRegistry().createEntity(changeSet.getEntityURN(enID), enID);
			log().logln("-----------------------------------");
			if (oldObj != null)
				log().logln("old =" + (oldObj).fullDescription());
			//Ali			log().logln("new =" + (newObj).fullDescription());
			if (oldObj == null){
				me.model().addEntity(newObj);

			}

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
				handleHumanChange((Human) oldObj, (Human) newObj, changeSet.getChangedProperties(enID), time);
			} else
				log().warn("UNKOWN Entity" + newObj);

			if (oldObj != null) {
				log().logln("final =" + (oldObj).fullDescription());
			}
		}
		log().logln("changed Areas=" + changedAreas);
		// --------------------------------- changed Areas
		messageUpdater.updatingBySenseFinished(changeSet);
		log().logln("-----------------------------------" + time + " :got: " + (System.currentTimeMillis() - start) + "ms");
		checkNoBlockadeMap();
		noOrLowCommunictionActivities();

	}

//	private void updateAgentReachableEdges() {
//		if (me.me() instanceof Center)
//			return;
//		if (me.time() < me.FREEZE_TIME)
//			return;
//		ArrayList<Human> sensedHuman = me.getVisibleEntities(Human.class);
//		for (Human human : sensedHuman) {
//			if (human.getAreaPosition() instanceof Road) {
//				Road myPosition = (Road) human.getAreaPosition();
//				for (int i = 0; i < myPosition.getPassableEdges().length; i++) {
//					boolean reachablity = Reachablity.isReachableAgentToEdge(human, myPosition, myPosition.getPassableEdges()[i]) == ReachablityState.Open;
//					if (reachablity)
//						human.addImReachableToEdge(myPosition.getPassableEdges()[i]);
//				}
//			}else{
//				Area myPosition =  human.getAreaPosition();
//				for (int i = 0; i < myPosition.getPassableEdges().length; i++) {
//					human.addImReachableToEdge(myPosition.getPassableEdges()[i]);
//				}
//			}
//		}
//
//	}

	private void noOrLowCommunictionActivities() {
		if (me.messageSystem.type == Type.NoComunication || me.messageSystem.type == Type.LowComunication) {
			if (me.time() % 70 == 0) {
				for (Road road : me.model().roads()) {
					road.setBlockades(new ArrayList<EntityID>());
				}
			}
		}
	}

	private void checkNoBlockadeMap() {
		if ((me.me() instanceof Center || ((Human) me.me()).getBuriedness() == 0) && me.time() > (/* Math.random()*2+ */15/* baraye inke hame yehoo nakhan process konan */) && !isBlockadeExistInMap) {
			if (me.time() == 16)
				log().consoleInfo("No Blockade MAP!!!!");

			for (Road road : me.model().roads()) {
				road.setBlockades(new ArrayList<EntityID>());
			}
		}
	}

	private ArrayList<EntityID> getSortedChangedList(final ChangeSet changeSet) {
		ArrayList<EntityID> changed = new ArrayList<EntityID>(changeSet.getChangedEntities());
		Collections.sort(changed, new Comparator<EntityID>() {

			@Override
			public int compare(EntityID o1, EntityID o2) {
				StandardEntityURN o1URN = StandardEntityFactory.INSTANCE.getEnum(changeSet.getEntityURN(o1));
				StandardEntityURN o2URN = StandardEntityFactory.INSTANCE.getEnum(changeSet.getEntityURN(o2));

				if (o1URN == o2URN)
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

				return 0;

			}

			private boolean isArea(StandardEntityURN o1URN) {
				switch (o1URN) {

				case BUILDING:
				case AMBULANCE_CENTRE:
				case FIRE_STATION:
				case POLICE_OFFICE:
				case REFUGE:
				case ROAD:
				case BLOCKADE:
					return true;
				case AMBULANCE_TEAM:
				case CIVILIAN:
				case FIRE_BRIGADE:
				case POLICE_FORCE:
				case WORLD:
				default:
					return false;
				}
			}

		});
		return changed;
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
		//		System.out.println("Building "+oldB+"   t1 "+oldB.getTemperature()+"   t2"+newB.getTemperature());
//		oldB.changeTemperature(newB.getTemperature());
		messageUpdater.senseBuildingChanged(oldB, newB, properties);

		//////////////////////////////////////////////////////////////////////////////////////////////////////
		updateProperties(oldB, properties, time); // update properties
		//////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	// *************************************************************************
	public void handleRoadChange(Road oldRd, Road newRd, Set<Property> properties, int time) {

		messageUpdater.senseRoadChange(oldRd, newRd, properties);

		if (oldRd.isBlockadesDefined() && newRd.isBlockadesDefined() && oldRd.getBlockadesID().size() > newRd.getBlockadesID().size()) {
			for (EntityID a : oldRd.getBlockadesID()) {
				if (!newRd.getBlockadesID().contains(a))
					me.model().removeBlockade((Blockade) me.model().getEntity(a));
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		updateProperties(oldRd, properties, time); // update properties
		//////////////////////////////////////////////////////////////////////////////////////////////
	}

	// *************************************************************************
	public void handleBlockadeChange(Blockade oldBk, Blockade newBk, Set<Property> properties, int time) {
		boolean isBlockadeRepairSizeChanged = (oldBk == null) || (oldBk.getRepairCost() != newBk.getRepairCost());
		//////////////////////////////////////////////////////////////////////////////////////////////
		if (oldBk != null)
			updateProperties(oldBk, properties, time); // update properties
		Blockade current = oldBk == null ? newBk : oldBk;
		//////////////////////////////////////////////////////////////////////////////////////////////

		if (isBlockadeRepairSizeChanged)
			current.setExpandedBlock(true);
	}

	// *************************************************************************
	public void handleHumanChange(Human oldHu, Human newHu, Set<Property> properties, int time) {
		int burriedness = newHu.isBuriednessDefined() ? newHu.getBuriedness() : 0;
		int hp = newHu.isHPDefined() ? newHu.getHP() : -1;
		int damage = newHu.isDamageDefined() ? newHu.getDamage() : -1;

		if (newHu.isBuriednessDefined() && newHu.getBuriedness() > 0 && newHu.getDamage() == 0) {
			damage = me.getConfig().getIntValue("perception.los.precision.damage") / 3;
		}
		if (oldHu != null)
			damage = Math.max(oldHu.getDamage(), damage);

		damage = Math.max(SimpleDeathTime.getEstimatedDamage(hp, me.time()), damage);
		newHu.setDamage(damage);

		log().log("buried=" + burriedness + " hp=" + hp + " dmg=" + damage + " position id=" + newHu.getPositionID());
		StandardEntity position = newHu.isPositionDefined() ? newHu.getPosition() : null;
		log().logln("  pos" + position + "");

		messageUpdater.senseHumanChange(oldHu, newHu, properties);

		//////////////////////////////////////////////////////////////////////////////////////////////
		if (oldHu != null)
			updateProperties(oldHu, properties, time); // update properties
		//////////////////////////////////////////////////////////////////////////////////////////////

		Human current = oldHu == null ? newHu : oldHu;
		current.setDamage(newHu.getDamage());

		//			if (!(oldObj instanceof Civilian)) {
		//				boolean isReallyReachable = !me.move.isReallyUnreachableXYPolice(oldObj.getAreaPosition().getPositionPair());
		//				oldObj.setIsReallyReachable(isReallyReachable);
		//			}

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
		if (header.equalsIgnoreCase(HEADER_OPEN_ROAD)) {
			int index = data.get(DATA_ROAD_INDEX);
			Road road = me.model().roads().get(index);
			if (road.updatedtime() >= me.time() - 2)
				return;
			road.setLastMsgTime(me.time() - 2);
			road.setBlockades(new ArrayList<EntityID>());

		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_FIRE)) {
			int time = data.get(DATA_TIME);
			Building bu = me.model().buildings().get(data.get(DATA_BUILDING_INDEX));
			if (time <= bu.updatedtime())
				return;
			bu.setFieryness(data.get(DATA_FIERYNESS));
			bu.setTemperature(data.get(DATA_HEAT) * 3);
			bu.setLastMsgTime(time);
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
			civ.setPosition(pos.getID(), getRndpos(pos.getX(), 200), getRndpos(pos.getY(), 200));
			if (pos instanceof Building)
				((Building) pos).setSearchedForCivilian(data.get(DATA_TIME));
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
			hu.setPosition(pos.getID(), pos.getX(), pos.getY());
		} else if (header.equalsIgnoreCase(HEADER_LOWCOM_FIRE)) {
			Building bu = me.model().buildings().get(data.get(DATA_BUILDING_INDEX));
			if (me.time() - bu.updatedtime() < 2)
				return;
			bu.setFieryness(data.get(DATA_FIERYNESS));
			if (bu.getFieryness() == 1)
				bu.setTemperature(70);
			else if (bu.getFieryness() == 2)
				bu.setTemperature(170);

			else if (bu.getFieryness() == 3)
				bu.setTemperature(200);
			else
				bu.setTemperature(20);

			bu.setLastMsgTime(me.model().time() - 2);

		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_POSITION)) {
			Area ar = me.model().areas().get(data.get(DATA_AREA_INDEX));
			Human ag = me.model().agents().get(data.get(DATA_AGENT_INDEX));
			if (ag.updatedtime() >= me.time() - 2)
				return;
			if (ar instanceof Building) {
				ag.setPosition(ar.getID(), ar.getX(), ar.getY());

				((Building) ar).setSearchedForCivilian(me.time()-2);///TODO ADDED BY ALI
			} else {
				Road rd = (Road) ar;
				ag.setPosition(ar.getID(), (int) Math.round(rd.getPositionBase().getX() + data.get(DATA_X) * 10), (int) Math.round(rd.getPositionBase().getY() + data.get(DATA_Y) * 10));
			}
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_DEAD_AGENT)) {
			Human ag = me.model().agents().get(data.get(DATA_AGENT_INDEX));
			if (ag.updatedtime() >= me.time() - 2)
				return;
			ag.setHP(0);
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_IGNORED_TARGET)) {
			int id = data.get(DATA_ID);
			Human hu = (Human) me.model().getEntity(new EntityID(id));
			if (hu != null) {
				hu.getRescueInfo().setIgnoredUntil(IgnoreReason.IgnoredTargetMessageReceived,1000);
			}
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		else if (header.equalsIgnoreCase(HEADER_FIRE_ZONE)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			int fiery = data.get(DATA_FIERYNESS);
			Building b = me.model().buildings().get(indx);
			b.setFieryness(fiery);
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
		} else if (header.equalsIgnoreCase(HEADER_SEARCHED_FOR_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			Building b = me.model().buildings().get(indx);
			b.setSearchedForCivilian(me.time()-2);
		} else if (header.equalsIgnoreCase(HEADER_NO_COMM_SEARCHED_FOR_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			Building b = me.model().buildings().get(indx);
			b.setSearchedForCivilian(data.get(DATA_TIME));
		} else if (header.equalsIgnoreCase(HEADER_LOWCOM_CIVILIAN)) {
			int indx = data.get(DATA_BUILDING_INDEX);
			int validCivilianCount = data.get(DATA_VALID_CIVILIAN_COUNT);
			boolean isReallyUnReachable = data.get(DATA_IS_REALLY_UNREACHABLE) == 1 ? true : false;
			Building b = me.model().buildings().get(indx);
			SearchBuilding searchB = me.model().searchWorldModel.getSearchBuilding(b);
			searchB.setValidCivilianCountInLowCom(validCivilianCount, isReallyUnReachable, me.time());
			/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
		} else if (header.equalsIgnoreCase(HEADER_ROAD_STATE)) {
			Road road = me.model().roads().get(data.get(MessageXmlConstant.DATA_ROAD_INDEX));
			if (road.updatedtime() >= me.time() - 2)
				return;
			if (road.isBlockadesDefined() && road.getBlockades().isEmpty())
				return;
			road.setLastMsgTime(me.time() - 2);
		} else if (header.equalsIgnoreCase(HEADER_AGENT_TO_EDGES_REACHABLITY_STATE)) {
			Road road = me.model().roads().get(data.get(MessageXmlConstant.DATA_ROAD_INDEX));
			Human humAgent = me.model().agents().get(data.get(MessageXmlConstant.DATA_AGENT_INDEX));
			for (int i = 0; i < dynamicBitArray.length(); i++) {
				if (dynamicBitArray.get(i))
					humAgent.addImReachableToEdge(road.getPassableEdges()[i]);

			}
		}
		/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	}

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
			civ.setPosition(pos.getID(), getRndpos(pos.getX(), 200), getRndpos(pos.getY(), 200));
		return civ;
	}

	/**
	 * logger
	 *
	 * @return
	 */
	private SOSLoggerSystem log() {
		return me.sosLogger;
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
