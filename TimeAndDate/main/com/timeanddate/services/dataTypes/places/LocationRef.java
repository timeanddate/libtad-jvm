package com.timeanddate.services.dataTypes.places;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class LocationRef {
	private String _id;
	private String _name;
	private String _state;
	
	/**
	 * The id of the location.
	 */
	public String getId() {
		return _id == null ? "" : _id;
	}

	/**
	 * The name of the location
	 */
	public String getName() {
		return _name == null ? "" : _name;
	}

	/**
	 * The state of the location within the country (only if applicable).
	 */
	public String getState() {
		return _state;
	}

	public static LocationRef fromNode(Node node) {
		LocationRef ref = new LocationRef();
		NamedNodeMap attr = node.getAttributes();
		Node id = attr.getNamedItem("id");
		Node name = attr.getNamedItem("name");
		Node state = attr.getNamedItem("state");

		if (id != null)
			ref._id = id.getTextContent();

		if (name != null)
			ref._name = name.getTextContent();

		if (state != null)
			ref._state = state.getTextContent();

		return ref;
	}
}
