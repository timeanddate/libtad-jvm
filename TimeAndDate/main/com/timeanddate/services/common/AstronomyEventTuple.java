package com.timeanddate.services.common;

import com.timeanddate.services.dataTypes.astro.AstronomyEventClass;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class AstronomyEventTuple {
	public String Command;
	public AstronomyEventClass EnumRepresentation;

	public AstronomyEventTuple(String cmd, AstronomyEventClass enumRep) {
		Command = cmd;
		EnumRepresentation = enumRep;
	}
}
