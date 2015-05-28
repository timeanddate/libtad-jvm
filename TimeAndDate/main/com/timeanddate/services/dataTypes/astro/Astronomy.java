package com.timeanddate.services.dataTypes.astro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.*;

import com.timeanddate.services.common.MalformedXMLException;
import com.timeanddate.services.common.XmlUtils;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class Astronomy {
	private AstronomyObjectType _name;
	private List<AstronomyEvent> _events;
	private AstronomySpecial _special;
	
	/**
	 * Object name. Currently, the sun is the only supported astronomical
	 * object.
	 */
	public AstronomyObjectType getName() {
		return _name;
	}

	/**
	 * Lists all sunrise/sunset events during the day.
	 */
	public List<AstronomyEvent> getEvents() {
		return _events;
	}

	/**
	 * This element is only present if there are no astronomical events. In this
	 * case it will indicate if the sun is up or down the whole day.
	 */
	public AstronomySpecial getSpecial() {
		return _special;
	}

	/**
	 * This returns the hour and minute of the sunrise in DateTime format. If
	 * there is no sunrise, null will be returned, but the Special-property will
	 * say whether or not the sun is up or down.
	 * 
	 * @return Returns sunset or null if there is no sunset that day.
	 */
	public AstronomyEvent getSunset() throws NullPointerException {
		List<AstronomyEvent> sortedEvents = new ArrayList<>();
		Iterator<AstronomyEvent> events = _events.iterator();
		while (events.hasNext()) {
			AstronomyEvent event = events.next();
			if (event.getType() == AstronomyEventType.Set)
				sortedEvents.add(event);
		}

		Collections.sort(sortedEvents);
		AstronomyEvent event = null;

		if (sortedEvents.size() == 1)
			event = sortedEvents.get(0);

		if (sortedEvents.size() > 1)
			event = sortedEvents.get(sortedEvents.size() - 1);

		return event;
	}

	/**
	 * This returns the hour and minute of the sunrise in DateTime format. If
	 * there is no sunrise, null will be returned, but the Special-property will
	 * say whether or not the sun is up or down.
	 * 
	 * @return Returns sunrise or null if there is no sunrise that day.
	 */
	public AstronomyEvent getSunrise() {
		List<AstronomyEvent> sortedEvents = new ArrayList<>();
		Iterator<AstronomyEvent> events = _events.iterator();
		while(events.hasNext()) {
			AstronomyEvent event = events.next();
			if(event.getType() == AstronomyEventType.Rise) {
				sortedEvents.add(event);
			}
		}
		
		Collections.sort(sortedEvents);
		AstronomyEvent event = null;

		if (sortedEvents.size() == 1)
			event = sortedEvents.get(0);

		if (sortedEvents.size() > 1)
			event = sortedEvents.get(sortedEvents.size() - 1);

		return event;
	}

	public Astronomy() {
		_events = new ArrayList<AstronomyEvent>();
	}

	public static Astronomy fromNode(Node node) {
		Astronomy astro = new Astronomy();
		NamedNodeMap attr = node.getAttributes();
		Node name = attr.getNamedItem("name");
		NodeList nodes = node.getChildNodes();

		try {
			if (name != null) {
				astro._name = parseName(name.getTextContent());
			}
			
			for (Node n : XmlUtils.asList(nodes)) {
				switch (n.getNodeName()) {
				case "event":
					astro._events.add(AstronomyEvent.fromNode(n));
					break;
				case "special":
					astro._special = AstronomySpecial.fromNode(n);
					break;
				}
			}
		} catch (MalformedXMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return astro;
	}

	private static AstronomyObjectType parseName(String objectName) throws MalformedXMLException {
		switch (objectName) {
		case "sun":
			return AstronomyObjectType.Sun;
		case "moon":
			return AstronomyObjectType.Moon;
		default:
			throw new MalformedXMLException(
					"The XML Received from Time and Date did not include an object name which complies with an AstronomyObjectType enum: "
							+ objectName);
		}
	}
}
