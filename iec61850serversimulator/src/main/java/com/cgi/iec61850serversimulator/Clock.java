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
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;

public class Clock {
	private static final Logger logger = LoggerFactory.getLogger(Clock.class);
	
	LocalDateTime currentTime;
	short timeZoneOffset; // offset in minutes from UTC
	String beginDateDST; // time zone abbreviation, in format: M<month in MM>.<which 'th' day in the month, 5 = 5th/last day of the month>.<which type of day it should switch? 0 = Sunday?>/<which hour it switches on>
	String endDateDST; // time zone abbreviation, in format: M<month in MM>.<which 'th' day in the month, 5 = 5th/last day of the month>.<which type of day it should switch? 0 = Sunday?>/<which hour it switches on>
	short deviationDST; // in minutes, how many minutes will be switched over when DST is enabled/disabled (Standard = 60 minutes = 1 hour)
	boolean enableDST;
	boolean enableNTP;
	String ipAddressNTP; // if multiple are in use, use ; to separate them
	short timeSyncInterval; // in minutes, regarding synchronizing time with the NTP server
	
	public void initializeClock(ServerModel serverModel){
		ModelNode clockInfo = serverModel.findModelNode("SWDeviceGenericIO/CSLC.Clock", Fc.CF);
		List<BasicDataAttribute> bdas = clockInfo.getBasicDataAttributes();
		
		for (BasicDataAttribute bda : bdas ) {
			String dataAttribute = bda.getName();
			
			switch(dataAttribute) {
			case "curT":
				logger.info("Current Time value found.");
				
				// For native timestamp: Epoch/UNIX timestamp format is used, from second on. Convert to native timestamp!. 
				byte[] bytesEpochTime = ((BdaTimestamp) bda).getValue();
				ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
				long longTime = wrappedTime.getLong();	
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
				this.timeSyncInterval = ((BdaInt16) bda).getValue();
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
		logger.info("Daylight Saving Time start date: " + this.beginDateDST); // Making it readable for humans?
		logger.info("Daylight Saving Time end date: " + this.endDateDST); // Making it readable for humans?
		logger.info("Daylight Saving Time deviation (in minutes): " + this.deviationDST);
		logger.info("Daylight Saving Time enabled: " + this.enableDST);
		logger.info("NTP client enabled: " + this.enableNTP);
		logger.info("NTP server IP address(es): " + this.ipAddressNTP);
		
	}
}

