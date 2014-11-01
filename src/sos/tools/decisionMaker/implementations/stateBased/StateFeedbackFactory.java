package sos.tools.decisionMaker.implementations.stateBased;

import java.util.List;

import sos.base.SOSAgent;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;
import sos.tools.decisionMaker.definitions.feedback.SOSIFeedbackFactory;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;

/**
 * Feedback factory for state based decision maker
 * 
 * @author Salim
 */
public abstract class StateFeedbackFactory implements SOSIFeedbackFactory {
	/**
	 * creates feedbacks for task created for a specific state
	 * 
	 * @return
	 */
	public abstract List<SOSFeedback> createFeedbacks(SOSAgent<?> agent, SOSIState state, SOSTask<?> task, long runTime);

}
