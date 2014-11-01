package sample.update;

import java.util.Set;
import java.util.TreeSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Property;
import sample.SOSAbstractSampleAgent;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.message.structure.blocks.MessageBlock;

public class SampleLowCommunicationStarategy extends SampleUpdateStrategy {

	public SampleLowCommunicationStarategy(SOSAbstractSampleAgent<?> me) {
		super(me);

	}

	@Override
	public void updatingBySenseStart() {
	}

	@Override
	public void senseBuildingChanged(Building oldB, Building newB, Set<Property> properties) {
		if ((!oldB.isFierynessDefined() && newB.getFieryness() > 0) || (oldB.isFierynessDefined() && oldB.getFieryness() == 0 && newB.getFieryness() > 0)) { // add SenseBuilding message
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

	}

	@Override
	public void senseRoadReachablityChanged(Area ar, boolean isAllOpen) {
	}

	@Override
	public void buildingIsSearchForCivilian(Building oldB) {
	}

	@Override
	public void updatingBySenseFinished(ChangeSet changeSet) {
		try {
//			ArrayList<Building> vb = me.getVisibleEntities(Building.class);
//			for (Building oldB : vb) {
//				if (oldB instanceof Refuge)
//					continue;
//				if (!oldB.isSearchedForCivilian())
//					continue;
//				int countValidCivilianTillRescueAndBriedness = 0;
//
//				ArrayList<Civilian> civilInBuildings = oldB.getCivilians();
//				for (Civilian civ : civilInBuildings) {
//					if (civ.getHP() == 0) {
//						continue;
//					}
//					if (civ.getDamage() > 200) {
//						continue;
//					}
//					if (SimpleDeathTime.getEasyLifeTime(civ.getHP(), civ.getDamage(), civ.updatedtime()) - me.time() < civ.getBuriedness() + 5) {
//						continue;
//					}
//					countValidCivilianTillRescueAndBriedness++;
//				}
//
//			}
		} catch (Exception e) {
			me.sosLogger.error(e);
		}
	}

}
