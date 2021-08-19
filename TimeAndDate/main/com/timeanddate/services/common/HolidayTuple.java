package com.timeanddate.services.common;

import com.timeanddate.services.dataTypes.holidays.*;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class HolidayTuple {
	public String Command;
	public HolidayType EnumRepresentation;

	public HolidayTuple(String cmd, HolidayType enumRep) {
		Command = cmd;
		EnumRepresentation = enumRep;
	}
}
