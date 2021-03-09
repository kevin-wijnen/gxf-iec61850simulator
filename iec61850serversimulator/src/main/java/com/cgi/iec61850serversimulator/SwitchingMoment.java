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
 *
 * The data being stored in this class is:
 *
 * @formatter:off
 * The destination relay's number
 * The date and time when to trigger the switching moment (LocalDateTime)
 * The type of action what to trigger (On = true, Off = false)
 * @formatter:on
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

	SwitchingMoment(int relayNr, LocalDateTime triggerTime, boolean triggerAction) {
		this.relayNr = relayNr;
		this.triggerTime = triggerTime;
		this.triggerAction = triggerAction;
	}

	public int getRelayNr() {
		return this.relayNr;
	}

	public void setRelayNr(int relayNr) {
		this.relayNr = relayNr;
	}

	public LocalDateTime getTriggerTime() {
		return this.triggerTime;
	}

	public void setTriggerTime(LocalDateTime triggerTime) {
		this.triggerTime = triggerTime;
	}

	public boolean isTriggerAction() {
		return this.triggerAction;
	}

	public void setTriggerAction(boolean triggerAction) {
		this.triggerAction = triggerAction;
	}

}
