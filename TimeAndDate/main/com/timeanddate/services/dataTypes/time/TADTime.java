package com.timeanddate.services.dataTypes.time;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;

public class TADTime {
	private String _ISO;
	private TADDateTime _datetime;
	private TADTimezone _tz;
	
	/**
	 * ISO representation of date and time, timezone included if different from
	 * UTC. If time is not applicable, only the date is shown.
	 *
	 * Example: 2011-06-08T09:18:16+02:00
	 *
	 * Example: 2011-06-08T07:18:16 (UTC time)
	 *
	 * Example: 2011-06-08 (only date)
	 */
	public String getISO() {
		return _ISO == null ? "" : _ISO;
	}

	/**
	 * Date and time representation of the ISO string.
	 */
	public TADDateTime getDateTime() {
		return _datetime;
	}

	/**
	 * Timezone information. Element is only present if different from UTC and
	 * requested by specifying the IncludeTimezoneInformation parameter.
	 */
	public TADTimezone getTimezone() {
		return _tz;
	}

	public static TADTime fromNode(Node node) {
		TADTime time = new TADTime();

		NodeList nodes = node.getChildNodes();
		NamedNodeMap attr = node.getAttributes();
		Node iso = attr.getNamedItem("iso");

		if (iso != null) {
			time._ISO = iso.getTextContent();
		}

		for (Node n : XmlUtils.asList(nodes)) {
			switch (n.getNodeName()) {
			case "timezone":
				time._tz = TADTimezone.fromNode(n);
				break;
			case "datetime":
				time._datetime = handleDatetime(n);
				break;
			}
		}

		if (time._datetime == null && iso != null) {
			time._datetime = new TADDateTime(iso.getTextContent());
		}

		return time;
	}

	private static TADDateTime handleDatetime(Node datetime) {
		NodeList nodes = datetime.getChildNodes();
		int year, month, day, hour, minute, second;
		year = month = day = hour = minute = second = 0;

		for (Node n : XmlUtils.asList(nodes)) {
			switch (n.getNodeName()) {
			case "year":
				year = Integer.parseInt(n.getTextContent());
				break;
			case "month":
				month = Integer.parseInt(n.getTextContent());
				break;
			case "day":
				day = Integer.parseInt(n.getTextContent());
				break;
			case "hour":
				hour = Integer.parseInt(n.getTextContent());
				break;
			case "minute":
				minute = Integer.parseInt(n.getTextContent());
				break;
			case "second":
				second = Integer.parseInt(n.getTextContent());
				break;
			default:
				break;
			}
		}

		return new TADDateTime(year, month, day, hour, minute, second);
	}
}
