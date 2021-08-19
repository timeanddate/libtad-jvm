package com.timeanddate.services.dataTypes.businessdays;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.time.TADTime;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class Period {
	private int _includedDays;
	private int _calendarDays;
	private int _skippedDays;
	private TADTime _startDate;
	private TADTime _endDate;
	private WeekdaysType _weekdays;
	private BusinessHoliday _holidays;

	/**
	 * Number of days calculated.
	 */
	public int getIncludedDays() {
		return _includedDays;
	}

	/**
	 * Number of calendar days in calculated period.
	 */
	public int getCalendarDays() {
		return _calendarDays;
	}

	/**
	 * Number of days which was skipped in the calculated period.
	 */
	public int getSkippedDays() {
		return _skippedDays;
	}

	/**
	 * The date the calculation started from.
	 */
	public TADTime getStartDate() {
		return _startDate;
	}

	/**
	 * The date the calculation ended on.
	 */
	public TADTime getEndDate() {
		return _endDate;
	}

	/**
	 * The spread of excluded or included weekdays in includeddays.
	 */
	public WeekdaysType getWeekdays() {
		return _weekdays;
	}

	/**
	 * Holidays which occur in the requested period.
	 */
	public BusinessHoliday getHolidays() {
		return _holidays;
	}

	public static Period fromNode(Node node) {
		Period period = new Period();
		NodeList children = node.getChildNodes();
		NamedNodeMap attr = node.getAttributes();

		period._includedDays = Integer.parseInt(attr.getNamedItem("includeddays").getTextContent());
		period._calendarDays = Integer.parseInt(attr.getNamedItem("calendardays").getTextContent());
		period._skippedDays = Integer.parseInt(attr.getNamedItem("skippeddays").getTextContent());

		for (Node n : XmlUtils.asList(children)) {
			switch (n.getNodeName()) {
				case "startdate":
					period._startDate = TADTime.fromNode(n);
					break;
				case "enddate":
					period._endDate = TADTime.fromNode(n);
					break;
				case "weekdays":
					period._weekdays = WeekdaysType.fromNode(n);
					break;
				case "holidays":
					if (n.getChildNodes().getLength() > 0) {
						period._holidays = BusinessHoliday.fromNode(n);
					}
					break;
				default:
					break;
			}
		}

		return period;
	}
}
