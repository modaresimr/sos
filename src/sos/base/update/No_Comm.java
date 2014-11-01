package sos.base.update;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import rescuecore2.misc.Pair;
import rescuecore2.standard.components.StandardAgent;
import sample.SOSAbstractSampleAgent;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.SOSAgent;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.message.MessageBuffer;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.SOSMessageList;
import sos.base.message.structure.blocks.DynamicSizeMessageBlock;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSRealFireZone;
import sos.base.worldGraph.WorldGraphEdge;
import sos.tools.GraphEdge;

/**
 * @author @ramik
 */
public class No_Comm implements MessageXmlConstant {
	private final StandardAgent<?> agent;
	private MessageBuffer sayMessages;

	public No_Comm(StandardAgent<?> agent) {
		this.agent = agent;
		if (agent instanceof SOSAgent<?>)
			sayMessages = ((SOSAgent<?>) agent).sayMessages;
		else if (agent instanceof SOSAbstractSampleAgent<?>)
			sayMessages = ((SOSAbstractSampleAgent<?>) agent).sayMessages;
	}

	private SOSWorldModel model() {
		return (SOSWorldModel) agent.model();
	}

	PriorityQueue<Human> validHumans = new PriorityQueue<Human>(50, new Comparator<Human>() {
		@Override
		public int compare(Human o1, Human o2) {
			return o2.updatedtime() - o1.updatedtime();
		}
	});

	private void chooseHumanMessages() {
		validHumans.clear();
		for (Human hm : agent.model().humans()) {

			if ((hm.isHPDefined() && hm.isPositionDefined())
					&& (hm.getRescueInfo().getIgnoredUntil() == 1000
							|| (hm.getHP() < 10000 && hm.getPositionArea() instanceof Refuge)
							|| hm.getHP() == 0)) {

				if (agent instanceof SOSAgent<?>) {
					SOSAgent<?> sosAgent = ((SOSAgent<?>) agent);
					sosAgent.sosLogger.noComunication.debug(hm + " ignoreUntil:" + hm.getRescueInfo().getIgnoredUntil() + " " + hm.getRescueInfo().getIgnoreReason() + " " + hm.fullDescription());
				}

				MessageBlock messageBlock = new MessageBlock(HEADER_IGNORED_TARGET);
				messageBlock.addData(DATA_ID, hm.getID().getValue());
				messageBlock.setPriority(8);

				sayMessages.add(messageBlock);
			} else if (hm.isPositionDefined() && ((hm.isDamageDefined() && hm.getDamage() > 0) ||
					(hm.isBuriednessDefined() && hm.getBuriedness() > 0)) && hm.getHP() > 0 && !hm.getRescueInfo().isIgnored()) {
				int deathTime;
				if (agent instanceof AmbulanceTeamAgent) {
					deathTime = hm.getRescueInfo().getDeathTime();
				} else
					deathTime = SimpleDeathTime.getEasyLifeTime(hm.getHP(), hm.getDamage(), hm.updatedtime());

				if (deathTime - hm.getBuriedness() / 2 > agent.model().time())
					validHumans.add(hm);

			}
		}
		//		Collections.sort(validHumans, new Comparator<Human>() {
		//			@Override
		//			public int compare(Human o1, Human o2) {
		//				return o2.updatedtime() - o1.updatedtime();
		//			}
		//		});
		int priority = 10;
		int count = 0;
		for (Human hm : validHumans) {
			count++;
			if (priority > 0 && count % 5 == 0)
				priority--;

			if (priority == 0)
				return;
			if (hm instanceof Civilian) {
				MessageBlock messageBlock = new MessageBlock(HEADER_SENSED_CIVILIAN);
				messageBlock.addData(DATA_ID, hm.getID().getValue());
				messageBlock.addData(DATA_AREA_INDEX, hm.getPositionArea().getAreaIndex());
				messageBlock.addData(DATA_HP, hm.getHP() / 322);
				int damage = hm.getDamage();
				if (damage > 1200)
					damage = 1200;
				messageBlock.addData(DATA_DAMAGE, damage / 10);
				int buried = hm.getBuriedness();
				if (buried > 126)
					buried = 126;
				messageBlock.addData(DATA_BURIEDNESS, buried);
				messageBlock.addData(DATA_TIME, hm.updatedtime());
				boolean isReallyReachable;
				if (agent instanceof SOSAgent<?>)
					isReallyReachable = !((SOSAgent<?>) agent).move.isReallyUnreachableXYPolice(hm.getPositionArea().getPositionPair());
				else
					isReallyReachable = false;

				messageBlock.addData(DATA_IS_REALLY_REACHABLE, isReallyReachable ? 1 : 0);
				messageBlock.setPriority(priority);
				sayMessages.add(messageBlock);
			} else {
				MessageBlock messageBlock = new MessageBlock(HEADER_SENSED_AGENT);
				messageBlock.addData(DATA_AGENT_INDEX, hm.getAgentIndex());
				messageBlock.addData(DATA_AREA_INDEX, hm.getPositionArea().getAreaIndex());
				messageBlock.addData(DATA_HP, hm.getHP() / 322);
				int damage = hm.getDamage();
				if (damage > 1200)
					damage = 1200;
				messageBlock.addData(DATA_DAMAGE, damage / 10);
				int buried = hm.getBuriedness();
				if (buried > 126)
					buried = 126;
				messageBlock.addData(DATA_BURIEDNESS, buried);
				messageBlock.addData(DATA_TIME, hm.updatedtime());
				messageBlock.setPriority(priority);
				sayMessages.add(messageBlock);
			}
		}
	}

	PriorityQueue<Road> roads = new PriorityQueue<Road>(50, new Comparator<Road>() {

		@Override
		public int compare(Road o1, Road o2) {
			return o2.updatedtime() - o1.updatedtime();
		}
	});

	private void chooseRoadMessages() {// TODO choose to spread it over time
		if (!(agent instanceof SOSAgent<?>))
			return;
		roads.clear();
		FOR: for (Road road : agent.model().roads()) {
			if (road.updatedtime() <= 1)
				continue;
			boolean haveAnOpenEdge = false;
			for (int j = 0; j < road.getGraphEdges().length; j++) {
				GraphEdge ge = model().graphEdges().get(road.getGraphEdges()[j]);
				//				if (ge.getState() == GraphEdgeState.FoggyBlock ||
				//						ge.getState() == GraphEdgeState.FoggyOpen)
				//					continue FOR;
				if (ge.getState() == GraphEdgeState.Open)
					haveAnOpenEdge = true;

			}
			if (haveAnOpenEdge)
				roads.add(road);

		}
		int priority = 6;
		int count = 0;
		SOSAgent<?> agent = (SOSAgent<?>) this.agent;
		for (Road rd : roads) {

			count++;
			if (priority > 0 && count % 5 == 0)
				priority--;
			if (priority == 0)
				return;
			boolean isAllOpen = true;
			for (Short ind : rd.getGraphEdges()) {
				GraphEdge ge = agent.model().graphEdges().get(ind);
				if (ge instanceof WorldGraphEdge && ge.getState() != GraphEdgeState.Open) {
					isAllOpen = false;
					break;
				}
			}
			if (isAllOpen) {
				MessageBlock messageBlock = new MessageBlock(HEADER_OPEN_ROAD);
				messageBlock.addData(DATA_ROAD_INDEX, rd.getRoadIndex());
				sayMessages.add(messageBlock);
			} else {
				SOSBitArray states = new SOSBitArray(rd.getWorldGraphEdgesSize());
				for (int i = 0; i < rd.getWorldGraphEdgesSize(); i++) {
					states.set(i, agent.model().graphEdges().get(rd.getGraphEdges()[i]).getState() != GraphEdgeState.Open);
				}
				MessageBlock messageBlock = new DynamicSizeMessageBlock(HEADER_ROAD_STATE, states);
				messageBlock.addData(DATA_ROAD_INDEX, rd.getRoadIndex());
				sayMessages.add(messageBlock);
			}
		}
	}

	PriorityQueue<Building> buildings = new PriorityQueue<Building>(100, new Comparator<Building>() {
		@Override
		public int compare(Building o1, Building o2) {

			if (o1.getFieryness() == o2.getFieryness())
				return o2.updatedtime() - o1.updatedtime();

			if (o1.getFieryness() >= 5 && o1.getFieryness() < 8 && o2.getFieryness() >= 5 && o2.getFieryness() < 8)
				return o2.updatedtime() - o1.updatedtime();

			if ((o1.getFieryness() == 3 || o1.getFieryness() == 8) && (o2.getFieryness() == 3 || o2.getFieryness() == 8))
				return o2.updatedtime() - o1.updatedtime();

			if (o1.getFieryness() == 1)
				return -1;
			if (o2.getFieryness() == 1)
				return 1;

			if (o1.getFieryness() == 2 || (o1.getFieryness() >= 5 && o1.getFieryness() < 8))
				return -1;
			if (o2.getFieryness() == 2 || (o2.getFieryness() >= 5 && o2.getFieryness() < 8))
				return 1;

			//			if (o1.getFieryness() == 2)
			//				return -1;
			//			if (o2.getFieryness() == 2)
			//				return 1;

			if (o1.getFieryness() == 3 || o1.getFieryness() == 8)
				return -1;
			if (o2.getFieryness() == 3 || o2.getFieryness() == 8)
				return 1;

			return 0;
		}

	});

	private void chooseFireMessages() {
		buildings.clear();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> fz : model().sosAgent().fireSiteManager.getFireSites()) {
			for (SOSRealFireZone firezone : fz.first()) {
				ArrayList<Building> outers = firezone.getOuter();
				if (outers.isEmpty())
					outers = firezone.getFires();
				if (!outers.isEmpty()) {
					Building b = outers.get(0);
					MessageBlock messageBlock = new MessageBlock(HEADER_FIRE);
					messageBlock.setPriority(11);
					messageBlock.addData(DATA_BUILDING_INDEX, b.getBuildingIndex());
					messageBlock.addData(DATA_FIERYNESS, b.getFieryness());
					messageBlock.addData(DATA_HEAT, b.getTemperature() / 3);
					messageBlock.addData(DATA_TIME, b.updatedtime());
					sayMessages.add(messageBlock);
				}
			}
		}
		try {

			for (Building sensed : model().sosAgent().getVisibleEntities(Building.class)) {
				if (sensed.isTemperatureDefined() && sensed.getTemperature() == 0) {
					Building b = sensed;
					MessageBlock messageBlock = new MessageBlock(HEADER_FIRE);
					messageBlock.setPriority(11);
					messageBlock.addData(DATA_BUILDING_INDEX, b.getBuildingIndex());
					messageBlock.addData(DATA_FIERYNESS, b.getFieryness());
					messageBlock.addData(DATA_HEAT, 0);
					messageBlock.addData(DATA_TIME, b.updatedtime());
					sayMessages.add(messageBlock);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Building b : agent.model().buildings()) {
			if (b.getFieryness() == 0 || b.getFieryness() == 4)
				continue;
			buildings.add(b);
		}
		int priority = 10;
		int count = 0;
		for (Building b : buildings) {

			count++;
			if (priority > 0 && count % 10 == 0)
				priority--;
			if (priority == 0)
				return;
			if (agent.model().time() - b.updatedtime() < 100) {
				MessageBlock messageBlock = new MessageBlock(HEADER_FIRE);
				messageBlock.addData(DATA_BUILDING_INDEX, b.getBuildingIndex());
				messageBlock.addData(DATA_FIERYNESS, b.getFieryness());
				messageBlock.addData(DATA_HEAT, b.getTemperature() / 3);
				messageBlock.addData(DATA_TIME, b.updatedtime());

				if (b.getSOSEstimateFireSite() != null && b.getSOSEstimateFireSite().shouldBeReported()) {
					sayMessages.add(messageBlock);
				} else if (agent.model().time() - b.updatedtime() < 30) {
					sayMessages.add(messageBlock);
				}
			}
		}
	}

	public void chooseAllMessages() {
		long t = System.currentTimeMillis();
		long t1 = System.currentTimeMillis();
		chooseHumanMessages();
		model().sosAgent().sosLogger.act.info(" choosing HumanMessages got:" + (System.currentTimeMillis() - t1) + "ms");
		t1 = System.currentTimeMillis();
		chooseFireMessages();
		model().sosAgent().sosLogger.act.info(" choosing FireMessages got:" + (System.currentTimeMillis() - t1) + "ms");
		t1 = System.currentTimeMillis();
		chooseRoadMessages();
		model().sosAgent().sosLogger.act.info(" choosing RoadMessages got:" + (System.currentTimeMillis() - t1) + "ms");
		t1 = System.currentTimeMillis();
		chooseSearchForCivilianBuildings();
		model().sosAgent().sosLogger.act.info(" choosing SearchForCivilian got:" + (System.currentTimeMillis() - t1) + "ms");
		t1 = System.currentTimeMillis();

		model().sosAgent().sosLogger.act.info(" choosing say messages got:" + (System.currentTimeMillis() - t) + "ms");
		t = System.currentTimeMillis();
		for (SOSMessageList<MessageBlock> messageList : sayMessages.getMessages().values()) {
			messageList.shuffle();
		}
		model().sosAgent().sosLogger.act.debug(" shuffeling say messages got:" + (System.currentTimeMillis() - t) + "ms");
	}

	HashSet<Building> searchedForCivilianBuildings = new HashSet<Building>();
	PriorityQueue<Building> searchbuildings = new PriorityQueue<Building>(100, new Comparator<Building>() {

		@Override
		public int compare(Building o1, Building o2) {
			if (!o1.isSearchedForCivilian() && !o2.isSearchedForCivilian())
				return 0;
			if (!o1.isSearchedForCivilian())
				return 1;
			if (!o2.isSearchedForCivilian())
				return -1;
			if (searchedForCivilianBuildings.contains(o1) != searchedForCivilianBuildings.contains(o2))
				return searchedForCivilianBuildings.contains(o1) ? 1 : -1;
			//				ClusterData myCluster = SOSAgent.currentAgent().getMyClusterData();
			//				if(myCluster.getBuildings().contains(o1)!=myCluster.getBuildings().contains(o2))
			//					return myCluster.getBuildings().contains(o1)?1:-1;
			//				if(o1.getLastSearchedForCivilianTime()>o2.getLastSearchedForCivilianTime())
			//					return -1;
			//				if(o1.getLastSearchedForCivilianTime()<o2.getLastSearchedForCivilianTime())
			//					return 1;
			double distanceTo1 = SOSAgent.currentAgent().me().getPositionPoint().distance(o1.getPositionPoint());
			double distanceTo2 = SOSAgent.currentAgent().me().getPositionPoint().distance(o2.getPositionPoint());
			if (distanceTo1 > distanceTo2)
				return 1;
			if (distanceTo1 < distanceTo2)
				return -1;
			return 0;

		}
	});

	private void chooseSearchForCivilianBuildings() {
		searchbuildings.clear();
		int priority = 10;
		int count = 0;
		for (Building b : model().buildings()) {
			if (b.isEitherFieryOrBurnt())
				continue;
			if (!b.isSearchedForCivilian())
				continue;
			searchbuildings.add(b);
		}
		for (Building b : searchbuildings) {
			count++;
			if (priority > 0 && count % 10 == 0)
				priority--;
			if (priority == 0)
				return;
			searchedForCivilianBuildings.add(b);
			MessageBlock messageBlock = new MessageBlock(HEADER_NO_COMM_SEARCHED_FOR_CIVILIAN);
			messageBlock.addData(DATA_BUILDING_INDEX, b.getBuildingIndex());
			messageBlock.addData(DATA_TIME, b.getLastSearchedForCivilianTime());

			sayMessages.add(messageBlock);

		}
	}
}
