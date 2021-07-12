package com.timeanddate.services.dataTypes.onthisday;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.time.TADTime;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class Person {
	private int _id;
	private Name _name;
	private TADTime _birthdate;
	private TADTime _deathdate;
	private List<String> _categories;
	private List<String> _nationalities;

	/**
	 * Identifier for the person.
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Full name.
	 */
	public Name getName() {
		return _name;
	}

	/**
	 * Date of birth.
	 */
	public TADTime getBirthDate() {
		return _birthdate;
	}

	/**
	 * Date of death, if applicable.
	 */
	public TADTime getDeathDate() {
		return _deathdate;
	}

	/**
	 * Person categories.
	 */
	public List<String> getCategories() {
		return _categories;
	}

	/**
	 * The nationalities of the person.
	 */
	public List<String> getNationalities() {
		return _nationalities;
	}

	private Person() {
		_categories = new ArrayList<String>();
		_nationalities = new ArrayList<String>();
	}

	public static Person fromNode(Node node) {
		Person person = new Person();
		NamedNodeMap attr = node.getAttributes();
		Node id = attr.getNamedItem("id");
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
				case "name":
					person._name = Name.fromNode(child);
					break;
				case "birthdate":
					person._birthdate = TADTime.fromNode(child);
					break;
				case "deathdate":
					person._deathdate = TADTime.fromNode(child);
					break;
				case "categories":
					for (Node category : XmlUtils.asList(child.getChildNodes())) {
						person._categories.add(category.getTextContent());
					}
					break;
				case "nationalities":
					for (Node nationality : XmlUtils.asList(child.getChildNodes())) {
						person._nationalities.add(nationality.getTextContent());
					}
					break;
			}
		}

		person._id = Integer.parseInt(id.getTextContent());

		return person;
	}
}
