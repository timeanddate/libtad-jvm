package com.timeanddate.services.dataTypes.astro;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.MalformedXMLException;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.places.Geo;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class AstronomyLocation {
	private String _id;
	private Geo _geography;
	private List<AstronomyObjectDetails> _objects;
	
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
	 * Requested astronomical information.
	 */
	public List<AstronomyObjectDetails> getObjects() {
		return _objects;
	}

	private AstronomyLocation() {
		_objects = new ArrayList<AstronomyObjectDetails>();
	}

	public static AstronomyLocation fromNode(Node location) {
		AstronomyLocation astro = new AstronomyLocation();
		NamedNodeMap attr = location.getAttributes();
		Node id = attr.getNamedItem("id");
		NodeList children = location.getChildNodes();

		for (Node n : XmlUtils.asList(children)) {
			switch (n.getNodeName()) {
			case "geo":
				astro._geography = Geo.fromNode(n);
				break;
			case "astronomy":
				for (Node child : XmlUtils.asList(n.getChildNodes()))
					try {
						astro._objects.add(AstronomyObjectDetails.fromNode(child));
					} catch (MalformedXMLException e) {
						e.printStackTrace();
					}
				break;

			}
		}

		astro._id = id.getTextContent();

		return astro;
	}
}
