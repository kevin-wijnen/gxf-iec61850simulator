package com.cgi.iec61850serversimulator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchingMomentCalculator {
	// Steps:
	// Accept device
	// Reads schedules from device
	// Checks on, off time and action type to create SwitchingMoments by assigning
	// relay Number, TriggerTime and TriggerType

	private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculator.class);

	public List<SwitchingMoment> calculateSwitchingMoments(Device device) {
		List<SwitchingMoment> switchingMoments = new ArrayList<SwitchingMoment>();
		// Calculating switching moments for today and tomorrow
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextDay = now.plusDays(1);

		for (int relayNr = 1; relayNr <= 4; relayNr++) {
			try {
				Relay relay = device.getRelay(relayNr);

				for (int scheduleNr = 1; scheduleNr <= relay.getSchedules().length; scheduleNr++) {
					try {
						Schedule schedule = relay.getSchedule(scheduleNr);
						if (schedule.isEnabled()) {
							try {
								SwitchingMoment actualSwitchingMomentOn = this.calculateSwitchingMoment(relayNr,
										schedule, true, now);
								switchingMoments.add(actualSwitchingMomentOn);
								SwitchingMoment actualSwitchingMomentOn2 = this.calculateSwitchingMoment(relayNr,
										schedule, true, nextDay);
								switchingMoments.add(actualSwitchingMomentOn2);
							} catch (Exception e) {
								logger.info("No Switching Moment for On action created...");
								logger.info(e.toString());
							}

							try {
								SwitchingMoment actualSwitchingMomentOff = this.calculateSwitchingMoment(relayNr,
										schedule, false, now);
								switchingMoments.add(actualSwitchingMomentOff);
								SwitchingMoment actualSwitchingMomentOff2 = this.calculateSwitchingMoment(relayNr,
										schedule, false, nextDay);
								switchingMoments.add(actualSwitchingMomentOff2);
							} catch (Exception e) {
								logger.info("No Switching Moment for Off action created...");
								logger.info(e.toString());

							}
						}
					} catch (Exception e) {
						logger.info("No enabled schedule found, number {}.", scheduleNr);
					}
				}
			} catch (Exception e) {
				logger.info("No relay found, number {}", relayNr);
			}
		}
		return switchingMoments;
	}

	private SwitchingMoment calculateSwitchingMoment(int relayNr, Schedule schedule, boolean triggerAction,
			LocalDateTime toCheckTime) {

		SwitchingMoment actualSwitchingMoment = new SwitchingMoment(relayNr, null, triggerAction);
		int scheduledOccurence = schedule.getDayInt();
		DayOfWeek dayOfWeek = toCheckTime.getDayOfWeek();
		/*
		 * LocalDateTime now = LocalDateTime.now(); LocalDateTime nextday =
		 * now.plusDays(1); DayOfWeek day1 = now.getDayOfWeek(); DayOfWeek day2 =
		 * nextday.getDayOfWeek();
		 */

		// TODO: Switching to ScheduleDay Enum

		switch (ScheduleDay.valueOf(scheduledOccurence)) {
		case EVERY_DAY:
			// Trigger every day? No condition check?
			actualSwitchingMoment.setTriggerTime(toCheckTime);
			logger.info("Elke dag");
			break;

		case WEEKDAY:
			// Check at Monday, Tuesday, Wednesday, Thursday, Friday
			if (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.WEDNESDAY
					|| dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
				logger.info("Elke werkdag");
			} else {
				logger.info("Given date is not on a weekday, no switching moment.");
			}
			break;

		case WEEKEND_DAY:
			// Check at Saturday, Sunday

			if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
				logger.info("Elke weekenddag");
			} else {
				logger.info("Given date is not on weekend day, no switching moment.", DayOfWeek.MONDAY);
			}
			break;

		case MONDAY:
			if (dayOfWeek == DayOfWeek.MONDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.MONDAY);
			}
			break;

		case TUESDAY:
			if (dayOfWeek == DayOfWeek.TUESDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.TUESDAY);
			}
			break;

		case WEDNESDAY:
			if (dayOfWeek == DayOfWeek.WEDNESDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.WEDNESDAY);
			}
			break;

		case THURSDAY:
			if (dayOfWeek == DayOfWeek.THURSDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.THURSDAY);
			}
			break;

		case FRIDAY:
			if (dayOfWeek == DayOfWeek.FRIDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.FRIDAY);
			}
			break;

		case SATURDAY:
			if (dayOfWeek == DayOfWeek.SATURDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.SATURDAY);
			}
			break;

		case SUNDAY:
			if (dayOfWeek == DayOfWeek.SUNDAY) {
				actualSwitchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.SUNDAY);
			}
			break;

		// When a static date is scheduled instead of a (set of) re-occurring day(s),
		// use the default case
		default:
			// With specific date
			logger.info("Datum... (yyyymmdd)");
			// Look at format: Year-Month-Day

			// TODO: Formatting/converting should only happen when receiving a value
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
			String stringDate = String.valueOf(scheduledOccurence);
			LocalDateTime triggerDate = LocalDateTime.parse(stringDate, dateFormat);
			actualSwitchingMoment.setTriggerTime(triggerDate);

			// Nog doen: triggerDate returnen! Al aan SwitchingMoment geven om dat te laten
			// returnen?
		}
		LocalDateTime triggerTime = actualSwitchingMoment.getTriggerTime();
		// Uur/minuut optellen

		if (triggerAction) {
			LocalTime timeOn = schedule.getTimeOn();
			LocalDate triggerDate = triggerTime.toLocalDate();
			triggerTime = LocalDateTime.of(triggerDate, timeOn);
			actualSwitchingMoment.setTriggerTime(triggerTime);
		}

		else {
			LocalTime timeOff = schedule.getTimeOff();
			LocalDate triggerDate = triggerTime.toLocalDate();
			triggerTime = LocalDateTime.of(triggerDate, timeOff);
			actualSwitchingMoment.setTriggerTime(triggerTime);
		}

		return actualSwitchingMoment;
	}
}
