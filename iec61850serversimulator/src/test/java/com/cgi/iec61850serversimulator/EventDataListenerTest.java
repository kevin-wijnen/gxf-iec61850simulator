package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.beanit.openiec61850.ObjectReference;

public class EventDataListenerTest {

    private EventDataListener eventDataListener = new EventDataListener(null);

    @Test
    public void extractRelayScheduleSingleDigit() {
        final ObjectReference reference = new ObjectReference("SWDeviceGenericIO/XSWC4.Sche.sche4.Enabled");

        final int[] relaySchedule = this.eventDataListener.extractRelayScheduleNumbers(reference);

        assertEquals(4, relaySchedule[0]);
        assertEquals(4, relaySchedule[1]);
    }

    @Test
    public void extractRelayScheduleDoubleDigit() {
        final ObjectReference reference = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche58.Descr");

        final int[] relaySchedule = this.eventDataListener.extractRelayScheduleNumbers(reference);

        assertEquals(1, relaySchedule[0]);
        assertEquals(58, relaySchedule[1]);
    }

    @Test
    // TODO: Kevin zoekt uit, waar dit voor nodig is. De test verwijst nu niet
    // naar een eigen class.
    public void extractRelayScheduleDynamicDigit() {
        // Pattern: [0-9]{1,}
        final ObjectReference reference = new ObjectReference("SWDeviceGenericIO/XSWC111.Sche.sche45822.Descr");
        final int[] regexResults = new int[2];
        final Pattern numberPattern = Pattern.compile("[0-9]{1,}");
        final Matcher numberMatcher = numberPattern.matcher(reference.toString());
        // while (numberMatcher.find()) {

        for (int i = 0; i < 2; i++) {
            numberMatcher.find();
            regexResults[i] = Integer.parseInt(numberMatcher.group());
        }
        assertEquals(111, regexResults[0]);
        assertEquals(45822, regexResults[1]);
    }

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
        final ObjectReference referenceTenDigits = new ObjectReference("SWDeviceGenericIO/XSWC1.Sche.sche1111111110.Descr");
        final int scheduleIndexTenDigits = this.eventDataListener.extractScheduleIndex(referenceTenDigits);
        assertEquals(1111111110, scheduleIndexTenDigits);
    }
}
