package com.cgi.iec61850serversimulator.dataclass;

import java.util.HashMap;
import java.util.Map;

public enum TriggerType {

	FIXED(0),
	LIGHT_SENSOR(1),
	ASTRONOMIC(2);

	private int timeTypeInt;
	private static Map<Integer, TriggerType> map = new HashMap<>();

	private TriggerType(int timeTypeInt) {
		this.timeTypeInt = timeTypeInt;
	}

	static {
		for (TriggerType trigger : TriggerType.values()) {
			map.put(trigger.timeTypeInt, trigger);
		}
	}

	public static TriggerType valueOf(int timeTypeInt) {
		return map.get(timeTypeInt);
	}
}
