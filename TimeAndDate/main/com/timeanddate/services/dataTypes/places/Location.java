package com.timeanddate.services.dataTypes.places;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.DateTimeUtils;
import com.timeanddate.services.common.LocalTimeDoesNotExistException;
import com.timeanddate.services.common.MissingTimeChangesException;
import com.timeanddate.services.common.QueriedDateOutOfRangeException;
import com.timeanddate.services.common.TimeSpan;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.astro.Astronomy;
import com.timeanddate.services.dataTypes.time.TADDateTime;
import com.timeanddate.services.dataTypes.time.TADTime;
import com.timeanddate.services.dataTypes.time.TimeChange;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class Location {
	private String _id;
	private Geo _geography;
	private TADTime _time;
	private List<TimeChange> _timeChanges;
	private List<Astronomy> _astronomy;
	
	/**
	 * The id of the location.
	 */
	public String getId() {
		return _id == null ? "" : _id;
	}

	/**
	 * Geographical information about the location.
	 */
	public Geo getGeography() {
		return _geography;
	}

	/**
	 * Time information about the location. Only present if requested.
	 */
	public TADTime getTime() {
		return _time;
	}

	/**
	 * Time changes (daylight savings time). Only present if requested and
	 * information is available.
	 */
	public List<TimeChange> getTimeChanges() {
		return _timeChanges;
	}

	/**
	 * Astronomical information – sunrise and sunset times. Only for the
	 * timeservice and if requested.
	 */
	public List<Astronomy> getAstronomy() {
		return _astronomy;
	}

	public Location() {
		_timeChanges = new ArrayList<TimeChange>();
		_astronomy = new ArrayList<Astronomy>();
	}

	public static Location fromNode(Node node) {
		Location location = new Location();
		NamedNodeMap attr = node.getAttributes();
		Node id = attr.getNamedItem("id");
		NodeList children = node.getChildNodes();

		if (id != null)
			location._id = id.getTextContent();

		for (Node n : XmlUtils.asList(children)) {
			switch (n.getNodeName()) {
			case "geo":
				location._geography = Geo.fromNode(n);
				break;
			case "time":
				location._time = TADTime.fromNode(n);
				break;
			case "timechanges":
				location._timeChanges = handleTimeChanges(n.getChildNodes());
				break;
			case "astronomy":
				location._astronomy = handleAstronomy(n.getChildNodes());
				break;
			default:
				break;
			}
		}

		return location;
	}

	private static List<TimeChange> handleTimeChanges(NodeList node) {
		List<TimeChange> list = new ArrayList<TimeChange>();

		for (Node n : XmlUtils.asList(node)) {
			list.add(TimeChange.fromNode(n));
		}

		return list;
	}

	private static List<Astronomy> handleAstronomy(NodeList node) {
		List<Astronomy> list = new ArrayList<Astronomy>();

		for (Node n : XmlUtils.asList(node)) {
			list.add(com.timeanddate.services.dataTypes.astro.Astronomy
					.fromNode(n));
		}

		return list;
	}

	public TimeSpan getUTCOffsetFromLocalTime(TADDateTime localTime) throws MissingTimeChangesException, QueriedDateOutOfRangeException, LocalTimeDoesNotExistException {
		if (_timeChanges == null
				|| (_timeChanges != null && _timeChanges.isEmpty()))
			throw new MissingTimeChangesException(
					"IncludeTimeChanges either set to false or no time changes for this location");

		int firstNewLocalTime = _timeChanges.get(0).getNewLocalTime().getYear();
		if (localTime.getYear() > firstNewLocalTime
				|| localTime.getYear() < firstNewLocalTime)
			throw new QueriedDateOutOfRangeException(
					"The year specified in localTime is outside the year available for this location");

		TimeChange change;
		if (_timeChanges.size() == 1)
			change = _timeChanges.get(0);
		else
			change = getTimeChangeForLocalTime(_timeChanges, localTime);

		TimeSpan span;
		if (DateTimeUtils.ToMinuteCompare(localTime, change.getOldLocalTime()) < 0) {
			long oldTicks = change.getOldLocalTime().getTimeInTicks();
			long utcTicks = change.getUtcTime().getTimeInTicks();
			span = TimeSpan.FromTicks(oldTicks - utcTicks);
		} else {
			span = TimeSpan.FromSeconds(change.getNewTotalOffset());
		}

		return span;
	}

	private static TimeChange getTimeChangeForLocalTime(
			List<TimeChange> changes, TADDateTime localTime) throws LocalTimeDoesNotExistException  {
		TimeChange timeChange = null;
		int numberOfChanges = changes.size() - 1;
		for (int i = 0; i <= numberOfChanges; i++) {
			TimeChange change = changes.get(i);

			// If the old local time was 02:00 and the new local time is 03:00
			// and the user ask for the timezone on 02:30 they will get this
			// exception
			if (DateTimeUtils.ToMinuteCompare(localTime, change.getOldLocalTime()) == 1
					&& DateTimeUtils.ToMinuteCompare(localTime,
							change.getNewLocalTime()) == -1)
				throw new LocalTimeDoesNotExistException(
						"The time and date requested falls between the old and new timezone");

			// If the date the user ask for is older (earlier) than the first
			// time change
			int localTimeIsEarlierThanOldLocalTime = DateTimeUtils
					.ToMinuteCompare(localTime, change.getOldLocalTime());
			if (localTimeIsEarlierThanOldLocalTime <= 0) {
				timeChange = change;
				break;
			}

			// If the date the user ask for is newer or the same date as the new
			// local time
			int localTimeIsLaterThanNewLocalTime = DateTimeUtils
					.ToMinuteCompare(localTime, change.getNewLocalTime());
			if (localTimeIsLaterThanNewLocalTime >= 0) {
				// If this is the last time change then this is the timechange
				// the user is in
				if (i == numberOfChanges) {
					timeChange = change;
				} else {
					// If there is more time changes, and the date the user asks
					// for is later than
					// the next date. Just continue to the next date. If not,
					// 'change' is the
					// timechange the localTime is in
					TimeChange next = changes.get(i + 1);
					if (DateTimeUtils.ToMinuteCompare(localTime,
							next.getNewLocalTime()) >= 0)
						continue; // Could be made recursive
					else {
						timeChange = change;
						break;
					}
				}
			}
		}

		return timeChange;
	}
}
