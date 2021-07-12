package com.timeanddate.services.dataTypes.onthisday;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class Name {
	private String _first;

	private String _middle;

	private String _last;

	/**
	 * First name.
	 */
	public String getFirst() {
		return _first;
	}

	/**
	 * Middle name.
	 */
	public String getMiddle() {
		return _middle;
	}

	/**
	 * Last name.
	 */
	public String getLast() {
		return _last;
	}

	public static Name fromNode(Node node) {
		Name name = new Name();
		NamedNodeMap attr = node.getAttributes();
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
				case "first":
					name._first = child.getTextContent();
					break;
				case "middle":
					name._middle = child.getTextContent();
					break;
				case "last":
					name._last = child.getTextContent();
					break;
			}
		}

		return name;
	}
}
