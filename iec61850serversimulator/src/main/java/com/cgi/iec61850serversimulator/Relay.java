package com.cgi.iec61850serversimulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.ModelNode;

class Relay {
	private static final Logger logger = LoggerFactory.getLogger(Relay.class);

	// Relay 1, 2, 3, 4
	int indexNumber;
	boolean lightStatus;
	ModelNode scheduleInfo;
	private Schedule[] schedules;
//	Schedule schedule1;
//	Schedule schedule2;
//	Schedule schedule3;
	// ..
//	Schedule schedule50;
	// filling up to 50 of them
	private ServerWrapper serverWrapper;

	public Relay(ModelNode relayInfo, ModelNode scheduleInfo) {

		this.indexNumber = Integer.parseInt(relayInfo.getReference().toString().substring(22, 23));
		this.scheduleInfo = scheduleInfo;
		List<BasicDataAttribute> bdas = relayInfo.getBasicDataAttributes();

		for (BasicDataAttribute bda : bdas) {
			String dataAttribute = bda.getName();

			switch (dataAttribute) {
			case "Oper.ctlVal":
				logger.info("Relay Light Status found.");
				this.lightStatus = ((BdaBoolean) bda).getValue();
				break;
			}
		}
		// Changing model to 1, enables actual switching with enbOpr on
		this.initializeSchedules(scheduleInfo, this.indexNumber);
	}

	public Relay(int relayNr) {
		this.indexNumber = relayNr + 1;
	}

	public void initializeSchedules(ModelNode scheduleInfo, int relayNr) {
		this.schedules = new Schedule[50];
		for (int scheduleNr = 0; scheduleNr < 50; scheduleNr++) {
			this.schedules[scheduleNr] = new Schedule(scheduleInfo, scheduleNr, relayNr);
		}
	}

	public Schedule getSchedule(int index) {
		return this.schedules[index - 1];
	}

	@Override
	public String toString() {
		final StringBuilder relayStringBuilder = new StringBuilder();
		relayStringBuilder.append("Relay " + this.indexNumber + "'s light status is " + this.lightStatus + "\n");
		return relayStringBuilder.toString();
	}

	public void setLight(boolean light) {
		this.lightStatus = light;
	}

	public boolean getLight() {
		return this.lightStatus;
	}
}