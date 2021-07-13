package com.timeanddate.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.IPredicate;

import com.timeanddate.services.dataTypes.places.Geo;
import com.timeanddate.services.dataTypes.time.TADTime;
import com.timeanddate.services.dataTypes.businessdays.*;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class BusinessDuration {
	public Geo Geography;

	public Period Period;

	static BusinessDuration fromXml(String result) {
		BusinessDuration duration = new BusinessDuration();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();

			NodeList geo = root.getElementsByTagName("geo");
			NodeList period = root.getElementsByTagName("period");

			if (geo != null)
				for (Node node : XmlUtils.asList(geo))
					duration.Geography = Geo.fromNode(node);

			if (period != null)
				for (Node node : XmlUtils.asList(period))
					duration.Period = com.timeanddate.services.dataTypes.businessdays.Period.fromNode(node);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return duration;
	}
}
