package com.cgi.iec61850serversimulator;

import java.util.List;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;

class Schedule {
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
	
	public void initializeSchedule(ServerModel serverModel, int indexNumber, int relayIndexNumber) {
		String scheduleReference = "SWDeviceGenericIO/XSWC*.Sche.sch~".replace("*", Integer.toString(relayIndexNumber)).replace("~", Integer.toString(indexNumber));
		ModelNode clockInfo = serverModel.findModelNode(scheduleReference, Fc.CF);
		List<BasicDataAttribute> bdas = clockInfo.getBasicDataAttributes();
	}
	
	
}
