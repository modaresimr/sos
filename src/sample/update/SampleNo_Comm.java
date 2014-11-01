package sample.update;

import sos.base.SOSAgent;
import sos.base.SOSConstant.GraphEdgeState;
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
import sos.base.worldGraph.WorldGraphEdge;
import sos.tools.GraphEdge;

public class SampleNo_Comm implements MessageXmlConstant {
	private SOSAgent<? extends StandardEntity> me;

	public SampleNo_Comm(SOSAgent<? extends StandardEntity> me) {
		this.me = me;
	}


	private void chooseHumanMessages() {
		for (Human hm : me.model().humans()) {
			if (hm.isPositionDefined() && ((hm.isDamageDefined() && hm.getDamage() > 0) ||
					(hm.isBuriednessDefined() && hm.getBuriedness() > 0)) && hm.getHP() > 0 && !hm.getRescueInfo().isIgnored()) {
				if (hm instanceof Civilian) {
					me.messageBlock = new MessageBlock(HEADER_SENSED_CIVILIAN);
					me.messageBlock.addData(DATA_ID, hm.getID().getValue());
					me.messageBlock.addData(DATA_AREA_INDEX, hm.getPositionArea().getAreaIndex());
					me.messageBlock.addData(DATA_HP, hm.getHP() / 322);
					int damage = hm.getDamage();
					if (damage > 1200)
						damage = 1200;
					me.messageBlock.addData(DATA_DAMAGE, damage / 10);
					int buried = hm.getBuriedness();
					if (buried > 126)
						buried = 126;
					me.messageBlock.addData(DATA_BURIEDNESS, buried);
					me.messageBlock.addData(DATA_TIME, hm.updatedtime());
					boolean isReallyReachable = !me.move.isReallyUnreachableXYPolice(hm.getPositionArea().getPositionPair());
					me.messageBlock.addData(DATA_IS_REALLY_REACHABLE, isReallyReachable?1:0);
					me.sayMessages.add(me.messageBlock);
				} else {
					me.messageBlock = new MessageBlock(HEADER_SENSED_AGENT);
					me.messageBlock.addData(DATA_AGENT_INDEX, hm.getAgentIndex());
					me.messageBlock.addData(DATA_AREA_INDEX, hm.getPositionArea().getAreaIndex());
					me.messageBlock.addData(DATA_HP, hm.getHP() / 322);
					int damage = hm.getDamage();
					if (damage > 1200)
						damage = 1200;
					me.messageBlock.addData(DATA_DAMAGE, damage / 10);
					int buried = hm.getBuriedness();
					if (buried > 126)
						buried = 126;
					me.messageBlock.addData(DATA_BURIEDNESS, buried);
					me.messageBlock.addData(DATA_TIME, hm.updatedtime());
					me.sayMessages.add(me.messageBlock);
				}
			}
			if (hm.getRescueInfo().getIgnoredUntil() == 1000 || (hm.getHP() < 10000 && hm.getPositionArea() instanceof Refuge) || hm.getHP() == 0) {
				me.messageBlock = new MessageBlock(HEADER_IGNORED_TARGET);
				me.messageBlock.addData(DATA_ID, hm.getID().getValue());
				me.sayMessages.add(me.messageBlock);
			}
		}
	}

	private void chooseRoadMessages() {// TODO choose to spread it over time
		for (Road rd : me.model().roads()) {
			if (rd.updatedtime() <= 1)
				continue;
			boolean isAllOpen = true;
			for (Short ind : rd.getGraphEdges()) {
				GraphEdge ge = me.model().graphEdges().get(ind);
				if (ge instanceof WorldGraphEdge && ge.getState() != GraphEdgeState.Open) {
					isAllOpen = false;
					break;
				}
			}
			if (isAllOpen) {
				me.messageBlock = new MessageBlock(HEADER_OPEN_ROAD);
				me.messageBlock.addData(DATA_ROAD_INDEX, rd.getRoadIndex());
				me.sayMessages.add(me.messageBlock);
			} else {
				SOSBitArray states = new SOSBitArray(rd.getWorldGraphEdgesSize());
				for (int i = 0; i < rd.getWorldGraphEdgesSize(); i++) {
					states.set(i, me.model().graphEdges().get(rd.getGraphEdges()[i]).getState() == GraphEdgeState.Block);
				}
				me.messageBlock = new DynamicSizeMessageBlock(HEADER_ROAD_STATE, states);
				me.messageBlock.addData(DATA_ROAD_INDEX, rd.getRoadIndex());
				me.sayMessages.add(me.messageBlock);
			}
		}
	}


	private void chooseFireMessages() {
//		for (SOSFireZone fz : me.model().Fzones) {
//			if (fz.allenvironmentalFireyBuildings.size() > 0) {
//				Building b = fz.allenvironmentalFireyBuildings().get(0);
//				me.messageBlock = new MessageBlock(HEADER_FIRE_ZONE);
//				me.messageBlock.addData(DATA_BUILDING_INDEX, b.getIndex());
//				me.messageBlock.addData(DATA_FIERYNESS, b.getFieryness());
//				me.sayMessages.add(me.messageBlock);
//			}
//		}
		for (Building b : me.model().fieryBuildings()) {
			if (me.time() - b.updatedtime() < 10) {
				me.messageBlock = new MessageBlock(HEADER_FIRE);
				me.messageBlock.addData(DATA_BUILDING_INDEX, b.getBuildingIndex());
				me.messageBlock.addData(DATA_FIERYNESS, b.getFieryness());
				me.messageBlock.addData(DATA_HEAT, b.getTemperature() / 3);
				me.messageBlock.addData(DATA_TIME, b.updatedtime());
				me.sayMessages.add(me.messageBlock);
			}
		}
	}

	public void chooseAllMessages() {
		chooseHumanMessages();
		chooseFireMessages();
		chooseRoadMessages();
	}
}
