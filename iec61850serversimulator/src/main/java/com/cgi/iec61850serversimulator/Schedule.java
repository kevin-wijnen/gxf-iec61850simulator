package com.cgi.iec61850serversimulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt32U;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;

class Schedule {
	private static final Logger logger = LoggerFactory.getLogger(Schedule.class);
	//TODO: SwitchingMoment related attributes and functions here.
	
	// Enum as prescribed in the GXF documentation for SetSchedule
	public enum DAY{
		EVERYDAY(0), 
		WEEKDAY(-1), 
		WEEKEND(-2), 
		MONDAY(1), 
		TUESDAY(2), 
		WEDNESDAY(3),
		THURSDAY(4), 
		FRIDAY(5), 
		SATURDAY(6), 
		SUNDAY(7);
		
		private int day;
		
		DAY(int day) {
			this.day = day;
		}
		
		public int getDay() {
			return this.day;
		}
		
		
	}
	
	public enum TIMETYPE{
		FIXED(0), 
		SENSOR(1), 
		ASTRONOMIC(2);
		
		private int timeType;

		TIMETYPE(int timeType) {
			this.timeType = timeType;
		}
		
		public int getTimeType() {
			return this.timeType;
		}
	}
	// serverModel.findModelNode("SWDeviceGenericIO/CSLC.Clock", Fc.CF)
	int indexNumber; // Temporarily pre-set because only the first one is implemented
	boolean enabled;
	String description;
	int dayInt;
	//DAY day;
	short timeOn; //(between 0 and 2359, then 100 % 59) -1 = no action
	int timeOnTypeInt;
	//TIMETYPE timeOnType;
	short timeOff; //(between 0 and 2359, then 100 % 59) -1 = no action
	int timeOffTypeInt;
	//TIMETYPE timeOffType;
	short burningMinsOn;
	short beforeOffset; // Maximum of 150?
	short afterOffset; // Maximum of 150?
	
	public Schedule(ModelNode scheduleInfo, int scheduleNr) {
		this.indexNumber = scheduleNr + 1;
		
		List<BasicDataAttribute> bdas = scheduleInfo.getBasicDataAttributes();
		
		for (BasicDataAttribute bda : bdas ) {
			String dataAttribute = bda.getName();
			
			switch(dataAttribute) {
			case "enable":
				logger.info("Schedule Enabled Status found.");
				this.enabled = ((BdaBoolean) bda).getValue();
				break;
				
			case "day":
				logger.info("Day found.");
				this.dayInt = ((BdaInt32) bda).getValue();
				break;
			
			case "tOn":
				logger.info("Time On found.");
				this.timeOn = (short) ((BdaInt32) bda).getValue();
				break;
				
			case "tOnT":
				logger.info("Time On Type found.");
				this.timeOnTypeInt = (int) ((BdaInt8) bda).getValue();
				break;
				
			case "tOff":
				logger.info("Time Off found.");
				this.timeOff = (short) ((BdaInt32) bda).getValue();
				break;
				
			case "tOffT":
				logger.info("Time Off Type found.");
				this.timeOffTypeInt = (int) ((BdaInt8) bda).getValue();
				break;
				
			case "minOnPer":
				logger.info("Burning Minutes found.");
				this.burningMinsOn = (short) ((BdaInt16U) bda).getValue();
				break;
				
			case "srBefWd":
				logger.info("Before Astrological Time Offset found.");
				this.beforeOffset = (short) ((BdaInt16U) bda).getValue();
				break;
				
			case "srAftWd":
				logger.info("After Astrological Time Offset found.");
				this.afterOffset = (short) ((BdaInt16U) bda).getValue();
				break;
				
			case "Descr":
				logger.info("Description found.");
				this.description = ((BdaVisibleString) bda).getValueString();
				break;
				
			default:
				logger.info("Unimplemented value found: " + bda.getName() + ", skipped.");
			}
		}
	}

	@Override
	public String toString() {
		return "Schedule [indexNumber=" + indexNumber + ", enabled=" + enabled + ", description=" + description
				+ ", day=" + dayInt + ", timeOn=" + timeOn + ", timeOnType=" + timeOnTypeInt + ", timeOff=" + timeOff
				+ ", timeOffType=" + timeOffTypeInt + ", burningMinsOn=" + burningMinsOn + ", beforeOffset=" + beforeOffset
				+ ", afterOffset=" + afterOffset + "]";
	}

	public int getIndexNumber() {
		return indexNumber;
	}

	public void setIndexNumber(int indexNumber) {
		this.indexNumber = indexNumber;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDayInt() {
		return dayInt;
	}

	public void setDayInt(int dayInt) {
		this.dayInt = dayInt;
	}

	public short getTimeOn() {
		return timeOn;
	}

	public void setTimeOn(short timeOn) {
		this.timeOn = timeOn;
	}

	public int getTimeOnTypeInt() {
		return timeOnTypeInt;
	}

	public void setTimeOnTypeInt(int timeOnTypeInt) {
		this.timeOnTypeInt = timeOnTypeInt;
	}

	public short getTimeOff() {
		return timeOff;
	}

	public void setTimeOff(short timeOff) {
		this.timeOff = timeOff;
	}

	public int getTimeOffTypeInt() {
		return timeOffTypeInt;
	}

	public void setTimeOffTypeInt(int timeOffTypeInt) {
		this.timeOffTypeInt = timeOffTypeInt;
	}

	public short getBurningMinsOn() {
		return burningMinsOn;
	}

	public void setBurningMinsOn(short burningMinsOn) {
		this.burningMinsOn = burningMinsOn;
	}

	public short getBeforeOffset() {
		return beforeOffset;
	}

	public void setBeforeOffset(short beforeOffset) {
		this.beforeOffset = beforeOffset;
	}

	public short getAfterOffset() {
		return afterOffset;
	}

	public void setAfterOffset(short afterOffset) {
		this.afterOffset = afterOffset;
	}
}
