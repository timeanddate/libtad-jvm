package com.timeanddate.services.dataTypes.businessdays;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.holidays.Holiday;
import com.timeanddate.services.dataTypes.time.TADTime;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class BusinessHoliday {
	private IncludeExclude _includeExclude;
	private int _count;
	private List<Holiday> _holidays;

	/**
	 * Either included or excluded, specifying whether or not the holidays
	 * in the result array were included or excluded when queried.
	 */
	public IncludeExclude getIncludeOrExclude() {
		return _includeExclude;
	}

	/**
	 * The number of holidays in the results.
	 */
	public int getCount() {
		return _count;
	}

	/**
	 * Holidays which occur in the requested period.
	 */
	public List<Holiday> getHolidays() {
		return _holidays;
	}

	private BusinessHoliday() {
		_holidays = new ArrayList<Holiday>();
	}

	public static BusinessHoliday fromNode(Node node) {
		BusinessHoliday businessHoliday = new BusinessHoliday();
		NodeList children = node.getChildNodes();
		NamedNodeMap attr = node.getAttributes();

		switch (attr.getNamedItem("type").getTextContent()) {
			case "included":
				businessHoliday._includeExclude = IncludeExclude.INCLUDED;
				break;
			case "excluded":
				businessHoliday._includeExclude = IncludeExclude.EXCLUDED;
				break;
		}

		businessHoliday._count = Integer.parseInt(attr.getNamedItem("count").getTextContent());

		for (Node list : XmlUtils.asList(children)) {
			for (Node holiday : XmlUtils.asList(list.getChildNodes())) {
				businessHoliday._holidays.add(Holiday.fromNode(holiday));
			}
		}

		return businessHoliday;
	}
}
