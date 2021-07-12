
package com.timeanddate.services.dataTypes.onthisday;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.time.TADTime;
import com.timeanddate.services.dataTypes.places.Country;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class Event {
	private int _id;
	private String _name;
	private TADTime _date;
	private String _location;
	private List<String> _categories;
	private List<Country> _countries;
	private String _description;

	/**
	 * Identifier for the event.
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Event name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Date of the event.
	 */
	public TADTime getDate() {
		return _date;
	}

	/**
	 * Location of the event.
	 */
	public String getLocation() {
		return _location;
	}

	/**
	 * Event categories.
	 */
	public List<String> getCategories() {
		return _categories;
	}

	/**
	 * Related countries.
	 */
	public List<Country> getCountries() {
		return _countries;
	}

	/**
	 * Event description.
	 */
	public String getDescription() {
		return _description;
	}

	private Event() {
		_categories = new ArrayList<String>();
		_countries = new ArrayList<Country>();
	}

	public static Event fromNode(Node node) {
		Event event = new Event();

		NamedNodeMap attr = node.getAttributes();
		Node id = attr.getNamedItem("id");
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
				case "name":
					event._name = child.getTextContent();
					break;
				case "date":
					event._date = TADTime.fromNode(child);
					break;
				case "location":
					event._location = child.getTextContent();
					break;
				case "categories":
					for (Node category : XmlUtils.asList(child.getChildNodes())) {
						event._categories.add(category.getTextContent());
					}
					break;
				case "countries":
					for (Node country : XmlUtils.asList(child.getChildNodes())) {
						event._countries.add(Country.fromNode(country));
					}
					break;
				case "description":
					event._description = child.getTextContent();
					break;
			}
		}

		event._id = Integer.parseInt(id.getTextContent());

		return event;
	}
}
