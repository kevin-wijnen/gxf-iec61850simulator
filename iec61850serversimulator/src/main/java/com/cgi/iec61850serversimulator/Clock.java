package com.cgi.iec61850serversimulator;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.ModelNode;

public class Clock {
	private static final Logger logger = LoggerFactory.getLogger(Clock.class);

	LocalDateTime currentTime;
	// Offset in minutes from UTC
	short timeZoneOffset;
	// Time zone abbreviation, in format: M<month in
	// MM>.<which 'th' day in the month, 5 = 5th/last day
	// of the month>.<which type of day it should switch? 0
	// = Sunday>/<which hour it switches on>
	String beginDateDST;
	// Same format for beginDateDST
	String endDateDST;
	// In minutes, how many minutes will be switched over
	// when DST is enabled/disabled (Standard = 60 minutes =
	// 1 hour)
	short deviationDST;
	boolean enableDST;
	boolean enableNTP;
	// If multiple IP addresses are in use, use ; to separate them
	String ipAddressNTP;
	// In minutes, regarding synchronizing time with the
	// NTP server
	int timeSyncInterval;

	public void initializeClock(final ModelNode clockInfo) {
		final List<BasicDataAttribute> bdas = clockInfo.getBasicDataAttributes();

		for (final BasicDataAttribute bda : bdas) {
			final String dataAttribute = bda.getName();

			switch (dataAttribute) {
			case "curT":
				logger.info("Current Time value found.");

				// For native timestamp: Epoch/UNIX timestamp format is used,
				// from second on. Convert to native timestamp!.
				final byte[] bytesEpochTime = ((BdaTimestamp) bda).getValue();
				final ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
				final long longTime = wrappedTime.getLong();
				this.currentTime = LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1));
				break;

			case "tZ":
				logger.info("Time Zone value found. (Offset in minutes from UTC!)");
				this.timeZoneOffset = ((BdaInt16) bda).getValue();
				break;

			case "dstBegT":
				logger.info("Daylight Saving Time Start Date value found.");
				this.beginDateDST = ((BdaVisibleString) bda).getValueString();
				break;

			case "dstEndT":
				logger.info("Daylight Saving Time Start Date value found.");
				this.endDateDST = ((BdaVisibleString) bda).getValueString();
				break;

			case "dvt":
				logger.info("Daylight Saving Time Deviation value found.");
				this.deviationDST = ((BdaInt16) bda).getValue();
				break;

			case "enbDst":
				logger.info("Daylight Saving Time Status value found.");
				this.enableDST = ((BdaBoolean) bda).getValue();
				break;

			case "enbNtpC":
				logger.info("NTP Client Enabled value found.");
				this.enableNTP = ((BdaBoolean) bda).getValue();
				break;

			case "ntpSvrA":
				logger.info("NTP Server IP Address value found.");
				this.ipAddressNTP = ((BdaVisibleString) bda).getValueString();
				break;

			case "syncPer":
				logger.info("Time Sync Interval (in minutes) value found.");
				this.timeSyncInterval = ((BdaInt16U) bda).getValue();
				break;

			default:
				logger.info("Unimplemented value found, " + "'" + dataAttribute + "'" + ", skipped.");
				break;
			}
		}
	}

	public void displayClock() {
		logger.info("** Printing clock.");
		logger.info("Current time: " + this.currentTime);
		logger.info("Time zone offset (in minutes, from UTC): " + this.timeZoneOffset);
		// Making it readable for humans?
		logger.info("Daylight Saving Time start date: " + this.beginDateDST);
		// Making it readable for humans?
		logger.info("Daylight Saving Time end date: " + this.endDateDST);
		logger.info("Daylight Saving Time deviation (in minutes): " + this.deviationDST);
		logger.info("Daylight Saving Time enabled: " + this.enableDST);
		logger.info("NTP client enabled: " + this.enableNTP);
		logger.info("NTP server IP address(es): " + this.ipAddressNTP);
	}

	@Override
	public String toString() {
		final StringBuilder clockStringBuilder = new StringBuilder();

		clockStringBuilder.append("** Printing clock.\n");
		clockStringBuilder.append("Current time: " + this.currentTime + '\n');
		clockStringBuilder.append("Time zone offset (in minutes, from UTC): " + this.timeZoneOffset + '\n');
		clockStringBuilder.append("Daylight Saving Time start date: " + this.beginDateDST + '\n');
		clockStringBuilder.append("Daylight Saving Time end date: " + this.endDateDST + '\n');
		clockStringBuilder.append("Daylight Saving Time deviation (in minutes): " + this.deviationDST + '\n');
		clockStringBuilder.append("Daylight Saving Time enabled: " + this.enableDST + '\n');
		clockStringBuilder.append("NTP client enabled: " + this.enableNTP + '\n');
		clockStringBuilder.append("NTP server IP address(es): " + this.ipAddressNTP + '\n');

		return clockStringBuilder.toString();
	}

	public void changeDST(final boolean enableDST) {
		this.enableDST = enableDST;
	}

	public void configNTP(final boolean enableNTP, final String ipAddressNTP, final short timeSyncInterval) {
		this.enableNTP = enableNTP;
		this.ipAddressNTP = ipAddressNTP;
		this.timeSyncInterval = timeSyncInterval;
	}

	public LocalDateTime getCurrentTime() {
		return this.currentTime;
	}

	public void setCurrentTime(final LocalDateTime currentTime) {
		this.currentTime = currentTime;
	}

	public short getTimeZoneOffset() {
		return this.timeZoneOffset;
	}

	public void setTimeZoneOffset(final short timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}

	public String getBeginDateDST() {
		return this.beginDateDST;
	}

	public void setBeginDateDST(final String beginDateDST) {
		this.beginDateDST = beginDateDST;
	}

	public String getEndDateDST() {
		return this.endDateDST;
	}

	public void setEndDateDST(final String endDateDST) {
		this.endDateDST = endDateDST;
	}

	public short getDeviationDST() {
		return this.deviationDST;
	}

	public void setDeviationDST(final short deviationDST) {
		this.deviationDST = deviationDST;
	}

	public boolean isEnableDST() {
		return this.enableDST;
	}

	public void setEnableDST(final boolean enableDST) {
		this.enableDST = enableDST;
	}

	public boolean isEnableNTP() {
		return this.enableNTP;
	}

	public void setEnableNTP(final boolean enableNTP) {
		this.enableNTP = enableNTP;
	}

	public String getIpAddressNTP() {
		return this.ipAddressNTP;
	}

	public void setIpAddressNTP(final String ipAddressNTP) {
		this.ipAddressNTP = ipAddressNTP;
	}

	public int getTimeSyncInterval() {
		return this.timeSyncInterval;
	}

	public void setTimeSyncInterval(final int timeSyncInterval) {
		this.timeSyncInterval = timeSyncInterval;
	}
}
