package com.timeanddate.services.dataTypes.places;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class Place {
	public int _id;
	public String _urlid;
	public Geo _geography;
	
	/**
	 * Numerical id of the referenced place.
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Textual id of the referenced place.
	 */
	public String getUrlid() {
		return _urlid == null ? "" : _urlid;
	}

	/**
	 * Geographical information about the location.
	 */
	public Geo getGeography() {
		return _geography;
	}

	public static Place fromNode(Node node) {
		Place model = new Place();
		NamedNodeMap attr = node.getAttributes();
		Node id = attr.getNamedItem("id");
		Node urlid = attr.getNamedItem("urlid");
		NodeList children = node.getChildNodes();

		for (Node n : XmlUtils.asList(children)) {
			if (n.getNodeName() == "geo")
				model._geography = Geo.fromNode(n);
		}

		if (id != null)
			model._id = Integer.parseInt(id.getTextContent());

		if (urlid != null)
			model._urlid = urlid.getTextContent();

		return model;
	}
}
