package com.timeanddate.services.dataTypes.holidays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;

public class HolidayState {
	private int _id;
	private String _abbrevation;
	private String _name;
	private String _exception;
	
	/**
	 * Unique id of the state/subdivision.
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Abbreviation of the state/subdivision.
	 */
	public String getAbbrevation() {
		return _abbrevation == null ? "" : _abbrevation;
	}

	/**
	 * Common name of the state/subdivision.
	 */
	public String getName() {
		return _name == null ? "" : _name;
	}

	/**
	 * Eventual exception if the holiday does not affect the whole
	 * state/subdivision.
	 */
	public String getException() {
		return _exception;
	}

	public static HolidayState fromNode(Node node) {
		HolidayState state = new HolidayState();
		NodeList children = node.getChildNodes();

		for (Node n : XmlUtils.asList(children)) {
			switch (n.getNodeName()) {
			case "id":
				state._id = Integer.parseInt(n.getTextContent());
				break;
			case "abbrev":
				state._abbrevation = n.getTextContent();
				break;
			case "name":
				state._name = n.getTextContent();
				break;
			case "exception":
				state._exception = n.getTextContent();
				break;
			}
		}

		return state;
	}
}
