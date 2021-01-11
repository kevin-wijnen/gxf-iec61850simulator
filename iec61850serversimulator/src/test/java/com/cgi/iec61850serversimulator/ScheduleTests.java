package com.cgi.iec61850serversimulator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.internal.cli.CliParameter;
import com.beanit.openiec61850.internal.cli.CliParameterBuilder;
import com.beanit.openiec61850.internal.cli.CliParseException;
import com.beanit.openiec61850.internal.cli.CliParser;
import com.beanit.openiec61850.internal.cli.StringCliParameter;
import com.cgi.iec61850serversimulator.Schedule;
import com.cgi.iec61850serversimulator.Scheduler;


class ScheduleTests {
	
	// Initializing example schedule + scheduler class
	Scheduler scheduler = new Scheduler();

	Schedule schedule1 = new Schedule(0);
	Device device = new Device();
	Relay[] relay = new Relay[4];
	
	
	// Via relay ophalen? Zodat via relay het gegeven kan worden in plaats van 
	
	@Test
	void testTimeExtraction(){
		schedule1.setIndexNumber(1);
		schedule1.setRelayNr(1);
		schedule1.setEnabled(true);
		schedule1.setDescription("Test schedule");
		schedule1.setDayInt(0);
		schedule1.setTimeOn(LocalTime.NOON);
		schedule1.setTimeOnTypeInt(0);
		schedule1.setTimeOff(LocalTime.of(13, 00));
		schedule1.setTimeOffTypeInt(0);
		schedule1.setBurningMinsOn(0);
		schedule1.setBeforeOffset(0);
		schedule1.setAfterOffset(0);
		
		LocalTime[]time = scheduler.timeExtractor(schedule1);
		
		
		// Start and stop time validation
		assertEquals(LocalTime.of(12, 00), time[0]);
	}
	// Initializing example schedule

	@Test
	void testTriggeringSchedule() {
		device.initalizeDevice();
		
		schedule1.setIndexNumber(1);
		schedule1.setRelayNr(1);

		schedule1.setEnabled(true);
		schedule1.setDescription("Test schedule");
		schedule1.setDayInt(0);
		schedule1.setTimeOn(LocalTime.NOON);
		schedule1.setTimeOnTypeInt(0);

		schedule1.setTimeOff(LocalTime.of(13, 00));
		schedule1.setTimeOffTypeInt(0);
		schedule1.setBurningMinsOn(0);
		schedule1.setBeforeOffset(0);
		schedule1.setAfterOffset(0);
		
		LocalTime toCheckTime = LocalTime.of(12, 00);
		
		// Current Time
		scheduler.scheduleCheck(device, schedule1, 1);
		assertFalse(device.getRelay(1).getLight());
		
		scheduler.scheduleCheck(device, schedule1, toCheckTime, 1);
		assertTrue(device.getRelay(1).getLight());
		
	}
	

}
