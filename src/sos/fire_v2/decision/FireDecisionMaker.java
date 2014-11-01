package sos.fire_v2.decision;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.fire_v2.decision.states.ExtinguishFire;
import sos.fire_v2.decision.states.ImHurtState;
import sos.fire_v2.decision.states.MoveToHydrant;
import sos.fire_v2.decision.states.MoveToRefuge;
import sos.tools.decisionMaker.implementations.stateBased.SOSStateBasedDecisionMaker;
import sos.tools.decisionMaker.implementations.stateBased.StateFeedbackFactory;

public class FireDecisionMaker extends SOSStateBasedDecisionMaker<FireInformationModel> {

	public String lastState = "Null";
	public String lastAct = "Null";

	public FireDecisionMaker(SOSAgent<? extends Human> agent, StateFeedbackFactory feedbackFactory) {
		super(agent, feedbackFactory, FireInformationModel.class);
	}

	@Override
	public void initiateStates() {
		getThinkStates().add(new ImHurtState(getInfoModel()));
		getThinkStates().add(new MoveToRefuge(getInfoModel()));
		getThinkStates().add(new MoveToHydrant(getInfoModel()));
		getThinkStates().add(new ExtinguishFire(getInfoModel(), getInfoModel().getFireZoneSelector()));
	}

	public FireInformationModel getInfoModel() {
		return infoModel;
	}

}
