package com.timeanddate.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.timeanddate.services.common.MalformedXMLException;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.places.Location;
import com.timeanddate.services.dataTypes.dialCode.Composition;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class DialCodes {
	public String Number;

	public List<Composition> Compositions;

	public List<Location> Locations;

	public DialCodes() {
		Compositions = new ArrayList<Composition>();
		Locations = new ArrayList<Location>();
	}

	static DialCodes fromXml(String result) {
		DialCodes codes = new DialCodes();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			NodeList children = root.getChildNodes();
			NodeList locations = root.getElementsByTagName("location");

			for (Node child : XmlUtils.asList(children)) {
				switch (child.getNodeName()) {
				case "composition":
					for (Node n : XmlUtils.asList(child.getChildNodes()))
						codes.Compositions.add(Composition.fromNode(n));
					break;
				case "number":
					codes.Number = child.getFirstChild().getTextContent();
					break;
				}
			}

			if (locations != null)
				for (Node location : XmlUtils.asList(locations))
					codes.Locations.add(Location.fromNode(location));
		} catch (ParserConfigurationException | SAXException | IOException | MalformedXMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return codes;
	}
}
