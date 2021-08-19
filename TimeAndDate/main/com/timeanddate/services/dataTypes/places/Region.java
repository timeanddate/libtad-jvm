package com.timeanddate.services.dataTypes.places;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class Region {
	private Country _country;
	private String _description;
	private String _biggestPlace;
	private List<LocationRef> _locations;
	
	/**
	 * The country.
	 */
	public Country getCountry() {
		return _country;
	}

	/**
	 * Textual description of a region.
	 *
	 * Example: All locations
	 *
	 * Example: most of Newfoundland and Labrador
	 *
	 * Example: some regions of Nunavut Territory; small region of Ontario
	 */
	public String getDescription() {
		return _description == null ? "" : _description;
	}

	/**
	 * Name of the biggest city within the region
	 */
	public String getBiggestPlace() {
		return _biggestPlace == null ? "" : _biggestPlace;
	}

	/**
	 * A list of all locations referenced by this region. Only returned if
	 * requested by specifying the parameter IncludePlacesForEveryCountry on
	 * DaylightSavingTimeService
	 */
	public List<LocationRef> getLocations() {
		return _locations;
	}

	private Region() {
		_locations = new ArrayList<LocationRef>();
	}

	public static Region fromNode(Node node) {
		Region region = new Region();
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
			case "country":
				region._country = com.timeanddate.services.dataTypes.places.Country
						.fromNode(child);
				break;
			case "desc":
				region._description = child.getTextContent();
				break;
			case "biggestplace":
				region._biggestPlace = child.getTextContent();
				break;
			case "locations":
				NodeList locations = child.getChildNodes();
				for (Node location : XmlUtils.asList(locations)) {
					region._locations.add(LocationRef.fromNode(location));
				}
				break;
			}
		}

		return region;
	}
}
