package com.timeanddate.services.dataTypes.places;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class Country {
	public String _id;
	public String _name;
	
	/**
	 * The ISO 3166-1-alpha-2 country code
	 */
	public String getId() {
		return _id == null ? "" : _id;
	}

	/**
	 * Full name of the country.
	 */
	public String getName() {
		return _name == null ? "" : _name;
	}

	public static Country fromNode(Node node) {
		Country country = new Country();
		NamedNodeMap attr = node.getAttributes();
		Node id = attr.getNamedItem("id");

		if (id != null)
			country._id = id.getTextContent();

		country._name = node.getTextContent();

		return country;
	}
}
