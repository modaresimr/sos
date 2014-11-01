package sos.ambulance_v2.decision.states;

import java.util.List;

import sos.ambulance_v2.AmbulanceInformationModel;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.AmbulanceUtils;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;

/*
 * @author reyhaneh
 */
public class LockInBlockadeState extends SOSIState<AmbulanceInformationModel> {

	AmbulanceTeamAgent ambulance;
	public LockInBlockadeState(AmbulanceInformationModel infoModel) {
		super(infoModel);
		ambulance=infoModel.getAmbulance();
	}

	@Override
	public SOSTask<?> decide(SOSEventPool eventPool) throws SOSActionException {
		
		infoModel.getLog().info("$$$$$$$$$$$$$$$$$$$$$$ LockInBlockadeState $$$$$$$$$$$$$$$$$$$$$$$$$");
		
		if(!AmbulanceUtils.LockInBlockade(ambulance)){
			infoModel.getLog().info("$$$$$ Skipped from LockInBlockadeState $$$$$");
			return null;
		}
		if (infoModel.getATEntity().getWork() != null && infoModel.getATEntity().getWork().getTarget() != null) {
			Human target = infoModel.getATEntity().getWork().getTarget();
			if (target.getAreaPosition() != ambulance.me().getAreaPosition())
			{
				target.getRescueInfo().setIgnoredUntil(IgnoreReason.LockInBlockade, infoModel.getTime() + 10);
				AmbulanceUtils.rejectTargetInPosition(ambulance, target.getAreaPosition());
				AmbulanceUtils.rejectTarget(target, infoModel.getATEntity(), ambulance);
			}
		}
		ambulance.finishTasksState();
		return null;
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