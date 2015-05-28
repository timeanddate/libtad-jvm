package com.timeanddate.services.dataTypes.astro;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.TimeSpan;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.time.TADDateTime;

public class AstronomyDay {
	private TADDateTime _date;
	private TimeSpan _dayLength;
	private MoonPhase _moonPhase;
	private List<AstronomyDayEvent> _events;
	
	/**
	 * Date for the current information.
	 */
	public TADDateTime getDate() {
		return _date;
	}

	/**
	 * Length of this day (time between sunrise and sunset). If the sun is not
	 * up on this day, 00:00 will reported. If the sun does not set on this day,
	 * the value will read 24:00.
	 */
	public TimeSpan DayLength() {
		return _dayLength;
	}

	/**
	 * Moon phase for the day. Only if requested.
	 */
	public MoonPhase getMoonPhase() {
		return _moonPhase;
	}

	/**
	 * Lists all events during the day.
	 */
	public List<AstronomyDayEvent> getEvents() {
		return _events;
	}

	private AstronomyDay() {
		_events = new ArrayList<AstronomyDayEvent>();
	}

	public static AstronomyDay fromNode(Node node) throws DOMException,
			Exception {
		AstronomyDay day = new AstronomyDay();
		NamedNodeMap attr = node.getAttributes();
		NodeList children = node.getChildNodes();
		Node date = attr.getNamedItem("date");
		Node daylength = attr.getNamedItem("daylength");
		Node moonphase = attr.getNamedItem("moonphase");

		if (date != null)
			day._date = new TADDateTime(date.getTextContent());

		if (daylength != null) {
			String[] components = daylength.getTextContent().split(":");
			int hour = Integer.parseInt(components[0]);
			int minute = Integer.parseInt(components[1]);
			day._dayLength = new TimeSpan(hour, minute, 0);
		}

		if (moonphase != null)
			day._moonPhase = com.timeanddate.services.dataTypes.astro.MoonPhase
					.valueOf(moonphase.getTextContent());
		else
			day._moonPhase = MoonPhase.NOTREQUESTED;

		for (Node child : XmlUtils.asList(children)) {
			if (child.getNodeName() == "event") {
				day._events.add(AstronomyDayEvent.fromNode(child));
			}
		}

		return day;

	}
}
