package sos.base.util;

public class SOSActionException extends Exception {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	
	public SOSActionException(String action) {
		super("Act: " + action);
	}
	
}
