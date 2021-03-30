package com.cgi.iec61850serversimulator.dataclasses;

import java.util.HashMap;
import java.util.Map;

public enum ScheduleDay {

	EVERY_DAY(0),
	WEEKDAY(-1),
	WEEKEND_DAY(-2),
	MONDAY(1),
	TUESDAY(2),
	WEDNESDAY(3),
	THURSDAY(4),
	FRIDAY(5),
	SATURDAY(6),
	SUNDAY(7);

	private int dayInt;
	private static Map<Integer, ScheduleDay> map = new HashMap<>();

	private ScheduleDay(int dayInt) {
		this.dayInt = dayInt;
	}

	static {
		for (ScheduleDay day : ScheduleDay.values()) {
			map.put(day.dayInt, day);
		}
	}

	public static ScheduleDay valueOf(int dayInt) {
		return map.get(dayInt);
	}
}
