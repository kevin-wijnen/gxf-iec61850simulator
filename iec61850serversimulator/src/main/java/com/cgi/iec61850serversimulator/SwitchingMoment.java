package com.cgi.iec61850serversimulator;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which represents a switching moment (a scheduled action based on a list
 * of actions in a schedule). It is used for storing valid switching moments
 * before being processed into future tasks. A valid switching moment is a
 * scheduled switching moment where the difference in time between TimeOn and
 * TimeOff is greater than BurningMins.
 *
 * Periodic switching moments will have each their own individual switching
 * moments instances based on how many are needed within the calculated X of
 * hours.
 */

public class SwitchingMoment {

	// TODO Use the class for switching moment calculations and:
	// - have the objects in an array before conversion to relative time
	// - put said relative time to use when creating scheduled tasks (using
	// ScheduledExecutorService?)

	private static final Logger logger = LoggerFactory.getLogger(SwitchingMoment.class);

	private int relayNr;
	private LocalDateTime triggerTime;
	private boolean triggerAction;

}
