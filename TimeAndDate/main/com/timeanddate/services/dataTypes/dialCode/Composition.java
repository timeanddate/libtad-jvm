package com.timeanddate.services.dataTypes.dialCode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.timeanddate.services.common.MalformedXMLException;

public class Composition {
	private PhoneNumberElementType _phoneNumberElement;
	private String _number;
	private String _description;
	
	/**
	 * Type of the phone number element.
	 */
	public PhoneNumberElementType getPhoneNumberElement() {
		return _phoneNumberElement;
	}

	/**
	 * The actual number part. May contain characters as variable if no number
	 * was supplied to the service (for the local-number part).
	 */
	public String getNumber() {
		return _number == null ? "" : _number;
	}

	/**
	 * Textual description of the composition part.
	 */
	public String getDescription() {
		return _description == null ? "" : _description;
	}

	private static PhoneNumberElementType getTypeByNode(Node node)
			throws Exception {
		String str = node.getTextContent();
		switch (str) {
		case "international-prefix":
			return PhoneNumberElementType.InternationalPrefix;
		case "country-prefix":
			return PhoneNumberElementType.CountryPrefix;
		case "national-prefix":
			return PhoneNumberElementType.NationalPrefix;
		case "unknown-national-prefix":
			return PhoneNumberElementType.UnknownNationalPrefix;
		case "national-code":
			return PhoneNumberElementType.NationalCode;
		case "area-code":
			return PhoneNumberElementType.AreaCode;
		case "local-number":
			return PhoneNumberElementType.LocalNumber;
		default:
			throw new MalformedXMLException(
					"The XML Received from Time and Date did not include an object name which complies with an AstronomyObjectType enum: "
							+ str);
		}
	}

	public static Composition fromNode(Node node) throws Exception {
		Composition composition = new Composition();
		NamedNodeMap attr = node.getAttributes();
		Node id = attr.getNamedItem("id");
		Node number = attr.getNamedItem("number");
		Node desc = attr.getNamedItem("description");

		if (id != null)
			composition._phoneNumberElement = getTypeByNode(id);

		if (number != null)
			composition._number = number.getTextContent();

		if (desc != null)
			composition._description = desc.getTextContent();

		return composition;
	}

}
