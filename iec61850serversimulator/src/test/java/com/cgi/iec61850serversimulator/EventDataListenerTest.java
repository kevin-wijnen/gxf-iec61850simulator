package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.beanit.openiec61850.ObjectReference;

public class EventDataListenerTest {

	private EventDataListener eventDataListener = new EventDataListener(null);

	@Test
	public void extractRelaySingleDigit() {
		final ObjectReference reference1 = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche1.Descr");
		final int relayIndex1 = this.eventDataListener.extractRelayIndex(reference1);
		assertEquals(1, relayIndex1);
	}

	@Test
	public void extractRelayMultipleDigits() {
		final ObjectReference reference10 = new ObjectReference("SWDeviceGenericIO/XSWC111111110.Sche.sche1.Descr");
		final int relayIndex10 = this.eventDataListener.extractRelayIndex(reference10);
		assertEquals(111111110, relayIndex10);
	}

	@Test
	public void extractScheduleSingleDigit() {
		final ObjectReference referenceSingleDigit = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche1.Descr");
		final int scheduleIndexSingleDigit = this.eventDataListener.extractScheduleIndex(referenceSingleDigit);
		assertEquals(1, scheduleIndexSingleDigit);
	}

	@Test
	public void extractScheduleMultipleDigits() {
		final ObjectReference referenceTenDigits = new ObjectReference(
				"SWDeviceGenericIO/XSWC1.Sche.sche1111111110.Descr");
		final int scheduleIndexTenDigits = this.eventDataListener.extractScheduleIndex(referenceTenDigits);
		assertEquals(1111111110, scheduleIndexTenDigits);
	}
}
