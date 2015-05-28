package com.timeanddate.services.dataTypes.dst;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.time.TADDateTime;
import com.timeanddate.services.dataTypes.time.TADTimezone;
import com.timeanddate.services.dataTypes.time.TimeChange;
import com.timeanddate.services.dataTypes.places.Region;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class DST {
	public Region _region;
	public TADTimezone _standardTimezone;
	public TADTimezone _dstTimezone;
	public DSTSpecialType _special;
	public TADDateTime _dstStart;
	public TADDateTime _dstEnd;
	public List<TimeChange> _timeChanges;
	
	/**
	 * The geographical region where this information is valid. Contains
	 * country, a textual description of the region and the name of the biggest
	 * place.
	 */
	public Region getRegion() {
		return _region;
	}

	/**
	 * Information about the standard timezone. This element is always returned.
	 */
	public TADTimezone getStandardTimezone() {
		return _standardTimezone;
	}

	/**
	 * Information about the daylight savings timezone. Suppressed, if there are
	 * no DST changes in the queried year.
	 * 
	 * Please note that if the region is on daylight savings time for the whole
	 * year, this information will be returned in the stdtimezone element.
	 * Additionally, the Special element will be set to
	 * DaylightSavingTimeAllYear.
	 */
	public TADTimezone getDstTimezone() {
		return _dstTimezone;
	}

	/**
	 * Indicates if the region does not observe DST at all, or is on DST all
	 * year long.
	 */
	public DSTSpecialType getSpecial() {
		return _special;
	}

	/**
	 * Starting date of daylight savings time. Suppressed, if there are no DST
	 * changes in the queried year.
	 */
	public TADDateTime getDstStart() {
		return _dstStart;
	}

	/**
	 * Ending date of daylight savings time. Suppressed, if there are no DST
	 * changes in the queried year.
	 */
	public TADDateTime getDstEnd() {
		return _dstEnd;
	}

	/**
	 * Time changes (daylight savings time). Only present if requested and
	 * information is available.
	 */
	public List<TimeChange> getTimeChanges() {
		return _timeChanges;
	}

	private DST() {
		_timeChanges = new ArrayList<TimeChange>();
	}

	public static DST fromNode(Node node) {
		DST dst = new DST();
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
			case "region":
				dst._region = com.timeanddate.services.dataTypes.places.Region
						.fromNode(child);
				break;
			case "stdtimezone":
				dst._standardTimezone = TADTimezone.fromNode(child);
				break;
			case "dsttimezone":
				dst._dstTimezone = TADTimezone.fromNode(child);
				break;
			case "dstend":
				dst._dstEnd = new TADDateTime(child.getTextContent());
				break;
			case "dststart":
				dst._dstStart = new TADDateTime(child.getTextContent());
				break;
			case "timechanges":
				for (Node change : XmlUtils.asList(child.getChildNodes())) {
					dst._timeChanges.add(TimeChange.fromNode(change));
				}
				break;
			case "special":
				NamedNodeMap attr = child.getAttributes();
				Node typeAttr = attr.getNamedItem("type");
				String type = typeAttr.getTextContent();
				if (type.equals("nodst"))
					dst._special = DSTSpecialType.NoDaylightSavingTime;
				else if (type.equals("allyear"))
					dst._special = DSTSpecialType.DaylightSavingTimeAllYear;
				break;
			}
		}

		return dst;
	}
}
