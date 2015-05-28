package com.timeanddate.services.dataTypes.places;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;

public class Geo {
	private String _name;
	private String _state;
	private Country _country;
	private Coordinates _coordinates;
	
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
		return _state == null ? "" : _state;
	}

	/**
	 * Country of the location.
	 */
	public Country getCountry() {
		return _country;
	}

	/**
	 * Geographical coordinates of the location.
	 */
	public Coordinates getCoordinates() {
		return _coordinates;
	}

	public static Geo fromNode(Node node) {
		Geo geo = new Geo();
		NodeList children = node.getChildNodes();
		double latitude = 0;
		double longitude = 0;
		boolean hasLat = false;
		boolean hasLong = false;

		for (Node n : XmlUtils.asList(children)) {
			switch (n.getNodeName()) {
			case "latitude":
				hasLat = true;
				latitude = Double.parseDouble(n.getTextContent());
				break;
			case "longitude":
				hasLong = true;
				longitude = Double.parseDouble(n.getTextContent());
				break;
			case "name":
				geo._name = n.getTextContent();
				break;
			case "state":
				geo._state = n.getTextContent();
				break;
			case "country":
				geo._country = com.timeanddate.services.dataTypes.places.Country
						.fromNode(n);
				break;
			default:
				break;
			}
		}

		if (hasLat && hasLong)
			geo._coordinates = new Coordinates(latitude, longitude);

		return geo;
	}
}
