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

	public static class ScheduleBuilder {

		private int scheduleNr;
		private int relayNr;
		private int dayInt;
		private int fixedTimeInt;
		private int fixedTimeOn;
		private int fixedTimeOff;
		private LocalTime timeOn;
		private LocalTime timeOff;
		private int burningMins;
		private boolean enabled;

		public ScheduleBuilder(int scheduleNr) {
			this.scheduleNr = scheduleNr;
		}

		public ScheduleBuilder relayNr(int relayNr) {
			this.relayNr = relayNr;

			return this;
		}

		public ScheduleBuilder dayInt(int dayInt) {
			this.dayInt = dayInt;

			return this;
		}

		public ScheduleBuilder fixedTimeInt(int fixedTimeInt) {
			this.fixedTimeInt = fixedTimeInt;

			return this;
		}

		public ScheduleBuilder fixedTimeOn(int fixedTimeOn) {
			this.fixedTimeOn = fixedTimeOn;

			return this;
		}

		public ScheduleBuilder fixedTimeOff(int fixedTimeOff) {
			this.fixedTimeOff = fixedTimeOff;

			return this;
		}

		public ScheduleBuilder timeOn(LocalTime timeOn) {
			this.timeOn = timeOn;

			return this;
		}

		public ScheduleBuilder timeOff(LocalTime timeOff) {
			this.timeOff = timeOff;

			return this;
		}

		public ScheduleBuilder burningMins(int burningMins) {
			this.burningMins = burningMins;

			return this;
		}

		public ScheduleBuilder isEnabled(boolean isEnabled) {
			this.enabled = isEnabled;

			return this;
		}

		public Schedule buildSchedule() {
			Schedule schedule = new Schedule(this.scheduleNr);
			schedule.relayNr = this.relayNr;
			schedule.dayInt = this.dayInt;

			// When specific trigger types are disabled/not set (-1 = disabled)
			if (this.fixedTimeOn == -1 || this.fixedTimeOff == -1) {
				schedule.timeOnTypeInt = this.fixedTimeInt;
				schedule.timeOffTypeInt = this.fixedTimeInt;
			}

			// When specific trigger types are set
			else {
				schedule.timeOnTypeInt = this.fixedTimeOn;
				schedule.timeOffTypeInt = this.fixedTimeOff;
			}

			schedule.timeOn = this.timeOn;
			schedule.timeOff = this.timeOff;
			schedule.burningMinsOn = this.burningMins;
			schedule.enabled = this.enabled;

			return schedule;

		}
	}

	private int indexNumber;
	private int relayNr;
	private boolean enabled;
	private String description;
	private int dayInt;
	private LocalTime timeOn;
	private int timeOnTypeInt;
	private LocalTime timeOff;
	private int timeOffTypeInt;
	private int burningMinsOn;

	// Maximum of 150 minutes for offsets, in GXF platform
	private int beforeOffset;
	private int afterOffset;

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
