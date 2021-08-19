package com.timeanddate.services.dataTypes.astro;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.MalformedXMLException;
import com.timeanddate.services.common.XmlUtils;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class AstronomyObjectDetails {
	private AstronomyObjectType _name;
	private List<AstronomyDay> _days;

	/**
	 * Lists all the requested days where events are happening.
	 */
	public List<AstronomyDay> getDays() {
		return _days;
	}
	
	/**
	 * Object name.
	 */
	public AstronomyObjectType getName() {
		return _name;
	}

	private AstronomyObjectDetails() {
		_days = new ArrayList<AstronomyDay>();
	}

	public static AstronomyObjectDetails fromNode(Node node) throws MalformedXMLException {
		AstronomyObjectDetails obj = new AstronomyObjectDetails();
		NamedNodeMap attr = node.getAttributes();
		Node name = attr.getNamedItem("name");
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children))
			if (child.getNodeName() == "day")
				obj._days.add(AstronomyDay.fromNode(child));

		if (name != null) {
			String nametxt = name.getTextContent();

			if (!nametxt.equalsIgnoreCase("sun") && !nametxt.equalsIgnoreCase("moon")) {
				throw new MalformedXMLException(
						"The XML returned from Time and Date contained an unsupported name: "
								+ nametxt);
			} else {
				obj._name = name.getTextContent().toLowerCase() == "sun" ? AstronomyObjectType.Sun
						: AstronomyObjectType.Moon;
			}
		}
		return obj;
	}
}
