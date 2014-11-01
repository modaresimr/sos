package sos.base.update;

import java.util.Set;
import java.util.TreeSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Property;
import sos.base.CenterAgent;
import sos.base.SOSAgent;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DynamicSizeMessageBlock;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.util.SOSGeometryTools;

/**
 * @author Ali
 */
public class GoodCommStrategy extends UpdateStrategy {

	public GoodCommStrategy(SOSAgent<? extends StandardEntity> sosAgent) {
		super(sosAgent);

	}

	long timePass = 0;

	@Override
	public void updatingBySenseStart() {
		timePass = 0;
	}

	@Override
	public void senseBuildingChanged(Building oldB, Building newB, Set<Property> properties) {
		long t1 = System.currentTimeMillis();
		if (isNeededForEstimator(newB, oldB) || (!oldB.isFierynessDefined() && newB.getFieryness() > 0) || (oldB.isFierynessDefined() && oldB.getFieryness() != newB.getFieryness()) || (oldB.isFierynessDefined() && oldB.getFieryness() == newB.getFieryness() && oldB.isTemperatureDefined() && (Math.abs(oldB.getTemperature() - newB.getTemperature()) > 10 || oldB.getTemperature() < 35 && newB.getTemperature() > 35)) || (!oldB.isFierynessDefined() && (newB.getFieryness() == 0 || newB.getFieryness() >= 4 && newB.getFieryness() <= 7) && newB.getTemperature() > 0)) { // add SenseBuilding message

			MessageBlock messageBlock = new MessageBlock(HEADER_FIRE);
			messageBlock.addData(DATA_BUILDING_INDEX, oldB.getBuildingIndex());
			messageBlock.addData(DATA_FIERYNESS, newB.getFieryness());
			int temp = newB.getTemperature() == 0 ? 0 : ((newB.getTemperature() - 1) / 3) + 1;
			messageBlock.addData(DATA_HEAT, temp);
			messageBlock.addData(DATA_TIME, newB.updatedtime());
			messageBuffer().add(messageBlock);
			//Yoosef

			if (!oldB.isTemperatureDefined() || (oldB.isFierynessDefined() && oldB.getFieryness() != newB.getFieryness()) && oldB.isBurning() && newB.getFieryness() > 3) {
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
		timePass += System.currentTimeMillis() - t1;
	}

	private boolean isNeededForEstimator(Building newB, Building oldB) {
		return (newB.isTemperatureDefined()
				&& newB.getTemperature() > 0
				&& (!oldB.isTemperatureDefined()
				|| (oldB.isTemperatureDefined()
				&& oldB.getTemperature() != newB.getTemperature()))
				&& oldB.getEstimator() == null)
				|| (oldB.isTemperatureDefined() && newB.isTemperatureDefined() && newB.getTemperature() < oldB.getTemperature());//TODO

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
		//TODO can we seprate update and sense HumanInfo
		long t1 = System.currentTimeMillis();
		StandardEntity position = newHu.isPositionDefined() ? newHu.getPosition() : null;
		int burriedness = newHu.isBuriednessDefined() ? newHu.getBuriedness() : 0;
		int hp = newHu.isHPDefined() ? newHu.getHP() : -1;
		int damage = newHu.isDamageDefined() ? newHu.getDamage() : -1;

		int oldburriedness = oldHu!=null&&oldHu.isBuriednessDefined() ? oldHu.getBuriedness() : 0;
		int oldhp = oldHu!=null&&oldHu.isHPDefined() ? oldHu.getHP() : -1;
		int olddamage = oldHu!=null&&oldHu.isDamageDefined() ? oldHu.getDamage() : -1;
		Human current=oldHu!=null?oldHu:newHu;
		if ((newHu instanceof Civilian && position != null && !(position instanceof Refuge || position instanceof AmbulanceTeam))
				&& hp != -1 && damage != -1
				&& (burriedness > 0 || hp < 10000 || damage > 0)
				) {// Add Sense Civilian message
			if(hp!=oldhp||oldburriedness!=burriedness||olddamage!=damage){

			MessageBlock messageBlock = new MessageBlock(HEADER_SENSED_CIVILIAN);
			messageBlock.addData(DATA_ID, current.getID().getValue());
			messageBlock.addData(DATA_AREA_INDEX, ((Area) position).getAreaIndex());
			messageBlock.addData(DATA_HP, hp / 322);
			if (damage > 1200)
				damage = 1200;  //TODO it's really huge
			messageBlock.addData(DATA_DAMAGE, (int) Math.max(1, Math.round(damage / 10d)));
			if (burriedness > 126)
				burriedness = 126;
			messageBlock.addData(DATA_BURIEDNESS, burriedness);
			messageBlock.addData(DATA_TIME, newHu.updatedtime());//TODO why????
			boolean isReallyReachable = me instanceof CenterAgent?false: !me.move.isReallyUnreachableXYPolice(position.getPositionPair());
			messageBlock.addData(DATA_IS_REALLY_REACHABLE, isReallyReachable ? 1 : 0);
			messageBuffer().add(messageBlock);
			}
		}
		if (!(newHu instanceof Civilian) && newHu.getBuriedness() > 0
				&& (current.getID().getValue() != me.getID().getValue() || me.time() % 3 == 0)) {
			MessageBlock messageBlock = new MessageBlock(HEADER_SENSED_AGENT);
			messageBlock.addData(DATA_AGENT_INDEX, oldHu.getAgentIndex());
			messageBlock.addData(DATA_AREA_INDEX, ((Area) position).getAreaIndex());
			messageBlock.addData(DATA_HP, hp / 322);//TODO changed 200 ::> 322
			if (damage > 1200)
				damage = 1200;
			messageBlock.addData(DATA_DAMAGE, (int) Math.max(1, Math.round(damage / 10d)));
			if (burriedness > 126)
				burriedness = 126;
			messageBlock.addData(DATA_BURIEDNESS, burriedness);
			messageBlock.addData(DATA_TIME, newHu.updatedtime());
			messageBuffer().add(messageBlock);
		}
		if (current.getID().getValue() == me.me().getID().getValue() && position != null && SOSGeometryTools.distance(newHu.getX(), newHu.getY(), oldHu.getX(), oldHu.getY()) > 1500) {
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
		if (oldHu!=null&&oldHu.equals(me.me())/* TODO and i can act and not stock */&& newHu.getBuriedness() == 0 && newHu.getHP() > 500 && me.time() % 5 == 3) {
			MessageBlock messageBlock = new MessageBlock(HEADER_IM_HEALTHY_AND_CAN_ACT);
			messageBlock.addData(DATA_AGENT_INDEX, ((Human) me.me()).getAgentIndex());
			messageBuffer().add(messageBlock);
		}
		timePass += System.currentTimeMillis() - t1;
	}

	@Override
	public void senseRoadReachablityChanged(Area ar, boolean isAllOpen) {
		long t1 = System.currentTimeMillis();
		if (isAllOpen) {
			MessageBlock messageBlock = new MessageBlock(HEADER_OPEN_ROAD);
			messageBlock.addData(DATA_ROAD_INDEX, ((Road) ar).getRoadIndex());
			messageBuffer().add(messageBlock);
		} else {
			SOSBitArray states = new SOSBitArray(((Road) ar).getWorldGraphEdgesSize());
			for (int i = 0; i < ((Road) ar).getWorldGraphEdgesSize(); i++) {
				states.set(i, me.model().graphEdges().get(ar.getGraphEdges()[i]).getState() == GraphEdgeState.Block);
			}
			DynamicSizeMessageBlock messageBlock = new DynamicSizeMessageBlock(HEADER_ROAD_STATE, states);
			messageBlock.addData(DATA_ROAD_INDEX, ((Road) ar).getRoadIndex());
			messageBuffer().add(messageBlock);
		}
		timePass += System.currentTimeMillis() - t1;
	}

	@Override
	public void buildingIsSearchForCivilian(Building oldB) {
		long t1 = System.currentTimeMillis();
		if (oldB.isSearchedForCivilian()) {
			MessageBlock messageBlock = new MessageBlock(HEADER_SEARCHED_FOR_CIVILIAN);
			messageBlock.addData(DATA_BUILDING_INDEX, oldB.getBuildingIndex());
			messageBuffer().add(messageBlock);
		}
		timePass += System.currentTimeMillis() - t1;
	}

	@Override
	public void updatingBySenseFinished(ChangeSet changeSet) {
		long t1 = System.currentTimeMillis();
		if (me.me() instanceof Human && me.me().getAreaPosition() instanceof Road) {
			Road myPosition = (Road) me.me().getAreaPosition();
			boolean haveAtLeastOneOpenEdge = false;
			SOSBitArray states = new SOSBitArray(myPosition.getPassableEdges().length);
			for (int i = 0; i < myPosition.getPassableEdges().length; i++) {
				boolean reachablity = Reachablity.isReachableAgentToEdge((Human) me.me(), myPosition, myPosition.getPassableEdges()[i]) == ReachablityState.Open;
				states.set(i, reachablity);
				haveAtLeastOneOpenEdge |= reachablity;
			}
			if (haveAtLeastOneOpenEdge) {
				DynamicSizeMessageBlock messageBlock = new DynamicSizeMessageBlock(HEADER_AGENT_TO_EDGES_REACHABLITY_STATE, states);
				messageBlock.addData(MessageXmlConstant.DATA_ROAD_INDEX, myPosition.getRoadIndex());
				messageBlock.addData(MessageXmlConstant.DATA_AGENT_INDEX, ((Human) me.me()).getAgentIndex());
				messageBuffer().add(messageBlock);
			}
		}
		timePass += System.currentTimeMillis() - t1;
		me.sosLogger.base.debug("prepareing for message got " + timePass + " ms");
	}

}
