package sos.base.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Property;
import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.CenterAgent;
import sos.base.SOSAgent;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.util.Triple;
import sos.search_v2.worldModel.SearchBuilding;

/**
 * @author ali
 */
public class LowCommunicationStarategy extends UpdateStrategy {

	public LowCommunicationStarategy(SOSAgent<? extends StandardEntity> sosAgent) {
		super(sosAgent);
	}

	@Override
	public void updatingBySenseStart() {
		newSearchForCivilian.clear();
	}

	@Override
	public void senseBuildingChanged(Building oldB, Building newB, Set<Property> properties) {
		if (!oldB.isBurning() && newB.isBurning()) { // add SenseBuilding message
			MessageBlock messageBlock = new MessageBlock(HEADER_LOWCOM_FIRE);
			messageBlock.addData(DATA_BUILDING_INDEX, oldB.getBuildingIndex());
			messageBlock.addData(DATA_FIERYNESS, newB.getFieryness());
			lowCommunicationMessageBuffer().add(messageBlock);
		}
	}

	@Override
	public void senseRoadChange(Road oldRd, Road newRd, Set<Property> properties) {
	}

	@Override
	public void senseBlockadeChange(Blockade oldBk, Blockade newBk, TreeSet<Blockade> newlist, TreeSet<Blockade> changedList) {
	}

	@Override
	public void senseHumanChange(Human oldHu, Human newHu, Set<Property> properties) {
		StandardEntity position = newHu.isPositionDefined() ? newHu.getPosition() : null;
		int burriedness = newHu.isBuriednessDefined() ? newHu.getBuriedness() : 0;
		int hp = newHu.isHPDefined() ? newHu.getHP() : -1;
		int damage = newHu.isDamageDefined() ? newHu.getDamage() : -1;
		Human current = oldHu != null ? oldHu : newHu;
		if (newHu instanceof Civilian)
			return;
//		if (hp == 0 && current.getID().getValue() != me.getID().getValue()) {
//			MessageBlock messageBlock = new MessageBlock(HEADER_DEAD_AGENT);
//			messageBlock.addData(DATA_AGENT_INDEX, ((Human) me.me()).getAgentIndex());
//			lowCommunicationMessageBuffer().add(messageBlock);
//			return;
//		}
		if (current.getID().getValue() != me.getID().getValue())
			return;
//		if ((oldHu.getBuriedness() > 0 || me.time() == 3) && newHu.getBuriedness() == 0) {
//			MessageBlock messageBlock = new MessageBlock(HEADER_IM_HEALTHY_AND_CAN_ACT);
//			messageBlock.addData(DATA_AGENT_INDEX, ((Human) me.me()).getAgentIndex());
//			lowCommunicationMessageBuffer().add(messageBlock);
//			return;
//		}
		if (newHu.getBuriedness() == 0 
				&& (!(newHu.getPositionArea() instanceof Building
						&& (newHu instanceof AmbulanceTeam|| newHu instanceof FireBrigade)
						&& (me.model().getLastAfterShockTime()>=me.time()-10))))//TODO: ager bad az aftershock ye bar too road boode dige niaz nist befreste
			return;

		if (me.time() + current.getAgentIndex() % 10 == 0) {
			MessageBlock messageBlock = new MessageBlock(HEADER_LOW_SENSED_AGENT);
			messageBlock.addData(DATA_AGENT_INDEX, oldHu.getAgentIndex());
			messageBlock.addData(DATA_AREA_INDEX, ((Area) position).getAreaIndex());			
			int lowBury = 0;
			if (burriedness ==0)
				lowBury=0;
			else if(burriedness<25)
				lowBury=1;
			else  if(burriedness<45)
				lowBury=2;
			else 
				lowBury=3;//burriedness=60
			
			messageBlock.addData(DATA_LOW_BURIEDNESS, lowBury);
			
			lowCommunicationMessageBuffer().add(messageBlock);
		}

	}

	@Override
	public void senseRoadReachablityChanged(Area ar, boolean isAllOpen) {

	}

	HashSet<Building> newSearchForCivilian = new HashSet<Building>();

	@Override
	public void buildingIsSearchForCivilian(Building oldB) {
		newSearchForCivilian.add(oldB);
	}

	@Override
	public void updatingBySenseFinished(ChangeSet changeSet) {
		try {
			sendLowCommCivilianMessages();
			sendSearchForCivilianBuildings();
			//			oldLowComm();
		} catch (Exception e) {
			me.sosLogger.worldModel.error(e);
		}
	}

	private void oldLowComm() {
		//			if (!(me instanceof PlatoonAgent<?>))
		//				return;
		ArrayList<Building> vb = me.getVisibleEntities(Building.class);
		for (Building oldB : vb) {
			if (oldB instanceof Refuge)
				continue;

			int countValidCivilianTillRescueAndBriedness = 0;
			SearchBuilding sb = me.model().searchWorldModel.getSearchBuilding(oldB);
			if (!sb.isHasBeenSeenBySelf() && sb.getValidCivilianCountInLowCom() == 0)
				continue;

			if (!sb.isReallyUnReachableInLowCom(false))
				continue;
			ArrayList<Civilian> civilInBuildings = oldB.getCivilians();
			for (Civilian civ : civilInBuildings) {
				if (civ.getDamage() == 0)
					continue;

				if (civ.getHP() == 0)
					continue;

				if (civ.getDamage() > 200)
					continue;

				if (SimpleDeathTime.getEasyLifeTime(civ.getHP(), civ.getDamage(), civ.updatedtime()) - me.time() < civ.getBuriedness())
					continue;

				countValidCivilianTillRescueAndBriedness++;
			}
			boolean isReallyUnreachable = me.move.isReallyUnreachableXYPolice(oldB.getPositionPair());

			me.sosLogger.agent.debug(oldB + " old reachablity=" + sb.isReallyUnReachableInLowCom(false) + " new reachablity:" + isReallyUnreachable + " validcivCount=" + countValidCivilianTillRescueAndBriedness + " oldvalid=" + sb.getValidCivilianCountInLowCom() + ", isnewSearchforcivilian=" + newSearchForCivilian.contains(oldB));
			if (sb.isReallyUnReachableInLowCom(false) != isReallyUnreachable
					|| (sb.getValidCivilianCountInLowCom() != countValidCivilianTillRescueAndBriedness)
					|| newSearchForCivilian.contains(oldB)) {
				me.sosLogger.agent.debug("==> new LowCom message has been added");
				MessageBlock messageBlock = new MessageBlock(HEADER_LOWCOM_CIVILIAN);
				messageBlock.addData(DATA_BUILDING_INDEX, oldB.getBuildingIndex());
				messageBlock.addData(DATA_VALID_CIVILIAN_COUNT, countValidCivilianTillRescueAndBriedness);
				messageBlock.addData(DATA_IS_REALLY_UNREACHABLE, isReallyUnreachable ? 1 : 0);
				lowCommunicationMessageBuffer().add(messageBlock);

			}
			sb.setValidCivilianCountInLowCom(countValidCivilianTillRescueAndBriedness, isReallyUnreachable, me.time());

		}
	}

	private void sendSearchForCivilianBuildings() {
		for (Building b : newSearchForCivilian) {
			MessageBlock mb = new MessageBlock(HEADER_SEARCHED_FOR_CIVILIAN);
			mb.addData(DATA_BUILDING_INDEX, b.getBuildingIndex());
			lowCommunicationMessageBuffer().add(mb);
		}
	}

	private void sendLowCommCivilianMessages() {

		HashMap<Building, ArrayList<Civilian>> area_civilians = getValidSensedCivilan();
		for (Building sense : area_civilians.keySet()) {
			newSearchForCivilian.remove(sense);
			ArrayList<Civilian> civilians = area_civilians.get(sense);
			boolean isReallyReachable = me instanceof CenterAgent ? false : me.move.isReallyReacahble(sense.getPositionPair());
			if (!isReallyReachable && (me.time() - civilians.get(0).getFoundTime()) % 10 == 0)
				continue;
			MessageBlock mb;
			switch (civilians.size()) {
			case 1:
				mb = new MessageBlock(HEADER_LOWCOM_1_CIVILIAN);
				mb.addData(DATA_DEATH_TIME_LOSSY1, Math.min(civilians.get(0).getRescueInfo().getDeathTime(), 1000) / 8);
				break;
			case 2:
				mb = new MessageBlock(HEADER_LOWCOM_2_CIVILIAN);
				mb.addData(DATA_DEATH_TIME_LOSSY1, Math.min(civilians.get(0).getRescueInfo().getDeathTime(), 1000) / 8);
				mb.addData(DATA_DEATH_TIME_LOSSY2, Math.min(civilians.get(1).getRescueInfo().getDeathTime(), 1000) / 8);
				break;
			default:
				mb = new MessageBlock(HEADER_LOWCOM_MORE_CIVILIAN);
				Triple<Integer, Integer, Integer> deathTimes = getDeathTimes(civilians);
				mb.addData(DATA_CIVILIAN_COUNT_LOSSY, getCivilianCountLossy(civilians.size()));
				mb.addData(DATA_DEATH_TIME_LOSSY1, Math.min(deathTimes.first(), 1000) / 8);
				mb.addData(DATA_DEATH_TIME_LOSSY2, Math.min(deathTimes.second(), 1000) / 8);
				mb.addData(DATA_DEATH_TIME_LOSSY3, Math.min(deathTimes.third(), 1000) / 8);
				break;
			}
			int buriedness = getMaxBuriedness(civilians);

			
			mb.addData(DATA_BUILDING_INDEX, sense.getBuildingIndex());
			mb.addData(DATA_BURIEDNESS_LEVEL, Math.min((buriedness + 7) / 8, 7));
			mb.addData(DATA_IS_REALLY_REACHABLE, isReallyReachable ? 1 : 0);
			//			mb.toBitArray(ChannelSystemType.Low);
			lowCommunicationMessageBuffer().add(mb);
		}
		ArrayList<Refuge> refuges = me.getVisibleEntities(Refuge.class);
		for (Refuge refuge : refuges) {
			boolean isReallyReachable = me instanceof CenterAgent ? false : me.move.isReallyReacahble(refuge.getPositionPair());
			if(!isReallyReachable)
				continue;
			MessageBlock mb = new MessageBlock(HEADER_LOWCOM_1_CIVILIAN);
			mb.addData(DATA_BUILDING_INDEX, refuge.getBuildingIndex());
			mb.addData(DATA_DEATH_TIME_LOSSY1, 0);
			mb.addData(DATA_BURIEDNESS_LEVEL, 0);
			mb.addData(DATA_IS_REALLY_REACHABLE, isReallyReachable ? 1 : 0);
			//			mb.toBitArray(ChannelSystemType.Low);
			lowCommunicationMessageBuffer().add(mb);
			
		}
	}

	private int getCivilianCountLossy(int size) {
		if (size == 3)
			return 0;
		if (size == 4)
			return 1;
		if (size <= 7)
			return 2;
		return 3;
	}

	private Triple<Integer, Integer, Integer> getDeathTimes(ArrayList<Civilian> civilians) {
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, sum = 0;
		for (Civilian civilian : civilians) {
			min = Math.min(min, civilian.getRescueInfo().getDeathTime());
			max = Math.max(min, civilian.getRescueInfo().getDeathTime());
			sum += civilian.getRescueInfo().getDeathTime();
		}
		return new Triple<Integer, Integer, Integer>(min, sum / civilians.size(), max);
	}

	private int getMaxBuriedness(ArrayList<Civilian> civilians) {
		int buriedness = 0;
		for (Civilian civilian : civilians) {
			buriedness = Math.max(buriedness, civilian.getBuriedness());
		}
		return buriedness;
	}

	private HashMap<Building, ArrayList<Civilian>> getValidSensedCivilan() {
		HashMap<Building, ArrayList<Civilian>> area_civilians = new HashMap<Building, ArrayList<Civilian>>();
		for (Civilian civ : me.getVisibleEntities(Civilian.class)) {
			if (civ.getDamage() == 0)//is alive
				continue;

			if (civ.getHP() == 0)//is dead
				continue;

			if (civ.getDamage() > 200)//have too damage to alive
				continue;

			if (civ.getPositionArea() instanceof Road || civ.getPositionArea() instanceof Refuge)//is in road or refuge
				continue;

			if (civ.getRescueInfo().getDeathTime() - me.time() < civ.getBuriedness() / 2)//will die soon
				continue;

			if (!area_civilians.containsKey(civ.getPositionArea()))
				area_civilians.put((Building) civ.getPositionArea(), new ArrayList<Civilian>());

			area_civilians.get(civ.getPositionArea()).add(civ);
		}
		return area_civilians;
	}

}
