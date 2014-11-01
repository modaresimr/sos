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
import sos.base.message.MessageBuffer;
import sos.base.message.structure.MessageXmlConstant;

public abstract class SampleUpdateStrategy implements MessageXmlConstant{
	
	protected final SOSAbstractSampleAgent<?> me;
	public SampleUpdateStrategy(SOSAbstractSampleAgent<?> me) {
		this.me = me;
	}
	protected MessageBuffer messageBuffer(){
		return me.messages;
	}
	protected MessageBuffer lowCommunicationMessageBuffer(){
		return me.lowCommunicationMessages;
	}
	
	public abstract void updatingBySenseStart();
	public abstract void senseBuildingChanged(Building oldB, Building newB, Set<Property> properties) ;
	public abstract void senseRoadChange(Road oldRd, Road newRd, Set<Property> properties);
	public abstract void senseBlockadeChange(Blockade oldBk, Blockade newBk, TreeSet<Blockade> newlist, TreeSet<Blockade> changedList);
	public abstract void senseHumanChange(Human oldHu, Human newHu, Set<Property> properties);
	public abstract void senseRoadReachablityChanged(Area ar, boolean isAllOpen) ;

	public abstract void buildingIsSearchForCivilian(Building oldB);

	public abstract void updatingBySenseFinished(ChangeSet changeSet); 
}
