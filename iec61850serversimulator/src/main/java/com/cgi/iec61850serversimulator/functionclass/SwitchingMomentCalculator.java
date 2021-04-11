package com.cgi.iec61850serversimulator.functionclass;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.iec61850serversimulator.dataclass.Device;
import com.cgi.iec61850serversimulator.dataclass.Relay;
import com.cgi.iec61850serversimulator.dataclass.Schedule;
import com.cgi.iec61850serversimulator.dataclass.ScheduleDay;
import com.cgi.iec61850serversimulator.dataclass.SwitchingMoment;

public class SwitchingMomentCalculator {
    // Steps:
    // Accept device
    // Reads schedules from device
    // Checks on, off time and action type to create SwitchingMoments by
    // assigning
    // relay Number, TriggerTime and TriggerType

    private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculator.class);

    public List<SwitchingMoment> returnSwitchingMoments(final Device device, final LocalDateTime dateTime)
            throws SwitchingMomentCalculationException {
        final List<SwitchingMoment> switchingMoments = new ArrayList<>();
        // Calculating switching moments for given day and the day after
        final LocalDateTime nextDay = dateTime.plusDays(1);
        int relayNr = 0;
        int scheduleNr = 0;

        try {
            for (relayNr = 1; relayNr <= device.getRelays().length; relayNr++) {
                final Relay relay = device.getRelay(relayNr);
                for (scheduleNr = 1; scheduleNr <= relay.getSchedules().length; scheduleNr++) {
                    final Schedule schedule = relay.getSchedule(scheduleNr);
                    if (schedule.isEnabled()) {
                        final SwitchingMoment actualSwitchingMomentOn = this.calculateSwitchingMoment(relayNr, schedule,
                                true, dateTime);
                        switchingMoments.add(actualSwitchingMomentOn);
                        final SwitchingMoment actualSwitchingMomentOn2 = this.calculateSwitchingMoment(relayNr,
                                schedule, true, nextDay);
                        switchingMoments.add(actualSwitchingMomentOn2);

                        final SwitchingMoment actualSwitchingMomentOff = this.calculateSwitchingMoment(relayNr,
                                schedule, false, dateTime);
                        switchingMoments.add(actualSwitchingMomentOff);
                        final SwitchingMoment actualSwitchingMomentOff2 = this.calculateSwitchingMoment(relayNr,
                                schedule, false, nextDay);
                        switchingMoments.add(actualSwitchingMomentOff2);
                    }
                }
            }
        } catch (final Exception e) {
            throw new SwitchingMomentCalculationException(
                    "Switching Moment calculation error at Relay " + relayNr + " Schedule " + scheduleNr, e);
        }
        return switchingMoments;

    }

    private SwitchingMoment calculateSwitchingMoment(final int relayNr, final Schedule schedule,
            final boolean triggerAction, final LocalDateTime toCheckTime) {

        final SwitchingMoment actualSwitchingMoment = new SwitchingMoment(relayNr, null, triggerAction);
        final int scheduledOccurrence = schedule.getDayInt();
        final DayOfWeek dayOfWeek = toCheckTime.getDayOfWeek();

        switch (ScheduleDay.valueOf(scheduledOccurrence)) {
        case EVERY_DAY:
            actualSwitchingMoment.setTriggerTime(toCheckTime);
            logger.info("Every day occurrence");
            break;

        case WEEKDAY:
            // Check at Monday, Tuesday, Wednesday, Thursday, Friday
            if (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.WEDNESDAY
                    || dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
                logger.info("Every weekday occurrence");
            } else {
                logger.info("Given date is not on a weekday, no switching moment.");
            }
            break;

        case WEEKEND_DAY:
            // Check at Saturday, Sunday

            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
                logger.info("Every weekend day occurrence");
            } else {
                logger.info("Given date is not on weekend day, no switching moment.");
            }
            break;

        case MONDAY:
            if (dayOfWeek == DayOfWeek.MONDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
            } else {
                logger.info("Given date is not on {}, no switching moment.", DayOfWeek.MONDAY);
            }
            break;

        case TUESDAY:
            if (dayOfWeek == DayOfWeek.TUESDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
            } else {
                logger.info("Given date is not on {}, no switching moment.", DayOfWeek.TUESDAY);
            }
            break;

        case WEDNESDAY:
            if (dayOfWeek == DayOfWeek.WEDNESDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
            } else {
                logger.info("Given date is not on {}, no switching moment.", DayOfWeek.WEDNESDAY);
            }
            break;

        case THURSDAY:
            if (dayOfWeek == DayOfWeek.THURSDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
            } else {
                logger.info("Given date is not on {}, no switching moment.", DayOfWeek.THURSDAY);
            }
            break;

        case FRIDAY:
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
            } else {
                logger.info("Given date is not on {}, no switching moment.", DayOfWeek.FRIDAY);
            }
            break;

        case SATURDAY:
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
            } else {
                logger.info("Given date is not on {}, no switching moment.", DayOfWeek.SATURDAY);
            }
            break;

        case SUNDAY:
            if (dayOfWeek == DayOfWeek.SUNDAY) {
                actualSwitchingMoment.setTriggerTime(toCheckTime);
            } else {
                logger.info("Given date is not on {}, no switching moment.", DayOfWeek.SUNDAY);
            }
            break;

        // When a static date is scheduled instead of a (set of) re-occurring
        // day(s),
        // use the default case
        default:
            // With specific date
            logger.info("Datum... (yyyymmdd)");
            // Look at format: Year-Month-Day

            // TODO: Formatting/converting should only happen when receiving a
            // value
            final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
            final String stringDate = String.valueOf(scheduledOccurrence);
            final LocalDateTime triggerDate = LocalDateTime.parse(stringDate, dateFormat);
            actualSwitchingMoment.setTriggerTime(triggerDate);

            // Nog doen: triggerDate returnen! Al aan SwitchingMoment geven om
            // dat te laten
            // returnen?
        }
        LocalDateTime triggerTime = actualSwitchingMoment.getTriggerTime();
        // Uur/minuut optellen

        if (triggerAction) {
            final LocalTime timeOn = schedule.getTimeOn();
            final LocalDate triggerDate = triggerTime.toLocalDate();
            triggerTime = LocalDateTime.of(triggerDate, timeOn);
            actualSwitchingMoment.setTriggerTime(triggerTime);
        }

        else {
            final LocalTime timeOff = schedule.getTimeOff();
            final LocalDate triggerDate = triggerTime.toLocalDate();
            triggerTime = LocalDateTime.of(triggerDate, timeOff);
            actualSwitchingMoment.setTriggerTime(triggerTime);
        }

        return actualSwitchingMoment;
    }
}
