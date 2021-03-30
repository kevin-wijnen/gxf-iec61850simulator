package com.cgi.iec61850serversimulator.datamodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "relay")
public class RelayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "indexNumber")
    private int indexNumber;

    @Column(name = "lightStatus")
    private boolean lightStatus;

    public RelayEntity() {

    }

    public RelayEntity(int indexNumber, boolean lightStatus) {
        this.indexNumber = indexNumber;
        this.lightStatus = lightStatus;
    }

    public int getIndexNumber() {
        return this.indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public boolean isLightStatus() {
        return this.lightStatus;
    }

    public void setLightStatus(boolean lightStatus) {
        this.lightStatus = lightStatus;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "RelayEntity [id=" + this.id + ", indexNumber=" + this.indexNumber + ", lightStatus=" + this.lightStatus
                + "]";

    }
}
