package sos.ambulance_v2.decision;

import sos.ambulance_v2.AmbulanceInformationModel;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.decision.states.CivilianSearchState;
import sos.ambulance_v2.decision.states.DeadState;
import sos.ambulance_v2.decision.states.IAmHurtState;
import sos.ambulance_v2.decision.states.IAmStuckState;
import sos.ambulance_v2.decision.states.LockInBlockadeState;
import sos.tools.decisionMaker.implementations.stateBased.SOSStateBasedDecisionMaker;
import sos.tools.decisionMaker.implementations.stateBased.StateFeedbackFactory;

public class AmbulanceDecisionMaker extends SOSStateBasedDecisionMaker<AmbulanceInformationModel> {


	public AmbulanceDecisionMaker(AmbulanceTeamAgent agent, StateFeedbackFactory feedbackFactory) {
		super(agent, feedbackFactory, AmbulanceInformationModel.class);
	}

	@Override
	public void initiateStates() {
		getThinkStates().add(new DeadState(infoModel));
		getThinkStates().add(new IAmHurtState(getInfoModel()));
		getThinkStates().add(new IAmStuckState(getInfoModel()));
		getThinkStates().add(new LockInBlockadeState(getInfoModel()));
		getThinkStates().add(new CivilianSearchState(getInfoModel()));
	}

	public AmbulanceInformationModel getInfoModel() {
		return infoModel;
	}
}