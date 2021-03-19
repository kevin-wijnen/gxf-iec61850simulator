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

				for (int scheduleNr = 1; scheduleNr <= 50; scheduleNr++) {
					try {
						Schedule schedule = relay.getSchedule(scheduleNr);
						if (schedule.isEnabled()) {
							try {
								SwitchingMoment switchingMomentOn = this.calculateSwitchingMoment(relayNr, schedule,
										true, now);
								switchingMoments.add(switchingMomentOn);
								SwitchingMoment switchingMomentOn2 = this.calculateSwitchingMoment(relayNr, schedule,
										true, nextDay);
								switchingMoments.add(switchingMomentOn2);
							} catch (Exception e) {
								logger.info("No Switching Moment for On action created...");
								logger.info(e.toString());
							}

							try {
								SwitchingMoment switchingMomentOff = this.calculateSwitchingMoment(relayNr, schedule,
										false, now);
								switchingMoments.add(switchingMomentOff);
								SwitchingMoment switchingMomentOff2 = this.calculateSwitchingMoment(relayNr, schedule,
										false, nextDay);
								switchingMoments.add(switchingMomentOff2);
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

	public SwitchingMoment calculateSwitchingMoment(int relayNr, Schedule schedule, boolean triggerAction,
			LocalDateTime toCheckTime) {

		SwitchingMoment switchingMoment = new SwitchingMoment(relayNr, null, triggerAction);
		int day = schedule.getDayInt();
		DayOfWeek dayOfWeek = toCheckTime.getDayOfWeek();
		/*
		 * LocalDateTime now = LocalDateTime.now(); LocalDateTime nextday =
		 * now.plusDays(1); DayOfWeek day1 = now.getDayOfWeek(); DayOfWeek day2 =
		 * nextday.getDayOfWeek();
		 */
		switch (day) {
		// Check if day is within 2 days. Always recalculate every day.
		// TODO: Implementing every day, every week day and every weekend day
		case 0:
			// Trigger every day? No condition check?
			switchingMoment.setTriggerTime(toCheckTime);
			logger.info("Elke dag");
			break;

		case -1:
			// Check at Monday, Tuesday, Wednesday, Thursday, Friday
			if (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.WEDNESDAY
					|| dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
				logger.info("Elke werkdag");
			} else {
				logger.info("Given date is not on a weekday, no switching moment.");
			}
			break;

		case -2:
			// Check at Saturday, Sunday

			if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
				logger.info("Elke weekenddag");
			} else {
				logger.info("Given date is not on weekend day, no switching moment.", DayOfWeek.MONDAY);
			}
			break;

		case 1:
			if (dayOfWeek == DayOfWeek.MONDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.MONDAY);
			}
			break;

		case 2:
			if (dayOfWeek == DayOfWeek.TUESDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.TUESDAY);
			}
			break;

		case 3:
			if (dayOfWeek == DayOfWeek.WEDNESDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.WEDNESDAY);
			}
			break;

		case 4:
			if (dayOfWeek == DayOfWeek.THURSDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.THURSDAY);
			}
			break;

		case 5:
			if (dayOfWeek == DayOfWeek.FRIDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.FRIDAY);
			}
			break;

		case 6:
			if (dayOfWeek == DayOfWeek.SATURDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.SATURDAY);
			}
			break;

		case 7:
			if (dayOfWeek == DayOfWeek.SUNDAY) {
				switchingMoment.setTriggerTime(toCheckTime);
			} else {
				logger.info("Given date is not on {0}, no switching moment.", DayOfWeek.SUNDAY);
			}
			break;

		default:
			// With specific date
			logger.info("Datum... (yyyymmdd)");
			// Kijken naar formaat: YYYY, MM, DD
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
			String stringDate = String.valueOf(day);
			LocalDateTime triggerDate = LocalDateTime.parse(stringDate, dateFormat);
			switchingMoment.setTriggerTime(triggerDate);

			// Nog doen: triggerDate returnen! Al aan SwitchingMoment geven om dat te laten
			// returnen?
		}
		LocalDateTime triggerTime = switchingMoment.getTriggerTime();
		// Uur/minuut optellen

		if (triggerAction) {
			LocalTime timeOn = schedule.getTimeOn();
			LocalDate triggerDate = triggerTime.toLocalDate();
			triggerTime = LocalDateTime.of(triggerDate, timeOn);
			switchingMoment.setTriggerTime(triggerTime);
		}

		else if (!triggerAction) {
			LocalTime timeOff = schedule.getTimeOff();
			LocalDate triggerDate = triggerTime.toLocalDate();
			triggerTime = LocalDateTime.of(triggerDate, timeOff);
			switchingMoment.setTriggerTime(triggerTime);
		}

		return switchingMoment;
	}
}
