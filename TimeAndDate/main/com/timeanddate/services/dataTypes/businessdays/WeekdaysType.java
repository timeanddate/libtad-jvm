package com.timeanddate.services.dataTypes.businessdays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import com.timeanddate.services.common.XmlUtils;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class WeekdaysType {
	private IncludeExclude _filterType;
	private int _totalCount;
	private int _mondayCount;
	private int _tuesdayCount;
	private int _wednesdayCount;
	private int _thursdayCount;
	private int _fridayCount;
	private int _saturdayCount;
	private int _sundayCount;

	/**
	 * Specifies whether or not the weekdays counted were part of an included or excluded filter.
	 */
	public IncludeExclude getFilterType() {
		return _filterType;
	}

	/**
	 * How many days in total have been counted.
	 */
	public int getTotalCount() {
		return _totalCount;
	}

	/**
	 * Count for Mondays.
	 */
	public int getMondayCount() {
		return _mondayCount;
	}

	/**
	 * Count for Tuesdays.
	 */
	public int getTuesdayCount() {
		return _tuesdayCount;
	}

	/**
	 * Count for Wednesdays.
	 */
	public int getWednesdayCount() {
		return _wednesdayCount;
	}

	/**
	 * Count for Thursdays.
	 */
	public int getThursdayCount() {
		return _thursdayCount;
	}

	/**
	 * Count for Fridays.
	 */
	public int getFridayCount() {
		return _fridayCount;
	}

	/**
	 * Count for Saturdays.
	 */
	public int getSaturdayCount() {
		return _saturdayCount;
	}

	/**
	 * Count for Sundays.
	 */
	public int getSundayCount() {
		return _sundayCount;
	}

	public static WeekdaysType fromNode(Node node) {
		WeekdaysType weekdays = new WeekdaysType();
		NodeList children = node.getChildNodes();
		NamedNodeMap attr = node.getAttributes();

		switch (attr.getNamedItem("type").getTextContent()) {
			case "included":
				weekdays._filterType = IncludeExclude.INCLUDED;
				break;
			case "excluded":
				weekdays._filterType = IncludeExclude.EXCLUDED;
				break;
		}

		weekdays._totalCount = Integer.parseInt(attr.getNamedItem("count").getTextContent());

		for (Node n : XmlUtils.asList(children)) {
			switch (n.getNodeName()) {
				case "mon":
					weekdays._mondayCount = Integer.parseInt(n.getTextContent());
					break;
				case "tue":
					weekdays._tuesdayCount = Integer.parseInt(n.getTextContent());
					break;
				case "wed":
					weekdays._wednesdayCount = Integer.parseInt(n.getTextContent());
					break;
				case "thu":
					weekdays._thursdayCount = Integer.parseInt(n.getTextContent());
					break;
				case "fri":
					weekdays._fridayCount = Integer.parseInt(n.getTextContent());
					break;
				case "sat":
					weekdays._saturdayCount = Integer.parseInt(n.getTextContent());
					break;
				case "sun":
					weekdays._sundayCount = Integer.parseInt(n.getTextContent());
					break;
			}
		}

		return weekdays;
	}
}
