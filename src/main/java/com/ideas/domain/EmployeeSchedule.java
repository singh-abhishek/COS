package com.ideas.domain;

import java.sql.Time;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class EmployeeSchedule {
	private String username;
	private TreeMap<Date, HashMap<String, Time>> eventsDateMap;

	public EmployeeSchedule(String username,
			TreeMap<Date, HashMap<String, Time>> eventDateMap) {
		this.username = username;
		this.eventsDateMap = eventDateMap;
	}

	public String getUsername() {
		return username;
	}

	public TreeMap<Date, HashMap<String, Time>> getEventsDateMap() {
		return eventsDateMap;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (this.getClass() != other.getClass()) {
			return false;
		}
		EmployeeSchedule that = (EmployeeSchedule) other;
		if (this == that) {
			return true;
		}
		return this.username.equals(that.username)
				&& compareEventsDateMap(this.eventsDateMap, that.eventsDateMap);

	}

	private boolean compareEventsDateMap(
			TreeMap<Date, HashMap<String, Time>> eventsDateMap1,
			TreeMap<Date, HashMap<String, Time>> eventsDateMap2) {
		for (Entry<Date, HashMap<String, Time>> entry : eventsDateMap1
				.entrySet()) {
			Date key1 = entry.getKey();
			HashMap<String, Time> value1 = entry.getValue();
			HashMap<String, Time> value2 = new HashMap<String, Time>();
			if (eventsDateMap2.get(key1) != null) {
				value2 = eventsDateMap2.get(key1);
			} else {
				return false;
			}

			if (!value1.equals(value2)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + username.hashCode();
		hash = hash * 31 + eventsDateMap.hashCode();
		return hash;

	}

}
