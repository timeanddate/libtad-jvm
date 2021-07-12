package com.timeanddate.services.common;

import com.timeanddate.services.dataTypes.onthisday.EventType;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class EventTuple {
	public String Command;
	public EventType EnumRepresentation;

	public EventTuple(String cmd, EventType enumRep) {
		Command = cmd;
		EnumRepresentation = enumRep;
	}
}
