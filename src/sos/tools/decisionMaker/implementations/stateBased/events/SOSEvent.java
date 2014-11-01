package sos.tools.decisionMaker.implementations.stateBased.events;

public class SOSEvent {

	private String msg;

	public SOSEvent(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return msg;
	}
}
