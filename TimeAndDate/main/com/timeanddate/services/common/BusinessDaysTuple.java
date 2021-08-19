package com.timeanddate.services.common;

import com.timeanddate.services.dataTypes.businessdays.*;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class BusinessDaysTuple {
	public String Command;
	public BusinessDaysFilterType EnumRepresentation;

	public BusinessDaysTuple(String cmd, BusinessDaysFilterType enumRep) {
		Command = cmd;
		EnumRepresentation = enumRep;
	}
}
