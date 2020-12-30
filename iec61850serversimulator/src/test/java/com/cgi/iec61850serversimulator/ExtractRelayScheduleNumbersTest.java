package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.beanit.openiec61850.ObjectReference;
import com.cgi.iec61850serversimulator.EventDataListener;

public class ExtractRelayScheduleNumbersTest {
	
	private EventDataListener relayChecker = new EventDataListener(null);
	
	@Test
	public void testRelayScheduleSingleDigit() {
		ObjectReference reference = new ObjectReference("SWDeviceGenericIO/XSWC4.Sche.sche4.Enabled");
		
		int[] relaySchedule = relayChecker.extractRelayScheduleNumbers(reference);
		
		assertEquals(4, relaySchedule[0]);
		assertEquals(4, relaySchedule[1]);		
	}
	
	@Test
	public void testRelayScheduleDoubleDigit() {
		ObjectReference reference = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche58.Descr");
		
		int[] relaySchedule = relayChecker.extractRelayScheduleNumbers(reference);
		
		assertEquals(1, relaySchedule[0]);
		assertEquals(58, relaySchedule[1]);		
	}
	
	@Test
	public void testRelayScheduleDynamicDigit() {
		// Pattern: [0-9]{1,}
		ObjectReference reference = new ObjectReference("SWDeviceGenericIO/XSWC111.Sche.sche45822.Descr");
		int[] regexResults = new int[2];
		Pattern numberPattern = Pattern.compile("[0-9]{1,}");
		Matcher numberMatcher = numberPattern.matcher(reference.toString());
		//while (numberMatcher.find()) {
		
		for (int i = 0; i < 2; i++) {
			numberMatcher.find();
			regexResults[i] = Integer.parseInt(numberMatcher.group());
		}
		assertEquals(111, regexResults[0]);
		assertEquals(45822, regexResults[1]);
	}
	
	@Test
	public void testRelayDynamicDigit() {
		ObjectReference reference1 = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche1.Descr");
		ObjectReference reference2 = new ObjectReference("SWDeviceGenericIO/XSWC12.Sche.sche1.Descr");
		ObjectReference reference5 = new ObjectReference("SWDeviceGenericIO/XSWC11115.Sche.sche1.Descr");
		ObjectReference reference10 = new ObjectReference("SWDeviceGenericIO/XSWC111111110.Sche.sche1.Descr");

		int relayIndex1 = relayChecker.extractRelayIndex(reference1);
		int relayIndex2 = relayChecker.extractRelayIndex(reference2);
		int relayIndex5 = relayChecker.extractRelayIndex(reference5);
		int relayIndex10 = relayChecker.extractRelayIndex(reference10);
		
		assertEquals(1, relayIndex1);
		assertEquals(12, relayIndex2);
		assertEquals(11115, relayIndex5);
		assertEquals(111111110, relayIndex10);
	}
	
	@Test
	public void testScheduleDynamicDigit() {
		ObjectReference reference1 = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche1.Descr");
		ObjectReference reference2 = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche12.Descr");
		ObjectReference reference5 = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche11115.Descr");
		ObjectReference reference10 = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche1111111110.Descr");

		int scheduleIndex1 = relayChecker.extractScheduleIndex(reference1);
		int scheduleIndex2 = relayChecker.extractScheduleIndex(reference2);
		int scheduleIndex5 = relayChecker.extractScheduleIndex(reference5);
		int scheduleIndex10 = relayChecker.extractScheduleIndex(reference10);
		
		assertEquals(1, scheduleIndex1);
		assertEquals(12, scheduleIndex2);
		assertEquals(11115, scheduleIndex5);
		assertEquals(1111111110, scheduleIndex10);
	}
	
	
}
