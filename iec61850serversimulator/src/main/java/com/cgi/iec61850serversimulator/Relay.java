package com.cgi.iec61850serversimulator;

import java.util.Arrays;
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

		// ModelNode relayInfo =
		// serverModel.findModelNode("SWDeviceGenericIO/XSWC*.SwType.Oper".replace("*",
		// Integer.toString(this.indexNumber)), Fc.CO);
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
		/*
		 * try { logger.info("Set CTLModel for relay {} to value {}", this.indexNumber,
		 * 1);
		 * 
		 * final BdaBoolean lightModel = (BdaBoolean) this.serverWrapper
		 * .findModelNode("SWDeviceGenericIO/XSWC" + this.indexNumber +
		 * ".Pos.Oper.ctlVal", Fc.CO); // .findModelNode("SWDeviceGenericIO/XSWC" +
		 * relay + ".Pos.stVal", // Fc.ST); lightModel.setValue(light);
		 * 
		 * final List<BasicDataAttribute> attributes = Arrays.asList(lightModel);
		 * this.serverWrapper.setValues(attributes); } catch (final Exception e) {
		 * logger.error("Find node or set value failed", e); }
		 */
		// Initalize Schedules!
		this.initializeSchedules(scheduleInfo, this.indexNumber);
	}

	public Relay(int relayNr) {
		this.indexNumber = relayNr + 1;
	}

	public void displayRelay() {
		logger.info("**Printing relay " + Integer.toString(this.indexNumber) + "**");
		logger.info("Light status:  " + Boolean.toString(this.lightStatus) + "\n");
		// logger.info(schedules[0].toString());
	}

	public void initializeSchedules(ModelNode scheduleInfo, int relayNr) {
		/*
		 * for (int scheduleNumber = 1; scheduleNumber < 51; scheduleNumber++) { //
		 * Schedule initialization up to 50 String scheduleReference =
		 * "SWDeviceGenericIO/XSWC*.Sche.sche~".replace("*",
		 * Integer.toString(this.indexNumber)).replace("~",
		 * Integer.toString(scheduleNumber));
		 *
		 * return scheduleReference;
		 */
		this.schedules = new Schedule[50];
		for (int scheduleNr = 0; scheduleNr < 50; scheduleNr++) {
			this.schedules[scheduleNr] = new Schedule(scheduleInfo, scheduleNr, relayNr);
		}
		/*
		 * this.schedule2 = new Schedule();
		 * this.schedule2.initializeSchedule(scheduleInfo); this.schedule3 = new
		 * Schedule(); this.schedule3.initializeSchedule(scheduleInfo); this.schedule50
		 * = new Schedule(); this.schedule50.initializeSchedule(scheduleInfo);
		 */
	}
	// TODO: Initializing all 50 schedules to be accepted by the platform
	// Using a for-loop to get all 50 initialized per device?
	// Using .toString generator to get the information

	public Schedule getSchedule(int index) {
		return this.schedules[index - 1];
	}

	@Override
	public String toString() {
		return "Relay [indexNumber=" + this.indexNumber + ", lightStatus=" + this.lightStatus + ", scheduleInfo="
				+ this.scheduleInfo + ", schedules=" + Arrays.toString(this.schedules) + "]";
	}

	public void setLight(boolean light) {
		this.lightStatus = light;
	}

	public boolean getLight() {
		return this.lightStatus;
	}
	// Giving proper ModelNode per schedule by constructing the reference:
	// SWDeviceGenericIP/XSWC<indexNumber>.Sche.sche<scheduleNumber>
}