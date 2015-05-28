package com.timeanddate.services.dataTypes.astro;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.timeanddate.services.common.MalformedXMLException;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class AstronomySpecial {
	private AstronomyEventType _type;
	
	/**
	 * Indicates if the sun is up or down all day.
	 */	
	public AstronomyEventType getType() {
		return _type;
	}

	public static AstronomySpecial fromNode(Node node) throws MalformedXMLException {
		AstronomySpecial special = new AstronomySpecial();
		NamedNodeMap attr = node.getAttributes();
		Node type = attr.getNamedItem("type");

		if (type != null && type.getTextContent() != "") {
			switch (type.getTextContent()) {
			case "rise":
				special._type = AstronomyEventType.Rise;
				break;
			case "set":
				special._type = AstronomyEventType.Set;
				break;
			default:
				throw new MalformedXMLException(
						"The XML Received from Time and Date did not include an event type which complies with an AstronomyEventType enum");
			}
		}

		return special;
	}
}
