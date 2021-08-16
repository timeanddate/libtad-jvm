package com.timeanddate.services.common;

import com.timeanddate.services.dataTypes.onthisday.OTDEventType;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class OTDEventTuple {
	public String Command;
	public OTDEventType EnumRepresentation;

	public OTDEventTuple(String cmd, OTDEventType enumRep) {
		Command = cmd;
		EnumRepresentation = enumRep;
	}
}
