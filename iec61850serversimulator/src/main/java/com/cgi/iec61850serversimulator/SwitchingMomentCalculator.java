package com.cgi.iec61850serversimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchingMomentCalculator implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculator.class);

	private Device device;

	public SwitchingMomentCalculator(final Device device) {
		this.device = device;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Calculating the switching moments by grabbing the schedules from the device's
		// relays

	}

}
