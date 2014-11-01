package sample.update;

import java.util.Set;
import java.util.TreeSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Property;
import sample.SOSAbstractSampleAgent;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.util.SOSGeometryTools;

public class SampleGoodCommStrategy extends SampleUpdateStrategy {

	public SampleGoodCommStrategy(SOSAbstractSampleAgent<?> me) {
		super(me);

	}

	@Override
	public void updatingBySenseStart() {
	}

	@Override
	public void senseBuildingChanged(Building oldB, Building newB, Set<Property> properties) {
		if ((!oldB.isFierynessDefined() && newB.getFieryness() > 0) || (oldB.isFierynessDefined() && oldB.getFieryness() != newB.getFieryness()) || (oldB.isFierynessDefined() && oldB.getFieryness() == newB.getFieryness() && oldB.isTemperatureDefined() && (Math.abs(oldB.getTemperature() - newB.getTemperature()) > 10 || oldB.getTemperature() < 35 && newB.getTemperature() > 35)) || (!oldB.isFierynessDefined() && (newB.getFieryness() == 0 || newB.getFieryness() >= 4 && newB.getFieryness() <= 7) && newB.getTemperature() > 0)) { // add SenseBuilding message
			MessageBlock messageBlock = new MessageBlock(HEADER_FIRE);
			messageBlock.addData(DATA_BUILDING_INDEX, oldB.getBuildingIndex());
			messageBlock.addData(DATA_FIERYNESS, newB.getFieryness());
			messageBlock.addData(DATA_HEAT, ((newB.getTemperature() - 1) / 3) + 1);
			messageBlock.addData(DATA_TIME, oldB.updatedtime());
			messageBuffer().add(messageBlock);
			//Yoosef

			if ((oldB.isFierynessDefined() && oldB.getFieryness() != newB.getFieryness()) && oldB.isBurning() && newB.getFieryness() > 3) {
				me.sayMessages.add(messageBlock);
				//				System.out.println("say kardam " + oldB + "   " + oldB.getIndex());
			}

			//			else if(oldB.getFireSite()!=null && oldB.getFireSite().getSize()==SiteSize.Small)
			//			{	me.sayMessages.add(messageBlock);
			//
			//			}///	me.sayMessages.add(messageBlock);
		}
		//		else if (oldB.virtualData.isStateChanged(newB.getTemperature(), newB.getFieryness()))//added Yoosef
		//		{
		//			MessageBlock messageBlock = new MessageBlock(VIRTUAL_BUILDING);
		//			messageBlock.addData(DATA_BUILDING_INDEX, oldB.getIndex());
		//			messageBlock.addData(DATA_FIERYNESS, newB.getFieryness());
		//			messageBlock.addData(DATA_HEAT, newB.getTemperature() / 3);
		//			messageBlock.addData(DATA_TIME, me.model().time());
		//			messageBuffer().add(messageBlock);
		//			me.sayMessages.add(messageBlock);
		//		}
		if (!oldB.isBrokennessDefined() && newB.getBrokenness() == 0) {
			// MessageBlock messageBlock = new MessageBlock(HEADER_ZERO_BUILDING_BROKNESS);
			// messageBlock.addData(DATA_BUILDING_INDEX, oldB.getIndex());
			// messageBuffer().add(messageBlock);
		}

	}

	@Override
	public void senseRoadChange(Road oldRd, Road newRd, Set<Property> properties) {
//		if ((oldRd.updatedtime() < 2 || (!oldRd.isBlockadesDefined() || (oldRd.isBlockadesDefined() && !oldRd.getBlockades().isEmpty())))&& (newRd.isBlockadesDefined() && newRd.getBlockades().isEmpty())) {
//			MessageBlock messageBlock = new MessageBlock(HEADER_OPEN_ROAD);
//			messageBlock.addData(DATA_ROAD_INDEX, oldRd.getIndex());
//			messageBuffer().add(messageBlock);
//		}
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
		if ((newHu instanceof Civilian && position != null && !(position instanceof Refuge||position instanceof AmbulanceTeam))
				&& hp != -1 && damage != -1 && (burriedness > 0 || hp < 10000 || damage > 0)) {// Add Sense Civilian message
			MessageBlock messageBlock = new MessageBlock(HEADER_SENSED_CIVILIAN);
			messageBlock.addData(DATA_ID, newHu.getID().getValue());
			messageBlock.addData(DATA_AREA_INDEX, ((Area) position).getAreaIndex());
			messageBlock.addData(DATA_HP, hp / 322);
			if (damage > 1200)
				damage = 1200;
			messageBlock.addData(DATA_DAMAGE, (int) Math.max(1,Math.round(damage / 10d)));
			if (burriedness > 126)
				burriedness = 126;
			messageBlock.addData(DATA_BURIEDNESS, burriedness);
			messageBlock.addData(DATA_TIME, newHu.updatedtime());
			boolean isReallyReachable = false;
			messageBlock.addData(DATA_IS_REALLY_REACHABLE, isReallyReachable ? 1 : 0);
			messageBuffer().add(messageBlock);
		}
		if (!(newHu instanceof Civilian) && newHu.getBuriedness() > 0 && (newHu.getID().getValue() != me.getID().getValue() || me.time() % 3 == 0)) {
			MessageBlock messageBlock = new MessageBlock(HEADER_SENSED_AGENT);
			messageBlock.addData(DATA_AGENT_INDEX, oldHu.getAgentIndex());
			messageBlock.addData(DATA_AREA_INDEX, ((Area) position).getAreaIndex());
			messageBlock.addData(DATA_HP, hp / 322);//TODO change 200::>322
			if (damage > 1200)
				damage = 1200;
			messageBlock.addData(DATA_DAMAGE, (int) Math.max(1,Math.round(damage / 10d)));
			if (burriedness > 126)
				burriedness = 126;
			messageBlock.addData(DATA_BURIEDNESS, burriedness);
			messageBlock.addData(DATA_TIME, newHu.updatedtime());
			messageBuffer().add(messageBlock);
		}
		if (newHu.getID().getValue() == me.me().getID().getValue() && position != null && SOSGeometryTools.distance(newHu.getX(), newHu.getY(), oldHu.getX(), oldHu.getY()) > 1500) {
			MessageBlock messageBlock = new MessageBlock(HEADER_POSITION);
			messageBlock.addData(DATA_AGENT_INDEX, ((Human) me.me()).getAgentIndex());
			messageBlock.addData(DATA_AREA_INDEX, ((Area) position).getAreaIndex());
			if (position instanceof Road) {
				messageBlock.addData(DATA_X, (int) Math.round((newHu.getX() - ((Road) position).getPositionBase().getX()) / 10));
				messageBlock.addData(DATA_Y, (int) Math.round((newHu.getY() - ((Road) position).getPositionBase().getY()) / 10));
			} else {
				messageBlock.addData(DATA_X, 0);
				messageBlock.addData(DATA_Y, 0);
			}
			messageBuffer().add(messageBlock);
		}
		if (newHu.equals(me.me())/* TODO and i can act and not stock */&& newHu.getBuriedness() == 0 && newHu.getHP() > 500 && me.time() % 5 == 3) {
			MessageBlock messageBlock = new MessageBlock(HEADER_IM_HEALTHY_AND_CAN_ACT);
			messageBlock.addData(DATA_AGENT_INDEX, ((Human) me.me()).getAgentIndex());
			messageBuffer().add(messageBlock);
		}
	}

	@Override
	public void senseRoadReachablityChanged(Area ar, boolean isAllOpen) {
		if (isAllOpen) {
			MessageBlock messageBlock = new MessageBlock(HEADER_OPEN_ROAD);
			messageBlock.addData(DATA_ROAD_INDEX, ((Road) ar).getRoadIndex());
			messageBuffer().add(messageBlock);
		} else {
		}
	}

	@Override
	public void buildingIsSearchForCivilian(Building oldB) {
		if (oldB.isSearchedForCivilian()) {
			MessageBlock messageBlock = new MessageBlock(HEADER_SEARCHED_FOR_CIVILIAN);
			messageBlock.addData(DATA_BUILDING_INDEX, oldB.getBuildingIndex());
			messageBuffer().add(messageBlock);
		}
	}

	@Override
	public void updatingBySenseFinished(ChangeSet changeSet) {
	}

}
