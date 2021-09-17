package com.timeanddate.services.dataTypes.tides;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class StationInfo {
	private String _name;
	private float _latitude;
	private float _longitude;
	private String _type;
	private float _distance;

	/**
	 * Station name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Latitude coordinate of the station.
	 */
	public float getLatitude() {
		return _latitude;
	}

	/**
	 * Longitude coordinate of the station.
	 */
	public float getLongitude() {
		return _longitude;
	}

	/**
	 * Station type. Either reference or subordinate station.
	 */
	public String getType() {
		return _type;
	}

	/** 
	 * Distance between request place and this station.
	 */
	public float getDistance() {
		return _distance;
	}

	public static StationInfo fromNode(Node node) {
		StationInfo stationInfo = new StationInfo();
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
			case "name":
				stationInfo._name = child.getTextContent();
				break;
			case "latitude":
				stationInfo._latitude = Float.parseFloat(child.getTextContent());
				break;
			case "longitude":
				stationInfo._longitude = Float.parseFloat(child.getTextContent());
				break;
			case "type":
				stationInfo._type = child.getTextContent();
				break;
			case "distance":
				stationInfo._distance = Float.parseFloat(child.getTextContent());
				break;
			}
		}

		return stationInfo;
	}
}
