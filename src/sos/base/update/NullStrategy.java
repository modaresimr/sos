package sos.base.update;

import java.util.Set;
import java.util.TreeSet;

import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Property;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;

public class NullStrategy extends UpdateStrategy{

	public NullStrategy(SOSAgent<? extends StandardEntity> sosAgent) {
		super(sosAgent);
	}

	@Override
	public void updatingBySenseStart() {
		
	}

	@Override
	public void senseBuildingChanged(Building oldB, Building newB, Set<Property> properties) {
		
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
		
	}

}
