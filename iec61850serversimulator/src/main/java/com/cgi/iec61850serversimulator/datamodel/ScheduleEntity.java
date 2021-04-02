package com.cgi.iec61850serversimulator.datamodel;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "indexNumber")
    private int indexNumber;

    // Convert to string later on?
    @Column(name = "day")
    private int day;

    @Column(name = "timeType")
    private int timeType;

    @Column(name = "timeTypeOn")
    private int timeTypeOn;

    @Column(name = "timeTypeOff")
    private int timeTypeOff;

    @Column(name = "timeOn")
    private LocalDateTime timeOn;

    @Column(name = "timeOff")
    private LocalDateTime timeOff;

    @Column(name = "burningMinutes")
    private int burningMinutes;

    @Column(name = "enabled")
    private boolean enabled;

    public ScheduleEntity() {

    }

    public ScheduleEntity(int indexNumber, int day, int timeType, int timeTypeOn, int timeTypeOff, int timeOn,
            int timeOff, int burningMinutes, boolean enabled) {
        this.indexNumber = indexNumber;
        this.day = day;
        this.timeType = timeType;
        this.timeTypeOn = timeTypeOn;
        this.timeTypeOff = timeTypeOff;
        this.burningMinutes = burningMinutes;
        this.enabled = enabled;
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

    public int getTimeType() {
        return this.timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
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

    public LocalDateTime getTimeOn() {
        return this.timeOn;
    }

    public void setTimeOn(LocalDateTime timeOn) {
        this.timeOn = timeOn;
    }

    public LocalDateTime getTimeOff() {
        return this.timeOff;
    }

    public void setTimeOff(LocalDateTime timeOff) {
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

    @Override
    public String toString() {
        return "RelayEntity [id=" + this.id + ", indexNumber=" + this.indexNumber + ", day=" + this.day + ", timeType="
                + this.timeType + ", timeTypeOn=" + this.timeTypeOn + ", timeTypeOff=" + this.timeTypeOff + ", timeOn="
                + this.timeOn + ", timeOff=" + this.timeOff + ", burningMinutes=" + this.burningMinutes + ", enabled="
                + this.enabled + "]";

    }
}
