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

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.IPredicate;

import com.timeanddate.services.dataTypes.places.Geo;
import com.timeanddate.services.dataTypes.time.TADTime;
import com.timeanddate.services.dataTypes.businessdays.*;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class BusinessDates {
	public Geo Geography;

	public List<Period> Periods;

	public BusinessDates() {
		Periods = new ArrayList<Period>();
	}

	static BusinessDates fromXml(String result) {
		BusinessDates dates = new BusinessDates();
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
					dates.Geography = Geo.fromNode(node);

			if (period != null)
				for (Node node : XmlUtils.asList(period))
					dates.Periods.add(Period.fromNode(node));

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dates;
	}
}
