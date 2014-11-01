package sos.ambulance_v2.decision.states;

import java.util.List;

import sos.ambulance_v2.AmbulanceInformationModel;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.base.util.SOSGeometryTools;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;
import sos.tools.decisionMaker.implementations.tasks.RescueTask;

/* 
 * @author Salim,reyhaneh
 */
public class SampleState extends SOSIState<AmbulanceInformationModel> {

	public SampleState(AmbulanceInformationModel infoModel) {
		super(infoModel);
	}

	@Override
	public SOSTask<?> decide(SOSEventPool eventPool) throws SOSActionException {

		Civilian civ = getNearestCivilian();
		return new RescueTask(civ, infoModel.getTime());
	}

	private Civilian getNearestCivilian() {
		int bestDist = Integer.MAX_VALUE;
		Civilian best=null;
		for (Civilian civilian : infoModel.getAgent().model().civilians()) {
			if(civilian.isUnkonwnCivilian())
				continue;
			int dist = distance(infoModel.getATEntity(), civilian);
			if(dist<bestDist){
				best=civilian;
				bestDist=dist;
			}
		}		
		return best;
	}

	private int distance(Human a,Human b){
		return SOSGeometryTools.distance(a.getPositionPoint(), b.getPositionPoint());
	}
	
	@Override
	public void giveFeedbacks(List<SOSFeedback> feedbacks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skipped() {
		// TODO Auto-generated method stub

	}

	@Override
	public void overTaken() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleEvent(SOSEvent sosEvent) {

	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}