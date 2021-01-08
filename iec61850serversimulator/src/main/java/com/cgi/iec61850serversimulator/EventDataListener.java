package com.cgi.iec61850serversimulator;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.ObjectReference;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;

class EventDataListener implements ServerEventListener{
	
	private static final Logger logger = LoggerFactory.getLogger(EventDataListener.class);
	LocalDateTime currentTime = null;
	String syncPer;
	Device device = null;
	
	public EventDataListener(Device device) {
		this.device = device;
		
	}

	@Override
	public List<ServiceError> write(List<BasicDataAttribute> bdas) {
		//TODO: Fixing relayScheduleNumbers integration to not lose connection with GUI/SoapUI! Problem lies with the setters.
		logger.info("BDA write request scanning...");
      try {
		for (BasicDataAttribute bda : bdas) {
			String dataAttribute = bda.getName();
			String referenceString = bda.getReference().toString();
			logger.info(dataAttribute);
			int[] relayScheduleNumbers; 
			//System.out.println("Relay array aangemaakt." + relayScheduleNumbers);
			switch(dataAttribute) {
			
			// Clock data
			case "curT":
				logger.info("Current Time value found.");
				
				// For native timestamp: Epoch/UNIX timestamp format is used, from second on. Convert to native timestamp!. 
				byte[] bytesEpochTime = ((BdaTimestamp) bda).getValue();
				ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
				long longTime = wrappedTime.getLong();
				device.getClock().setCurrentTime(LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1)));
				//System.out.println("CurT variabel test");
				break;
				
			case "tZ":
				logger.info("Time Zone value found. (Offset in minutes from UTC!)");
				device.getClock().setTimeZoneOffset(((BdaInt16) bda).getValue());
				break;
				
			case "dstBegT":
				logger.info("Daylight Saving Time Start Date value found.");
				device.getClock().setBeginDateDST(((BdaVisibleString) bda).getValueString());
				break;
				
			case "dstEndT":
				logger.info("Daylight Saving Time Start Date value found.");
				device.getClock().setEndDateDST(((BdaVisibleString) bda).getValueString());
				break;
				
			case "dvt":
				logger.info("Daylight Saving Time Deviation value found.");
				device.getClock().setDeviationDST(((BdaInt16) bda).getValue());
				break;
				
			case "enbDst":
				logger.info("Daylight Saving Time Status value found.");
				device.getClock().setEnableDST(((BdaBoolean) bda).getValue());
				break;
				
			case "enbNtpC":
				logger.info("NTP Client Enabled value found.");
				device.getClock().setEnableNTP(((BdaBoolean) bda).getValue());
				break;
			
			case "ntpSvrA":
				logger.info("NTP Server IP Address value found.");
				device.getClock().setIpAddressNTP(((BdaVisibleString) bda).getValueString());
				break;
				
			case "syncPer":
				logger.info("Time Sync Interval (in minutes) value found.");
				device.getClock().setTimeSyncInterval(((BdaInt16U) bda).getValue());
				break;
			
			// Relay data
			// Remember: Set CTLModel to 1, then it would work with enbOpr enabled!
			case "ctlVal":{
				int relayIndex = extractRelayIndex(bda.getReference());
				boolean lightStatus = ((BdaBoolean) bda).getValue();
				System.out.println(lightStatus);
						
				if (lightStatus) {
					device.getRelay(relayIndex).setLight(true);
				}
				else {
						device.getRelay(relayIndex).setLight(false);
				}
					
				logger.info("Relay " + relayIndex + "'s light status ON is " + lightStatus);
				break;}
			
			// Schedule data
				
			case "enable":{
				logger.info("Schedule Enabled Status found.");
				//TODO: Why is the previous instance out of scope?
				//int relayIndex = extractRelayIndex(bda.getReference());
				//int scheduleIndex = extractScheduleIndex(bda.getReference());
				relayScheduleNumbers = extractRelayScheduleNumbers(bda.getReference());
				
				logger.info("Value to set for schedule 1 of relay 1:", ((BdaBoolean) bda).getValue());
				try {
					device.getSchedule(relayScheduleNumbers).setEnabled(((BdaBoolean) bda).getValue());
					//device.getRelay(relayIndex).getSchedule(scheduleIndex).setEnabled(((BdaBoolean) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "day":{
				logger.info("Day found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				
				try{
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setDayInt(((BdaInt32) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
			
			case "tOn":{
				logger.info("Time On found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try{
					int timeOnInt = ((BdaInt32) bda).getValue();
					LocalTime timeOn = LocalTime.of(timeOnInt / 100, timeOnInt % 100);
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setTimeOn(timeOn);
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "tOnT":{
				logger.info("Time On Type found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try{
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setTimeOnTypeInt((int) ((BdaInt8) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "tOff":{
				logger.info("Time Off found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try{
					int timeOffInt = ((BdaInt32) bda).getValue();
					LocalTime timeOff = LocalTime.of(timeOffInt / 100, timeOffInt % 100);
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setTimeOff(timeOff);
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "tOffT":{
				logger.info("Time Off Type found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try{
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setTimeOffTypeInt((int) ((BdaInt8) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "minOnPer":{
				logger.info("Burning Minutes found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try{
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setBurningMinsOn((short) ((BdaInt16U) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "srBefWd":{
				logger.info("Before Astrological Time Offset found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try{
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setBeforeOffset((short) ((BdaInt16U) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "srAftWd":{
				logger.info("After Astrological Time Offset found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try{
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setAfterOffset((short) ((BdaInt16U) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
			case "Descr":{
				logger.info(" found.");
				int relayIndex = extractRelayIndex(bda.getReference());
				int scheduleIndex = extractScheduleIndex(bda.getReference());
				try {
					device.getRelay(relayIndex).getSchedule(scheduleIndex).setDescription(((BdaVisibleString) bda).getValueString());}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;}
				
				
			default:
				logger.info("Unimplemented value found, " + "'" + dataAttribute + "'" + ", skipped.");
				break;
			}
		}	
		} catch (Exception e) {
			logger.error("An unexpected exception occurred", e);
		}
      
		return new ArrayList<>();
	}

	@Override
	public void serverStoppedListening(ServerSap serverSAP) {
		// TODO Auto-generated method stub
		
	}
	
	public int[] extractRelayScheduleNumbers(ObjectReference reference) {
		System.out.println(reference);
		System.out.println(reference.toString());
		
		int[] relaySchedule = new int[2];
		String referenceString = reference.toString();
		System.out.println("Reference string aangemaakt.");
		
		relaySchedule[0] = Integer.parseInt(Character.toString(referenceString.charAt(22)));
		System.out.println("Eerste integer in relaySchedule aangemaakt: " + relaySchedule[0]);
		
		// Check to see if schedule number is over 9, to take second digit into account
		try {
		String scheduleNumber = Character.toString(referenceString.charAt(33)) + Character.toString(referenceString.charAt(34));
		relaySchedule[1] = Integer.parseInt(scheduleNumber);
		System.out.println("Eerste nummer gevonden voor tweede getal.");
		}
		catch(Exception e){
			System.out.println("Tweede getal gevonden! Nu nog naar integer maken.");
			relaySchedule[1] = Integer.parseInt(Character.toString(referenceString.charAt(33)));
			System.out.println("Tweede getal omgezet.");
		}
		
		System.out.println(relaySchedule[0]);
		System.out.println(relaySchedule[1]);
		
		return relaySchedule;
	}
	
	public int extractRelayIndex(ObjectReference reference) {
		String referenceString = reference.toString();
		System.out.println(referenceString);
		
		Pattern numberPattern = Pattern.compile("[0-9]+");
		Matcher numberMatcher = numberPattern.matcher(referenceString);
		
		numberMatcher.find();
		System.out.println(numberMatcher.group());
		
		return Integer.parseInt(numberMatcher.group());
	}
	
	public int extractScheduleIndex(ObjectReference reference) {
		String referenceString = reference.toString();
		//int scheduleIndex = Integer.parseInt(Character.toString(referenceString.charAt(33)));
		
		Pattern numberPattern = Pattern.compile("[0-9]+");
		Matcher numberMatcher = numberPattern.matcher(referenceString);
		
		for (int i = 0; i < 2; i++) {
			numberMatcher.find();
		}
		
		return Integer.parseInt(numberMatcher.group());
	}
	
}