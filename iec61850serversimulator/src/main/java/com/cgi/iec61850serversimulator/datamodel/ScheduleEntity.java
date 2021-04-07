package com.cgi.iec61850serversimulator.datamodel;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {

    public static class ScheduleEntityBuilder {

        private int indexNumber;
        private int day;
        private int timeTypeOn;
        private int timeTypeOff;
        private LocalTime timeOn;
        private LocalTime timeOff;
        private int burningMinutes;
        private boolean enabled;
        private RelayEntity relayEntity;

        public ScheduleEntityBuilder(int scheduleNr, RelayEntity relayEntity) {
            this.indexNumber = scheduleNr;
            this.relayEntity = relayEntity;
        }

        public void setIndexNumber(int indexNumber) {
            this.indexNumber = indexNumber;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public void setTimeTypeOn(int timeTypeOn) {
            this.timeTypeOn = timeTypeOn;
        }

        public void setTimeTypeOff(int timeTypeOff) {
            this.timeTypeOff = timeTypeOff;
        }

        public void setTimeOn(LocalTime timeOn) {
            this.timeOn = timeOn;
        }

        public void setTimeOff(LocalTime timeOff) {
            this.timeOff = timeOff;
        }

        public void setBurningMinutes(int burningMinutes) {
            this.burningMinutes = burningMinutes;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setRelayEntity(RelayEntity relayEntity) {
            this.relayEntity = relayEntity;
        }

        public ScheduleEntity buildScheduleEntity() {
            ScheduleEntity scheduleEntity = new ScheduleEntity();
            scheduleEntity.indexNumber = this.indexNumber;
            scheduleEntity.day = this.day;
            scheduleEntity.timeTypeOn = this.timeTypeOn;
            scheduleEntity.timeTypeOff = this.timeTypeOff;
            scheduleEntity.timeOn = this.timeOn;
            scheduleEntity.timeOff = this.timeOff;
            scheduleEntity.burningMinutes = this.burningMinutes;
            scheduleEntity.enabled = this.enabled;
            scheduleEntity.relay = this.relayEntity;

            return scheduleEntity;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "indexNumber")
    private int indexNumber;

    // Convert to string later on?
    @Column(name = "day")
    private int day;

    @Column(name = "timeTypeOn")
    private int timeTypeOn;

    @Column(name = "timeTypeOff")
    private int timeTypeOff;

    @Column(name = "timeOn")
    private LocalTime timeOn;

    @Column(name = "timeOff")
    private LocalTime timeOff;

    @Column(name = "burningMinutes")
    private int burningMinutes;

    @Column(name = "enabled")
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    private RelayEntity relay;

    public ScheduleEntity() {

    }

    public ScheduleEntity(int indexNumber, int day, int timeTypeOn, int timeTypeOff, LocalTime timeOn,
            LocalTime timeOff, int burningMinutes, boolean enabled, RelayEntity relay) {
        this.indexNumber = indexNumber;
        this.day = day;
        this.timeTypeOn = timeTypeOn;
        this.timeTypeOff = timeTypeOff;
        this.timeOn = timeOn;
        this.timeOff = timeOff;
        this.burningMinutes = burningMinutes;
        this.enabled = enabled;
        this.relay = relay;
    }

    public int getIndexNumber() {
        return this.indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getTimeTypeOn() {
        return this.timeTypeOn;
    }

    public void setTimeTypeOn(int timeTypeOn) {
        this.timeTypeOn = timeTypeOn;
    }

    public int getTimeTypeOff() {
        return this.timeTypeOff;
    }

    public void setTimeTypeOff(int timeTypeOff) {
        this.timeTypeOff = timeTypeOff;
    }

    public LocalTime getTimeOn() {
        return this.timeOn;
    }

    public void setTimeOn(LocalTime timeOn) {
        this.timeOn = timeOn;
    }

    public LocalTime getTimeOff() {
        return this.timeOff;
    }

    public void setTimeOff(LocalTime timeOff) {
        this.timeOff = timeOff;
    }

    public int getBurningMinutes() {
        return this.burningMinutes;
    }

    public void setBurningMinutes(int burningMinutes) {
        this.burningMinutes = burningMinutes;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RelayEntity getRelay() {
        return this.relay;
    }

    public void setRelay(RelayEntity relay) {
        this.relay = relay;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RelayEntity [id=" + this.id + ", indexNumber=" + this.indexNumber + ", day=" + this.day
                + ", timeTypeOn=" + this.timeTypeOn + ", timeTypeOff=" + this.timeTypeOff + ", timeOn=" + this.timeOn
                + ", timeOff=" + this.timeOff + ", burningMinutes=" + this.burningMinutes + ", enabled=" + this.enabled
                + "]";

    }
}
