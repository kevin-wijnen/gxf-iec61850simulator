package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.iec61850serversimulator.Schedule.ScheduleBuilder;

class SwitchingMomentCalculatorTest {
    /**
     * JUnit test class which tests the various related functions of the
     * SwitchingMomentCalculator class.
     *
     * This includes unique functions for mocking a relay and schedule.
     *
     * Tests include:
     * <ul>
     * <li>Checking the mocked relay
     * <li>Checking the mocked schedule
     * <li>Checking the generated Switching Moment made out of a mocked relay
     * and schedule
     * </ul>
     */

    private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculatorTest.class);

    // Initializing test Device
    Device device = new Device();

    @Test
    public void checkMockRelay() {
        // Mock Relay test
        final int relayNr = 1;

        final Relay relay = this.getMockRelay(relayNr - 1, null);
        assertEquals(relay.getIndexNumber(), 1);
    }

    @Test
    public void checkMockSchedule() {
        // Mock Schedule test
        final int scheduleNr = 1;
        final int relayNr = 1;
        // General time type for both on and off times
        // 0 = fixed time, 1 = light sensor, 2 = astronomical time
        // -1 = disabled action
        final int fixedTimeInt = 0;
        final LocalTime timeOn = LocalTime.of(12, 00);
        final LocalTime timeOff = LocalTime.of(13, 00);
        final int burningMinutes = 30;
        final boolean enabled = true;

        final Schedule schedule = this.builderForDefaultSchedule(scheduleNr).buildSchedule();

        // Schedule schedule = this.getMockSchedule(scheduleNr - 1, relayNr,
        // dayInt,
        // fixedTimeInt, fixedTimeOn,
        // fixedTimeOff, timeOn, timeOff, burningMinutes);

        assertEquals(scheduleNr, schedule.getIndexNumber());
        assertEquals(relayNr, schedule.getRelayNr());
        assertEquals(fixedTimeInt, schedule.getTimeOnTypeInt());
        assertEquals(fixedTimeInt, schedule.getTimeOffTypeInt());
        assertEquals(timeOn, schedule.getTimeOn());
        assertEquals(timeOff, schedule.getTimeOff());
        assertEquals(burningMinutes, schedule.getBurningMinsOn());
        assertEquals(enabled, schedule.isEnabled());

    }

    @Test
    public void calculateEveryDaySwitchingMoments() throws SwitchingMomentCalculationException {

        final int scheduleNr = 1;
        final int relayNr = 1;

        // Initializing mock relay
        // Custom schedule separated test!
        final Schedule schedule = this.getMockLunchTimeSchedule(scheduleNr);
        final Relay relay = this.getMockRelay(relayNr, schedule);
        final Relay[] relays = new Relay[1];
        relays[0] = relay;
        this.device.setRelays(relays);

        // Calculating switching moment

        final SwitchingMomentCalculator calculator = new SwitchingMomentCalculator();
        final List<SwitchingMoment> switchingMoments = calculator.returnSwitchingMoments(this.device);

        // Tests

        final SwitchingMoment actualSwitchingMomentOn = switchingMoments.get(0);
        final SwitchingMoment actualSwitchingMomentOff = switchingMoments.get(2);
        final SwitchingMoment actualSwitchingMomentNextDayOn = switchingMoments.get(1);
        final SwitchingMoment actualSwitchingMomentNextDayOff = switchingMoments.get(3);

        final LocalDateTime expectedLunchTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).withHour(12);
        assertEquals(expectedLunchTime, actualSwitchingMomentOn.getTriggerTime());
        assertTrue(actualSwitchingMomentOn.isTriggerAction());

        final LocalDateTime expectedAfterLunchTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).withHour(13);
        assertEquals(expectedAfterLunchTime, actualSwitchingMomentOff.getTriggerTime());
        assertFalse(actualSwitchingMomentOff.isTriggerAction());

        final LocalDateTime expectedNextLunchTime = LocalDateTime.now()
                .plusDays(1)
                .truncatedTo(ChronoUnit.HOURS)
                .withHour(12);
        assertEquals(expectedNextLunchTime, actualSwitchingMomentNextDayOn.getTriggerTime());
        assertTrue(actualSwitchingMomentOn.isTriggerAction());

        final LocalDateTime expectedNextAfterLunchTime = LocalDateTime.now()
                .plusDays(1)
                .truncatedTo(ChronoUnit.HOURS)
                .withHour(13);
        assertEquals(expectedNextAfterLunchTime, actualSwitchingMomentNextDayOff.getTriggerTime());
        assertFalse(actualSwitchingMomentOff.isTriggerAction());

    }

    @Test
    public void calculateCustomScheduleSwitchingMoments() throws SwitchingMomentCalculationException {

        // Custom schedule
        // Set up your own schedule with the variables

        final int scheduleNr = 1;
        final int relayNr = 1;
        final LocalTime timeOn = LocalTime.of(12, 00);
        final LocalTime timeOff = LocalTime.of(23, 33);

        // Custom schedule initialization
        final Schedule customSchedule = this.builderForDefaultSchedule(scheduleNr).timeOff(timeOff).buildSchedule();

        // Initializing mock relay

        final Relay relay = this.getMockRelay(relayNr, customSchedule);
        final Relay[] relays = new Relay[1];
        relays[0] = relay;
        this.device.setRelays(relays);

        // Calculating switching moment

        final SwitchingMomentCalculator calculator = new SwitchingMomentCalculator();
        final List<SwitchingMoment> switchingMoments = calculator.returnSwitchingMoments(this.device);

        // Tests

        final SwitchingMoment actualSwitchingMomentOn = switchingMoments.get(0);
        final SwitchingMoment actualSwitchingMomentOff = switchingMoments.get(2);
        final SwitchingMoment actualSwitchingMomentNextDayOn = switchingMoments.get(1);
        final SwitchingMoment actualSwitchingMomentNextDayOff = switchingMoments.get(3);

        final LocalDateTime expectedTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES)
                .withHour(timeOn.getHour())
                .withMinute(timeOn.getMinute());
        assertEquals(expectedTime, actualSwitchingMomentOn.getTriggerTime());
        assertTrue(actualSwitchingMomentOn.isTriggerAction());

        final LocalDateTime expectedAfterTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES)
                .withHour(timeOff.getHour())
                .withMinute(timeOff.getMinute());
        assertEquals(expectedAfterTime, actualSwitchingMomentOff.getTriggerTime());
        assertFalse(actualSwitchingMomentOff.isTriggerAction());

        final LocalDateTime expectedNextTime = LocalDateTime.now()
                .plusDays(1)
                .truncatedTo(ChronoUnit.MINUTES)
                .withHour(timeOn.getHour())
                .withMinute(timeOn.getMinute());
        assertEquals(expectedNextTime, actualSwitchingMomentNextDayOn.getTriggerTime());
        assertTrue(actualSwitchingMomentOn.isTriggerAction());

        final LocalDateTime expectedNextAfterTime = LocalDateTime.now()
                .plusDays(1)
                .truncatedTo(ChronoUnit.MINUTES)
                .withHour(timeOff.getHour())
                .withMinute(timeOff.getMinute());
        assertEquals(expectedNextAfterTime, actualSwitchingMomentNextDayOff.getTriggerTime());
        assertFalse(actualSwitchingMomentOff.isTriggerAction());

    }

    public Relay getMockRelay(final int relayNr, final Schedule customSchedule) {
        final Relay relay = new Relay(relayNr);

        final Schedule[] schedules = new Schedule[1];

        // Making mock fixed time schedule.
        if (customSchedule == null) {
            // Default mock schedule:
            // On time: 12 PM
            // Off time: 1 PM
            // Burning Minutes: 30
            final Schedule schedule = this.getMockLunchTimeSchedule(1);
            schedules[0] = schedule;
        }

        else {
            schedules[0] = customSchedule;
        }

        relay.setSchedules(schedules);

        return relay;
    }

    public Schedule getMockLunchTimeSchedule(final int scheduleNr) {
        return this.builderForDefaultSchedule(scheduleNr).buildSchedule();
    }

    public ScheduleBuilder builderForDefaultSchedule(final int scheduleNr) {

        final int relayNr = 1;
        // Trigger day(s), see Enum in GXF LF Energy documentation
        // 0 = Every day
        final int dayInt = 0;
        // General time type for both on and off times
        final int fixedTimeInt = 0;
        // Optional: Specific time type for on time
        // -1 = using general time type
        final int fixedTimeOn = -1;
        // Optional: Specific time type for off time
        // -1 = using general time type
        final int fixedTimeOff = -1;
        final LocalTime timeOn = LocalTime.of(12, 00);
        final LocalTime timeOff = LocalTime.of(13, 00);
        final int burningMinutes = 30;
        final boolean enabled = true;

        return new ScheduleBuilder(scheduleNr - 1).relayNr(relayNr)
                .dayInt(dayInt)
                .fixedTimeInt(fixedTimeInt)
                .fixedTimeOn(fixedTimeOn)
                .fixedTimeOff(fixedTimeOff)
                .timeOn(timeOn)
                .timeOff(timeOff)
                .burningMins(burningMinutes)
                .isEnabled(enabled);

    }

    public ScheduleBuilder getDefaultSchedule() {
        return null;
    }
}
