package sos.ambulance_v2.decision.states;

import java.util.List;

import sos.ambulance_v2.AmbulanceInformationModel;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;
import sos.tools.decisionMaker.implementations.targets.EmptyTarget;
import sos.tools.decisionMaker.implementations.tasks.RestTask;

/*
 * @author reyhaneh
 */
public class DeadState extends SOSIState<AmbulanceInformationModel> {

	public DeadState(AmbulanceInformationModel infoModel) {
		super(infoModel);
	}

	@Override
	public SOSTask<?> decide(SOSEventPool eventPool) {
		
		infoModel.getLog().info("$$$$$$$$$$$$$$$$$$$$$$ DeadState $$$$$$$$$$$$$$$$$$$$$$$$$");
		
		if(!isValid()){
			infoModel.getLog().info("$$$$$ Skipped from DeadState $$$$$");
			return null;
		}
		return new RestTask(new EmptyTarget(),infoModel.getTime(), "Agent is dead.");
	}

	public boolean isValid() {

		if( ((AmbulanceTeamAgent)infoModel.getAgent()).me().getHP() == 0 )
			return true;
		
		return false;
	}
	
	@Override
	public void giveFeedbacks(List<SOSFeedback> feedbacks) {
		
	}

	@Override
	public void skipped() {
		
	}

	@Override
	public void overTaken() {
		
	}

	@Override
	protected void handleEvent(SOSEvent sosEvent) {
		
	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	

}