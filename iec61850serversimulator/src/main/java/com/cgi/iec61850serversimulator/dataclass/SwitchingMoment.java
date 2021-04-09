package com.cgi.iec61850serversimulator.dataclass;

import java.time.LocalDateTime;

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
 * <ul>
 * <li>The destination relay's number</li>
 * <li>The date and time when to trigger the switching moment
 * (LocalDateTime)</li>
 * <li>The type of action what to trigger (On = true, Off = false)</li>
 * </ul>
 */

public class SwitchingMoment {

    // TODO Use the class for switching moment calculations and:
    // - have the objects in an array before conversion to relative time
    // - put said relative time to use when creating scheduled tasks (using
    // ScheduledExecutorService?)

    private int relayNr;
    private LocalDateTime triggerTime;
    private boolean triggerAction;

    public SwitchingMoment(int relayNr, LocalDateTime triggerTime, boolean triggerAction) {
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
