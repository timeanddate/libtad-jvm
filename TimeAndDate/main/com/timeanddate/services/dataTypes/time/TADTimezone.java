package com.timeanddate.services.dataTypes.time;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.TimeSpan;
import com.timeanddate.services.common.XmlUtils;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class TADTimezone {
	private String _abbrevation;
	private String _name;
	private TimeSpan _offset;
	private int _basicOffset;
	private int _dSTOffset;
	private int _totalOffset;
	
	/**
	 * Abbreviated timezone name.
	 *
	 * Example: LHDT
	 */
	public String getAbbrevation() {
		return _abbrevation == null ? "" : _abbrevation;
	}

	/**
	 * Full timezone name.
	 *
	 * Example: Lord Howe Daylight Time
	 */
	public String getName() {
		return _name == null ? "" : _name;
	}

	/**
	 * The timezone offset (from UTC) as TimeSpan.
	 */
	public TimeSpan getOffset() {
		return _offset;
	}

	/**
	 * Basic timezone offset (without DST) in seconds.
	 */
	public int getBasicOffset() {
		return _basicOffset;
	}

	/**
	 * DST component of timezone offset in seconds.
	 */
	public int getDSTOffset() {
		return _dSTOffset;
	}

	/**
	 * Total offset from UTC in seconds.
	 */
	public int getTotalOffset() {
		return _totalOffset;
	}

	public static TADTimezone fromNode(Node node) {
		TADTimezone tz = new TADTimezone();
		NodeList nodes = node.getChildNodes();
		NamedNodeMap attr = node.getAttributes();
		Node offset = attr.getNamedItem("offset");

		for (Node n : XmlUtils.asList(nodes)) {
			switch (n.getNodeName()) {
			case "zoneabb":
				tz._abbrevation = n.getTextContent();
				break;
			case "zonename":
				tz._name = n.getTextContent();
				break;
			case "zoneoffset":
				tz._basicOffset = Integer.parseInt(n.getTextContent());
				break;
			case "zonedst":
				tz._dSTOffset = Integer.parseInt(n.getTextContent());
				break;
			case "zonetotaloffset":
				tz._totalOffset = Integer.parseInt(n.getTextContent());
				break;
			default:
				break;
			}
		}

		if (offset != null) {
			String[] components = offset.getTextContent().split(":");
			int hour = Integer.parseInt(components[0]);
			int minute = Integer.parseInt(components[1]);
			tz._offset = new TimeSpan(hour, minute, 0);
		}

		return tz;
	}
}
