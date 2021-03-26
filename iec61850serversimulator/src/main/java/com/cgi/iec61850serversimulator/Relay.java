package com.cgi.iec61850serversimulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;

/**
 * Class which represents a relay. It holds 50 schedules. While 64 schedules are
 * possible with IEC61850, the GXF platform only 50 of them. Hence why there are
 * only 50 schedules attached to every relay.
 */
class Relay {
	private static final Logger logger = LoggerFactory.getLogger(Relay.class);

	// Either 1, 2, 3 or 4
	int indexNumber;
	boolean lightStatus;
	ModelNode scheduleInfo;

	private Schedule[] schedules;

	private ServerWrapper serverWrapper;

	public Relay(ModelNode relayInfo, ModelNode scheduleInfo, ServerWrapper serverSapWrapper) {

		this.indexNumber = Integer.parseInt(relayInfo.getReference().toString().substring(22, 23));
		this.scheduleInfo = scheduleInfo;
		this.serverWrapper = serverSapWrapper;
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

	public Schedule[] getSchedules() {
		return this.schedules;
	}

	public void setSchedules(Schedule[] schedules) {
		this.schedules = schedules;
	}

	public void initializeSchedules(ModelNode scheduleInfo, int relayNr) {
		String SWITCH_ROOT = "SWDeviceGenericIO/XSWC";

		this.schedules = new Schedule[50];
		for (int scheduleNr = 0; scheduleNr < 50; scheduleNr++) {
			scheduleInfo = this.serverWrapper.findModelNode(SWITCH_ROOT + (relayNr) + ".Sche.sche" + (scheduleNr + 1),
					Fc.CF);
			this.schedules[scheduleNr] = new Schedule(scheduleInfo, scheduleNr, relayNr);
		}
	}

	public Schedule getSchedule(int index) {
		return this.schedules[index - 1];
	}

	@Override
	public String toString() {
		return "Relay " + this.indexNumber + "'s light status is " + this.lightStatus + "\n";
	}

	public void setLight(boolean light) {
		this.lightStatus = light;
	}

	public boolean getLight() {
		return this.lightStatus;
	}

	public int getIndexNumber() {
		return this.indexNumber;
	}

	public void setIndexNumber(int indexNumber) {
		this.indexNumber = indexNumber;
	}
}