package com.cgi.iec61850serversimulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;

class Schedule {
	private static final Logger logger = LoggerFactory.getLogger(Schedule.class);
	//TODO: SwitchingMoment related attributes and functions here.
	
	// Enum as prescribed in the GXF documentation for SetSchedule
	enum DAY{
		EVERYDAY, WEEKDAY, WEEKEND, MONDAY, TUESDAY, 
		WEDNESDAY,THURSDAY, FRIDAY, SATURDAY, SUNDAY;
		// 0, -1, -2, 1, 2, 3, 4, 5, 6, 7
	}
	
	enum TIMETYPE{
		FIXED, SENSOR, ASTRONOMIC;
	}
	// serverModel.findModelNode("SWDeviceGenericIO/CSLC.Clock", Fc.CF)
	int indexNumber;
	boolean enabled;
	String description;
	DAY day;
	short timeOn; //(between 0 and 2359, then 100 % 59) -1 = no action
	TIMETYPE timeOnType;
	short timeOff; //(between 0 and 2359, then 100 % 59) -1 = no action
	TIMETYPE timeOffType;
	short burningMinsOn;
	short beforeOffset; // Maximum of 150?
	short afterOffset; // Maximum of 150?
	
	public void initializeSchedule(ModelNode scheduleInfo) {
	
		List<BasicDataAttribute> bdas = scheduleInfo.getBasicDataAttributes();
		
		for (BasicDataAttribute bda : bdas ) {
			String dataAttribute = bda.getName();
			
			switch(dataAttribute) {
			case "enable":
				logger.info("Schedule Enabled Status found.");
				
				break;
				
			case "day":
				logger.info("Day found.");
				break;
			
			case "tOn":
				logger.info("Time On found.");
				break;
				
			case "tOnT":
				logger.info("Time On Type found.");
				break;
				
			case "tOff":
				logger.info("Time Off found.");
				break;
				
			case "tOffT":
				logger.info("Time Off Type found.");
				break;
				
			case "minOnPer":
				logger.info("Burning Minutes found.");
				break;
				
			case "srBefWd":
				logger.info("Before Astrological Time Offset found.");
				break;
				
			case "srAftWd":
				logger.info("After Astrological Time Offset found.");
				break;
				
			case "Descr":
				logger.info(" found.");
				break;
				
			default:
				logger.info("Unimplemented value found: " + bda.getName() + ", skipped.");
			}
		}
	}
}
