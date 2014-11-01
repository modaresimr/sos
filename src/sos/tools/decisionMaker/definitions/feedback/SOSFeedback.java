package sos.tools.decisionMaker.definitions.feedback;

/**
 * This is abstract class for feedbacks. Feedbacks are used to inform states about their behaviors.
 * 
 * @author Salim
 */
public class SOSFeedback {
	/**
	 * Contains message of the feedback but its not always set. It can sometimes be null.
	 */
	private String msg;

	public SOSFeedback(String msg) {
		this.msg = msg;

	}

	/**
	 * Checks if the feedback is not null
	 * 
	 * @return
	 */
	public boolean hasMessage() {
		return msg != null;
	}

	public String getMessage() {
		return msg;
	}

}
