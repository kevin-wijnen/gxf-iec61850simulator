package com.cgi.iec61850serversimulator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SwitchingMomentCalculator {
	// Steps:
	// Accept device
	// Reads schedules from device
	// Checks on, off time and action type to create SwitchingMoments by assigning
	// relay Number, TriggerTime and TriggerType

	public List<SwitchingMoment> calculateSwitchingMoments(Device device) {
		List<SwitchingMoment> switchingMoments = new ArrayList<SwitchingMoment>();
		for (int i = 1; i <= 4; i++) {
			Relay relay = device.getRelay(i);

			for (int j = 1; j <= 50; j++) {
				Schedule schedule = relay.getSchedule(j);
				if (schedule.isEnabled())
					try {
						SwitchingMoment switchingMomentOn = this.calculateSwitchingMoment(i, schedule, true);
						switchingMoments.add(switchingMomentOn);
					} catch (Exception e) {
						System.out.println("No Switching Moment for On action created...");
						System.out.println(e.toString());
					}

				try {
					SwitchingMoment switchingMomentOff = this.calculateSwitchingMoment(i, schedule, false);
					switchingMoments.add(switchingMomentOff);
				} catch (Exception e) {
					System.out.println("No Switching Moment for Off action created...");
					System.out.println(e.toString());

				}

			}
		}
		return switchingMoments;
	}

	public SwitchingMoment calculateSwitchingMoment(int relayNr, Schedule schedule, boolean triggerAction) {

		SwitchingMoment switchingMoment = new SwitchingMoment(relayNr, null, triggerAction);
		int day = schedule.getDayInt();

		switch (day) {
		// For days: Checking date of now, and checking if day corresponds with the day
		// of date?
		case 0:
			System.out.println("Elke dag, niet geimplementeerd");

		case -1:
			System.out.println("Elke werkdag, niet geimplementeerd");

		case -2:
			System.out.println("Elke weekenddag, niet geimplementeerd");

		case 1:
			System.out.println("Maandag, niet geimplementeerd");

		case 2:
			System.out.println("Dinsdag, niet geimplementeerd");

		case 3:
			System.out.println("Woensdag, niet geimplementeerd");

		case 4:
			System.out.println("Donderdag, niet geimplementeerd");

		case 5:
			System.out.println("Vrijdag, niet geimplementeerd");

		case 6:
			System.out.println("Zaterdag, niet geimplementeerd");

		case 7:
			System.out.println("Zondag, niet geimplementeerd");

		default:
			// With specific date
			System.out.println("Datum... (yyyymmdd)");
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
