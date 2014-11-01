package sos.tools.decisionMaker.implementations.feedbacks;

import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;

/**
 * This feedback informs the state about errors in the result task.
 * 
 * @author Salim
 */
public class TaskWithErrorFeedback extends SOSFeedback {
	public enum ErrorType {
		TASK_WITH_NO_TARGET, NULL_TASK;
		@Override
		public String toString() {

			if (this.equals(TASK_WITH_NO_TARGET))
				return SOSFeedbackConstants.SOS_FEEDBACK_TASK_WITH_NO_TARGET;
			else if (this.equals(NULL_TASK))
				return SOSFeedbackConstants.SOS_FEEDBACK_NULL_TASK;
			return super.toString();

		}
	}

	public TaskWithErrorFeedback(ErrorType type) {
		super(type.toString());
	}

}
