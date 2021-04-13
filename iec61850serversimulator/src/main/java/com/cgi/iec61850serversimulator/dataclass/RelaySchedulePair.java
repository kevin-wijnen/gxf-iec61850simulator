package com.cgi.iec61850serversimulator.dataclass;

public class RelaySchedulePair {
    /**
     * Class to store relay numbers and schedule numbers into a single object for
     * EventDataListener HashMap and general refactoring of code
     */

    private int relayNr;
    private int scheduleNr;

    public RelaySchedulePair(int relayNr, int scheduleNr) {
        this.relayNr = relayNr;
        this.scheduleNr = scheduleNr;
    }

    public int getRelayNr() {
        return this.relayNr;
    }

    public void setRelayNr(int relayNr) {
        this.relayNr = relayNr;
    }

    public int getScheduleNr() {
        return this.scheduleNr;
    }

    public void setScheduleNr(int scheduleNr) {
        this.scheduleNr = scheduleNr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.relayNr;
        result = prime * result + this.scheduleNr;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        RelaySchedulePair other = (RelaySchedulePair) obj;
        if (this.relayNr != other.relayNr)
            return false;
        if (this.scheduleNr != other.scheduleNr)
            return false;
        return true;
    }

}
