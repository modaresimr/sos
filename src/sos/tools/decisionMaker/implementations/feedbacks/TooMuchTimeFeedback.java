package sos.tools.decisionMaker.implementations.feedbacks;

import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;

/**
 * A simple feedback that tells the state it is using too much time
 * 
 * @author Salim
 */
public class TooMuchTimeFeedback extends SOSFeedback {

	public TooMuchTimeFeedback() {
		super(SOSFeedbackConstants.SOS_FEEDBACK_TOO_MUCH_TIME);
	}

}
