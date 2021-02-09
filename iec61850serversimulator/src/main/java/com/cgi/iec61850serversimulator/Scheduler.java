package com.cgi.iec61850serversimulator;

import java.time.LocalTime;
import com.cgi.iec61850serversimulator.Device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Scheduler {
	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	public LocalTime[] timeExtractor(Schedule schedule){
		LocalTime[] time = new LocalTime[2];
		
		/*int startHour = Integer.parseInt(Integer.toString(schedule.getTimeOn()).substring(0,2));
		int startMin = Integer.parseInt(Integer.toString(schedule.getTimeOn()).substring(2,4));
		int stopHour = Integer.parseInt(Integer.toString(schedule.getTimeOn()).substring(0,2));
		int stopMin = Integer.parseInt(Integer.toString(schedule.getTimeOn()).substring(2,4));*/
		LocalTime startTime = schedule.timeOn;
		
		time[0] = startTime;
		//time[1] = stopTime;
		
		return time;
		
	}
	
	public void scheduleCheck(Device device, Schedule schedule, LocalTime currentTime, int relayNr) {
		// VERSION FOR UNIT TESTS!
		
		//TODO: Check input schedule for times, then do the following actions when checking the time!
		// currentTime as parameter due to test data compatibility
		// Also necessary: Relay?
		
		// Steps:
		// Check schedule's time
		// Compare it to wanted on/off times
		// Execute the proper action/setLight feature
		
		// Necessities:
		// Schedule data
		// LocalTime current Time
		// Relay to switch?
		
		LocalTime timeOn = schedule.timeOn;
		LocalTime timeOff = schedule.timeOff;
		
		if (currentTime == timeOn) {
			device.getRelay(relayNr).setLight(true);
			logger.info("Light switched on");
			// Turn on light at the right relay
		}
		else if (currentTime == timeOff) {
			device.getRelay(relayNr).setLight(false);
			logger.info("Light switched off");
			// Turn off light at the right relay
		}
		else {
			logger.info("Current time " + currentTime.toString() + " does not fit schedule times. No action executed.");
		}
	}
		
		public void scheduleCheck(Device device, Schedule schedule, int relayNr) {
			//TODO: Check input schedule for times, then do the following actions when checking the time!
			// currentTime as parameter due to test data compatibility
			// Also necessary: Relay?
			
			// Steps:
			// Check schedule's time
			// Compare it to wanted on/off times
			// Execute the proper action/setLight feature
			
			// Necessities:
			// Schedule data
			// LocalTime current Time
			// Relay to switch?
			
			LocalTime timeOn = schedule.timeOn;
			LocalTime timeOff = schedule.timeOff;
			LocalTime currentTime = LocalTime.now();
			
			if (currentTime == timeOn) {
				device.getRelay(relayNr).setLight(true);
				logger.info("Light switched on");
				// Turn on light at the right relay
			}
			else if (currentTime == timeOff) {
				device.getRelay(relayNr).setLight(false);
				logger.info("Light switched off");
				// Turn off light at the right relay
			}
			else {
				logger.info("Current time " + currentTime.toString() + " does not fit schedule times. No action executed.");
			}
			
	}

}
