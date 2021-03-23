package com.cgi.iec61850serversimulator;

public class SwitchingMomentCalculationException extends Exception {

	/**
	 * Exception for when making Switching Moments out of schedules.
	 */
	private static final long serialVersionUID = 1L;

	public SwitchingMomentCalculationException(String message, Exception e) {
		super(message + System.lineSeparator() + e);
	}
}
