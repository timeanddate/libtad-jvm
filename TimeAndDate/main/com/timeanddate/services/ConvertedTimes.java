package com.timeanddate.services;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.places.Location;
import com.timeanddate.services.dataTypes.time.TADTime;

public class ConvertedTimes {
	// / <summary>
	// / UTC time stamp of requested time.
	// / </summary>
	// / <value>
	// / The UTC.
	// / </value>
	public TADTime Utc;

	// / <summary>
	// / This element contains the time information for the locations mentioned
	// in the request.
	// / </summary>
	// / <value>
	// / The locations.
	// / </value>
	public List<Location> Locations;

	public ConvertedTimes() {
		Locations = new ArrayList<Location>();
	}

	public static ConvertedTimes fromXml(String result) throws Exception {
		ConvertedTimes times = new ConvertedTimes();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream stream = new ByteArrayInputStream(
				result.getBytes(StandardCharsets.UTF_8));
		Document document = builder.parse(stream);
		Element root = document.getDocumentElement();
		NodeList children = root.getChildNodes();
		NodeList locations = root.getElementsByTagName("location");

		// TODO: Replace with XPATH
		for (Node n : XmlUtils.asList(children)) {
			if (n.getNodeName() == "utc") {
				Node time = n.getFirstChild();
				times.Utc = TADTime.fromNode(time);
			}
		}

		if (locations != null)
			for (Node location : XmlUtils.asList(locations))
				times.Locations.add(Location.fromNode(location));

		return times;
	}
}
