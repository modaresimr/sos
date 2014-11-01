package sos.fire_v2.decision;

import java.util.ArrayList;
import java.util.List;

import sos.ambulance_v2.decision.states.IAmHurtState;
import sos.base.SOSAgent;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;
import sos.tools.decisionMaker.implementations.feedbacks.TaskWithErrorFeedback;
import sos.tools.decisionMaker.implementations.feedbacks.TaskWithErrorFeedback.ErrorType;
import sos.tools.decisionMaker.implementations.stateBased.StateFeedbackFactory;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;

public class FBFeedbackFactory extends StateFeedbackFactory {

	@Override
	public List<SOSFeedback> createFeedbacks(SOSAgent<?> agent, SOSIState state, SOSTask<?> task, long runTime) {
		List<SOSFeedback> feedbacks = new ArrayList<SOSFeedback>();
		if (state instanceof IAmHurtState && task == null) {
			feedbacks.add(new TaskWithErrorFeedback(ErrorType.NULL_TASK));
		}
		return feedbacks;
	}

}
