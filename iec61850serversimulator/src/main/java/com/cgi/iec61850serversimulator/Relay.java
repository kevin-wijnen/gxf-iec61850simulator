package com.cgi.iec61850serversimulator;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;

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
	//..
//	Schedule schedule50;
	//filling up to 50 of them

	public Relay(ModelNode relayInfo, ModelNode scheduleInfo){
	
	this.indexNumber = Integer.parseInt(relayInfo.getReference().toString().substring(22,23));
	this.scheduleInfo = scheduleInfo;
	
	
	//ModelNode relayInfo = serverModel.findModelNode("SWDeviceGenericIO/XSWC*.SwType.Oper".replace("*", Integer.toString(this.indexNumber)), Fc.CO);
	List<BasicDataAttribute> bdas = relayInfo.getBasicDataAttributes();
	
	for (BasicDataAttribute bda : bdas ) {
		String dataAttribute = bda.getName();
		
		switch(dataAttribute) {
		case "Oper.ctlVal":
			logger.info("Relay Light Status found.");
			this.lightStatus = ((BdaBoolean) bda).getValue();
			break;
			}
		}
	// Initalize Schedules!
	initializeSchedules(scheduleInfo, indexNumber);
	}
	
	public Relay(int relayNr) {
		this.indexNumber = relayNr + 1;
	}
	
	public void displayRelay() {
		logger.info("**Printing relay " + Integer.toString(indexNumber) + "**");
		logger.info("Light status:  " + Boolean.toString(lightStatus) + "\n");
		//logger.info(schedules[0].toString());
	}
	
	public void initializeSchedules(ModelNode scheduleInfo, int relayNr) {
		/*for (int scheduleNumber = 1; scheduleNumber < 51; scheduleNumber++) {
			// Schedule initialization up to 50
			String scheduleReference = "SWDeviceGenericIO/XSWC*.Sche.sche~".replace("*", Integer.toString(this.indexNumber)).replace("~", Integer.toString(scheduleNumber));
			
			return scheduleReference;*/
		schedules = new Schedule[50];
		for (int scheduleNr = 0; scheduleNr < 50; scheduleNr++) {
			schedules[scheduleNr] = new Schedule(scheduleInfo, scheduleNr, relayNr);
		}
		/*this.schedule2 = new Schedule();
		this.schedule2.initializeSchedule(scheduleInfo);
		this.schedule3 = new Schedule();
		this.schedule3.initializeSchedule(scheduleInfo);
		this.schedule50 = new Schedule();
		this.schedule50.initializeSchedule(scheduleInfo);*/
		}
		//TODO: Initializing all 50 schedules to be accepted by the platform
		// Using a for-loop to get all 50 initialized per device?
		// Using .toString generator to get the information
	
	public Schedule getSchedule(int index) {
		return schedules[index - 1];
	}
	@Override
	public String toString() {
		return "Relay [indexNumber=" + indexNumber + ", lightStatus=" + lightStatus + ", scheduleInfo=" + scheduleInfo
				+ ", schedules=" + Arrays.toString(schedules) + "]";
	}
	
	public void setLight(boolean lightStatus) {
		this.lightStatus = lightStatus;
	}
	
	public boolean getLight() {
		return this.lightStatus;
	}
		
		// Giving proper ModelNode per schedule by constructing the reference:
		// SWDeviceGenericIP/XSWC<indexNumber>.Sche.sche<scheduleNumber>
	}