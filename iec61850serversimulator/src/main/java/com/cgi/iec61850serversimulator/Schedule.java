package com.cgi.iec61850serversimulator;

import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.ModelNode;

/**
 * Class which represents a schedule.
 */
class Schedule {
	private static final Logger logger = LoggerFactory.getLogger(Schedule.class);
	// TODO: SwitchingMoment related attributes and functions here.

	// Enum as prescribed in the GXF documentation for SetSchedule
	// To use for clearer visualization to users?
	public enum DAY {
		EVERYDAY(0), WEEKDAY(-1), WEEKEND(-2), MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6),
		SUNDAY(7);

		private int day;

		DAY(int day) {
			this.day = day;
		}

		public int getDay() {
			return this.day;
		}

	}

	// Usage for clearer visualization to users?
	public enum TIMETYPE {
		FIXED(0), SENSOR(1), ASTRONOMIC(2);

		private int timeType;

		TIMETYPE(int timeType) {
			this.timeType = timeType;
		}

		public int getTimeType() {
			return this.timeType;
		}
	}

	int indexNumber;
	int relayNr;
	boolean enabled;
	String description;
	int dayInt;
	// DAY day;
	LocalTime timeOn;
	int timeOnTypeInt;
	// TIMETYPE timeOnType;
	LocalTime timeOff;
	int timeOffTypeInt;
	// TIMETYPE timeOffType;
	int burningMinsOn;
	// Maximum of 150 minutes for offsets, in GXF platform
	int beforeOffset;
	int afterOffset;

	public Schedule(int indexNumber) {
		// For empty schedule initialization, especially for test cases!
		this.setIndexNumber(indexNumber + 1);
	}

	public Schedule(ModelNode scheduleInfo, int scheduleNr, int relayNr) {
		this.setIndexNumber(scheduleNr + 1);
		this.setRelayNr(relayNr);

		List<BasicDataAttribute> bdas = scheduleInfo.getBasicDataAttributes();

		for (BasicDataAttribute bda : bdas) {
			String dataAttribute = bda.getName();

			switch (dataAttribute) {
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
				int timeOnInt = ((BdaInt32) bda).getValue();
				this.timeOn = LocalTime.of(timeOnInt / 100, timeOnInt % 100);
				break;

			case "tOnT":
				logger.info("Time On Type found.");
				this.timeOnTypeInt = ((BdaInt8) bda).getValue();
				break;

			case "tOff":
				logger.info("Time Off found.");
				int timeOffInt = ((BdaInt32) bda).getValue();
				this.timeOff = LocalTime.of(timeOffInt / 100, timeOffInt % 100);
				break;

			case "tOffT":
				logger.info("Time Off Type found.");
				this.timeOffTypeInt = ((BdaInt8) bda).getValue();
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
		// TODO: Making certain raw data better visualized towards users? AKA Day,
		// timeOn/timeOff, their types etc.
		StringBuilder scheduleStringBuilder = new StringBuilder();
		scheduleStringBuilder.append("Schedule " + this.indexNumber + ":\n")
				.append("Is it enabled? " + this.enabled + '\n').append("Description: " + this.description + '\n')
				.append("Day: " + this.dayInt + '\n').append("Time On: " + this.timeOn + '\n')
				.append("Time On type: " + this.timeOnTypeInt + '\n').append("Time Off: " + this.timeOffTypeInt + '\n')
				.append("Burning Minutes On: " + this.burningMinsOn + '\n')
				.append("Offset (Before): " + this.beforeOffset + '\n')
				.append("Offset (After): " + this.afterOffset + '\n');

		return scheduleStringBuilder.toString();
	}

	public int getIndexNumber() {
		return this.indexNumber;
	}

	public void setIndexNumber(int indexNumber) {
		this.indexNumber = indexNumber;
	}

	public int getRelayNr() {
		return this.relayNr;
	}

	public void setRelayNr(int relayNr) {
		this.relayNr = relayNr;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public int getDayInt() {
		return this.dayInt;
	}

	public void setDayInt(int dayInt) {

		this.dayInt = dayInt;
	}

	public LocalTime getTimeOn() {
		return this.timeOn;
	}

	public void setTimeOn(LocalTime timeOn) {
		this.timeOn = timeOn;
	}

	public int getTimeOnTypeInt() {
		return this.timeOnTypeInt;
	}

	public void setTimeOnTypeInt(int timeOnTypeInt) {

		this.timeOnTypeInt = timeOnTypeInt;
	}

	public LocalTime getTimeOff() {
		return this.timeOff;
	}

	public void setTimeOff(LocalTime timeOff) {
		this.timeOff = timeOff;
	}

	public int getTimeOffTypeInt() {
		return this.timeOffTypeInt;
	}

	public void setTimeOffTypeInt(int timeOffTypeInt) {

		this.timeOffTypeInt = timeOffTypeInt;
	}

	public int getBurningMinsOn() {
		return this.burningMinsOn;
	}

	public void setBurningMinsOn(int burningMinsOn) {
		this.burningMinsOn = burningMinsOn;
	}

	public int getBeforeOffset() {
		return this.beforeOffset;
	}

	public void setBeforeOffset(int beforeOffset) {
		this.beforeOffset = beforeOffset;
	}

	public int getAfterOffset() {
		return this.afterOffset;
	}

	public void setAfterOffset(int afterOffset) {
		this.afterOffset = afterOffset;
	}

}
