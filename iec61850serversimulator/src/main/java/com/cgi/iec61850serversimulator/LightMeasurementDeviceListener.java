package com.cgi.iec61850serversimulator;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

class LightMeasurementDeviceListener implements ServerEventListener{
	
	private static final Logger logger = LoggerFactory.getLogger(LightMeasurementDeviceListener.class);
	LocalDateTime currentTime = null;
	String syncPer;
	Device device = null;
	
	public LightMeasurementDeviceListener(Device device) {
		this.device = device;
		
	}

	@Override
	public List<ServiceError> write(List<BasicDataAttribute> bdas) {
		//TODO: Fixing relayScheduleNumbers integration to not lose connection with GUI/SoapUI! Problem lies with the setters.
		logger.info("BDA write request scanning...");
		
		for (BasicDataAttribute bda : bdas) {
			String dataAttribute = bda.getName();
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
				device.clock.setCurrentTime(LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1)));
				//System.out.println("CurT variabel test");
				break;
				
			case "tZ":
				logger.info("Time Zone value found. (Offset in minutes from UTC!)");
				device.clock.setTimeZoneOffset(((BdaInt16) bda).getValue());
				break;
				
			case "dstBegT":
				logger.info("Daylight Saving Time Start Date value found.");
				device.clock.setBeginDateDST(((BdaVisibleString) bda).getValueString());
				break;
				
			case "dstEndT":
				logger.info("Daylight Saving Time Start Date value found.");
				device.clock.setEndDateDST(((BdaVisibleString) bda).getValueString());
				break;
				
			case "dvt":
				logger.info("Daylight Saving Time Deviation value found.");
				device.clock.setDeviationDST(((BdaInt16) bda).getValue());
				break;
				
			case "enbDst":
				logger.info("Daylight Saving Time Status value found.");
				device.clock.setEnableDST(((BdaBoolean) bda).getValue());
				break;
				
			case "enbNtpC":
				logger.info("NTP Client Enabled value found.");
				device.clock.setEnableNTP(((BdaBoolean) bda).getValue());
				break;
			
			case "ntpSvrA":
				logger.info("NTP Server IP Address value found.");
				device.clock.setIpAddressNTP(((BdaVisibleString) bda).getValueString());
				break;
				
			case "syncPer":
				logger.info("Time Sync Interval (in minutes) value found.");
				device.clock.setTimeSyncInterval(((BdaInt16U) bda).getValue());
				break;
			
			// Relay data	
			//case "":
				//break;
			
			// Schedule data
				
			case "enable":
				logger.info("Schedule Enabled Status found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				
				logger.info("Value to set for schedule 1 of relay 1:", ((BdaBoolean) bda).getValue());
				try {
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setEnabled(((BdaBoolean) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
			case "day":
				logger.info("Day found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setDayInt(((BdaInt32) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
			
			case "tOn":
				logger.info("Time On found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setTimeOn((short) ((BdaInt32) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
			case "tOnT":
				logger.info("Time On Type found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setTimeOnTypeInt((int) ((BdaInt8) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
			case "tOff":
				logger.info("Time Off found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setTimeOff((short) ((BdaInt32) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}break;
				
			case "tOffT":
				logger.info("Time Off Type found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setTimeOffTypeInt((int) ((BdaInt8) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
			case "minOnPer":
				logger.info("Burning Minutes found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setBurningMinsOn((short) ((BdaInt16U) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
			case "srBefWd":
				logger.info("Before Astrological Time Offset found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setBeforeOffset((short) ((BdaInt16U) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
			case "srAftWd":
				logger.info("After Astrological Time Offset found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try{
					device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setAfterOffset((short) ((BdaInt16U) bda).getValue());
				}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
			case "Descr":
				logger.info(" found.");
				relayScheduleNumbers = relayScheduleNumbersCheck(bda.getReference());
				try {
				device.relays[relayScheduleNumbers[0]].schedules[relayScheduleNumbers[1]].setDescription(((BdaVisibleString) bda).getValueString());}
				catch(Exception e){
					logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
				}
				break;
				
				
			default:
				logger.info("Unimplemented value found, " + "'" + dataAttribute + "'" + ", skipped.");
				break;
			}
			/*System.out.println(bda.getReference());
			System.out.println(bda.getFc());
			String updatedNodeName = bda.getName();
			System.out.println(updatedNodeName);
			System.out.println(bda.getValueString());
			try {
			
				
				
			/*if (updatedNodeName.equals("enbDst")){	
				logger.info("DST STATUS FOUND!");
				logger.info(bda.getValueString());
				boolean daylightActive = Boolean.parseBoolean(bda.getValueString());
				device.clock.changeDST(daylightActive);
			}
			
			else if (updatedNodeName.equals("curT")){
				logger.info("CURRENT TIME FOUND!");
				logger.info(bda.getValueString());
				currentTime = LocalDateTime.parse(bda.getValueString());}
			
			else if (updatedNodeName.equals("syncPer")) {
			logger.info("TIME SYNC INTERVAL FOUND!");
			int syncPer = ((BdaInt16U) bda).getValue();
			logger.info("Value: " + syncPer);
			device.updateSyncInterval(syncPer);
			}
			
			else if (updatedNodeName.equals("Oper.ctlVal")) {
				logger.info("SWITCH LIGHT STATUS FOUND!");
				System.out.println(bda.getParent());
			}
			
			/*else if (bda.getName().equals(bda)){
				
			}
			
			} catch (RuntimeException re)	 {
				logger.error("Parsen van nodewaarde is gefaald", re);
			}*/

						
		//device.updateDeviceCheck(daylightActive);
		//BLIJFT VASTHANGEN ALS IE CURRENTTIME OF SYNCPER OPHAALT! SYNCPER IS NULL, dayLightActive werkt wel gewoon!
		// Vasthangen = opnieuw moeten reconnecten om de server opnieuw te starten, programma zelf blijft wel draaien zonder crash. (Dus 'stopped listening'?)
			}
			
			/*if (bda.getParent().toString().contains("SWDeviceGenericIO/CSLC.Clock.enbDst:")) {
				logger.info(bda.getName());
			}*/	
			
			/*if (bda.getParent().toString().contains("SWDeviceGenericIO/XSWC") && bda.getParent().toString().contains("Sche.sche"))
				logger.info("Switch schedule data.");
	          	logger.info(bda.getName());
	          	logger.info(bda.getParent().getName());
	     }*/
		logger.info("**Printing device.");
		//device.deviceDisplay();
		
		return null;
	}

	@Override
	public void serverStoppedListening(ServerSap serverSAP) {
		// TODO Auto-generated method stub
		
	}
	
	public int[] relayScheduleNumbersCheck(ObjectReference reference) {
		System.out.println(reference);
		System.out.println(reference.toString());
		
		int[] relaySchedule = new int[2];
		String referenceString = reference.toString();
		System.out.println("Reference string aangemaakt.");
		
		relaySchedule[0] = Integer.parseInt(Character.toString(referenceString.charAt(22))) - 1;
		System.out.println("Eerste integer in relaySchedule aangemaakt: " + relaySchedule[0]);
		
		// Check to see if schedule number is over 9, to take second digit into account
		try {
		String scheduleNumber = Character.toString(referenceString.charAt(33)) + Character.toString(referenceString.charAt(34));
		relaySchedule[1] = Integer.parseInt(scheduleNumber) - 1;
		System.out.println("Eerste nummer gevonden voor tweede getal.");
		}
		catch(Exception e){
			System.out.println("Tweede getal gevonden! Nu nog naar integer maken.");
			relaySchedule[1] = Integer.parseInt(Character.toString(referenceString.charAt(33))) - 1;
			System.out.println("Tweede getal omgezet.");
		}
		
		System.out.println(relaySchedule[0]);
		System.out.println(relaySchedule[1]);
		
		return relaySchedule;
	}

}