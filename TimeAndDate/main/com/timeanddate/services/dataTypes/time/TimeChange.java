package com.timeanddate.services.dataTypes.time;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class TimeChange {
	private int _newDaylightSavingTime = -1;
	private int _newTimezoneOffset = -1;
	private int _newTotalOffset;
	private TADDateTime _utcTime;
	private TADDateTime _oldLocalTime;
	private TADDateTime _newLocalTime;
	
	/**
	 * New DST offset in seconds. Value will be null if there is no DST for this
	 * location.
	 */
	public int getNewDaylightSavingTime() {
		return _newDaylightSavingTime;
	}

	/**
	 * New timezone offset to UTC in seconds if there is a timezone change for
	 * this place. Otherwise the value will be null. Time zones changes happen
	 * only very rarely, so the field will be null on most occasions.
	 */
	public int getNewTimezoneOffset() {
		return _newTimezoneOffset;
	}

	/**
	 * New total offset to UTC in seconds.
	 */
	public int getNewTotalOffset() {
		return _newTotalOffset;
	}

	/**
	 * The UTC time of the transition
	 */
	public TADDateTime getUtcTime() {
		return _utcTime;
	}

	/**
	 * The old local time before the transition.
	 */
	public TADDateTime getOldLocalTime() {
		return _oldLocalTime;
	}

	/**
	 * The new local time after the transition.
	 */
	public TADDateTime getNewLocalTime() {
		return _newLocalTime;
	}

	public static TimeChange fromNode(Node node) {
		TimeChange change = new TimeChange();
		NamedNodeMap attr = node.getAttributes();
		Node newdst = attr.getNamedItem("newdst");
		Node newzone = attr.getNamedItem("newzone");
		Node newoffset = attr.getNamedItem("newoffset");
		Node utctime = attr.getNamedItem("utctime");
		Node oldlocal = attr.getNamedItem("oldlocaltime");
		Node newlocal = attr.getNamedItem("newlocaltime");

		if (newdst != null  && newdst.getTextContent() != "") {
			change._newDaylightSavingTime = Integer.parseInt(newdst
					.getTextContent());
		}

		if (newzone != null && newzone.getTextContent() != "") {
			change._newTimezoneOffset = Integer.parseInt(newzone
					.getTextContent());
		}

		if (newoffset != null  && newoffset.getTextContent() != "") {
			change._newTotalOffset = Integer
					.parseInt(newoffset.getTextContent());
		}

		if (utctime != null) {
			change._utcTime = new TADDateTime(utctime.getTextContent());
		}

		if (newlocal != null) {
			change._newLocalTime = new TADDateTime(newlocal.getTextContent());
		}

		if (oldlocal != null) {
			change._oldLocalTime = new TADDateTime(oldlocal.getTextContent());
		}

		return change;
	}
}
