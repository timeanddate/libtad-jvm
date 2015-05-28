package com.timeanddate.services.dataTypes.astro;

import java.util.concurrent.TimeUnit;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.timeanddate.services.common.MalformedXMLException;
import com.timeanddate.services.common.TimeSpan;

public class AstronomyEvent implements Comparable<AstronomyEvent> {
	private TimeSpan _time;
	public AstronomyEventType _type;
	
	/**
	 * Indicates the type of the event.
	 */
	public AstronomyEventType getType() {
		return _type;
	}

	/**
	 * Local time at which the event is happening
	 */
	public TimeSpan getTime() {
		return _time;
	}

	public static AstronomyEvent fromNode(Node node) throws Exception {
		AstronomyEvent event = new AstronomyEvent();
		NamedNodeMap attr = node.getAttributes();
		Node type = attr.getNamedItem("type");
		Node hour = attr.getNamedItem("hour");
		Node minute = attr.getNamedItem("minute");

		if (type != null && type.getTextContent() != "") {
			switch (type.getTextContent()) {
			case "rise":
				event._type = AstronomyEventType.Rise;
				break;
			case "set":
				event._type = AstronomyEventType.Set;
				break;
			default:
				throw new MalformedXMLException(
						"The XML Received from Time and Date did not include an "
								+ "event type which complies with an AstronomyEventType enum: "
								+ type.getTextContent());
			}
		}

		int h = 0, m = 0;
		if (hour != null) {
			h = Integer.parseInt(hour.getTextContent());
		}

		if (minute != null) {
			m = Integer.parseInt(hour.getTextContent());
		}

		event._time = new TimeSpan(h, m, 0);
		return event;
	}

	@Override
	public int compareTo(AstronomyEvent arg) {
		long th = TimeUnit.HOURS.toMillis(this._time.getHours());
		long tm = TimeUnit.MINUTES.toMillis(this._time.getMinutes());
		long ah = TimeUnit.HOURS.toMillis(arg._time.getHours());
		long am = TimeUnit.MINUTES.toMillis(arg._time.getMinutes());

		long thisHm = th + tm;
		long argHm = ah + am;

		return Long.compare(thisHm, argHm);
	}
}
