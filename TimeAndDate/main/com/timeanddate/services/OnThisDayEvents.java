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
import com.timeanddate.services.dataTypes.onthisday.Event;
import com.timeanddate.services.dataTypes.onthisday.Person;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class OnThisDayEvents {
	public List<Event> Events;

	public List<Person> Births;

	public List<Person> Deaths;

	public OnThisDayEvents() {
		Events = new ArrayList<Event>();
		Births = new ArrayList<Person>();
		Deaths = new ArrayList<Person>();
	}

	static OnThisDayEvents fromXml(String result) {
		OnThisDayEvents otd_events = new OnThisDayEvents();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			NodeList children = root.getChildNodes();

			NodeList events = root.getElementsByTagName("events");
			NodeList births = root.getElementsByTagName("births");
			NodeList deaths = root.getElementsByTagName("deaths");

			if (events != null)
				for (Node event : XmlUtils.asList(events))
					for (Node event_node : XmlUtils.asList(event.getChildNodes()))
						otd_events.Events.add(Event.fromNode(event_node));

			if (births != null)
				for (Node birth : XmlUtils.asList(births))
					for (Node birth_node : XmlUtils.asList(birth.getChildNodes()))
						otd_events.Births.add(Person.fromNode(birth_node));

			if (deaths != null)
				for (Node death : XmlUtils.asList(deaths))
					for (Node death_node : XmlUtils.asList(death.getChildNodes()))
						otd_events.Deaths.add(Person.fromNode(death_node));

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return otd_events;
	}
}
