package com.timeanddate.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.timeanddate.services.common.IPredicate;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.UriUtils;
import com.timeanddate.services.common.WebClient;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.holidays.Holiday;
import com.timeanddate.services.dataTypes.holidays.HolidayType;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class HolidaysService extends BaseService {
	/**
	 * Holiday types which should be returned. To combine multiple classes, use
	 * EnumSet.of()
	 * <p>
	 * Example: <b>service.IncludedHolidayTypes = HolidayType.Local</b>
	 */
	private EnumSet<HolidayType> _types;

	/**
	 * The holidays service can be used to retrieve the list of holidays for a
	 * country.
	 * 
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public HolidaysService(String accessKey, String secretKey)
			throws SignatureException, UnsupportedEncodingException {
		super(accessKey, secretKey, "holidays");
	}

	/**
	 * The holidays service can be used to retrieve the list of holidays for a
	 * country.
	 * 
	 * @param countryCode
	 *            Specify the ISO3166-1-alpha-2 Country Code for which you would
	 *            like to retrieve the list of holidays.
	 * @param year
	 *            The year for which the holidays should be retrieved.
	 * @return List of holidays for a given country
	 * @throws DOMException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ServerSideException
	 * @throws URISyntaxException
	 */
	public List<Holiday> holidaysForCountry(String countryCode, int year)
			throws DOMException, ParserConfigurationException, SAXException,
			IOException, ServerSideException, URISyntaxException {
		if (countryCode != null && !countryCode.isEmpty() && year <= 0)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		return retrieveHolidays(countryCode, year);
	}

	/**
	 * The holidays service can be used to retrieve the list of holidays for a
	 * country. This overload uses the current year by default.
	 * 
	 * @param country
	 *            Specify the ISO3166-1-alpha-2 Country Code for which you would
	 *            like to retrieve the list of holidays.
	 * @return
	 * @throws DOMException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ServerSideException
	 * @throws URISyntaxException
	 */
	public List<Holiday> holidaysForCountry(String country)
			throws DOMException, ParserConfigurationException, SAXException,
			IOException, ServerSideException, URISyntaxException {
		if (country == null || (country != null && !country.isEmpty()))
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		return retrieveHolidays(country,
				Calendar.getInstance().get(Calendar.YEAR));
	}

	private List<Holiday> retrieveHolidays(String country, int year)
			throws DOMException, ParserConfigurationException, SAXException,
			IOException, ServerSideException, URISyntaxException {
		Map<String, String> arguments = getArguments(country, year);
		String query = UriUtils.BuildUriString(arguments);

		URL uri = new URL(Constants.EntryPoint + ServiceName + query);
		WebClient client = new WebClient();
		String result = client.downloadString(uri);
		XmlUtils.checkForErrors(result);
		return fromXml(result);
	}
	
	public void setHolidayTypes(EnumSet<HolidayType> types) {
		_types = types;
	}
	
	public void addHolidayType(HolidayType type) {
		_types.add(type);
	}
	
	public EnumSet<HolidayType> getHolidayTypes() {
		return _types;
	}

	private Map<String, String> getArguments(String country, int year) {
		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		String types = getHolidayTypesAsStr();
		args.put("country", country);
		args.put("lang", Language);
		args.put("version", Integer.toString(Version));
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));
		args.put("out", Constants.DefaultReturnFormat);

		if (types != null && !types.isEmpty())
			args.put("types", types);

		if (year > 0)
			args.put("year", Integer.toString(year));

		return args;
	}

	private static List<Holiday> fromXml(String result)
			throws ParserConfigurationException, SAXException, IOException,
			DOMException, URISyntaxException {
		ArrayList<Holiday> list = new ArrayList<Holiday>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream stream = new ByteArrayInputStream(
				result.getBytes(StandardCharsets.UTF_8));
		Document document = builder.parse(stream);
		Element root = document.getDocumentElement();

		NodeList nodes = root.getElementsByTagName("holiday");

		for (Node node : XmlUtils.asList(nodes)) {
			list.add(Holiday.fromNode(node));
		}

		return list;
	}

	private String getHolidayTypesAsStr() {
		if (_types == null)
			return "";

		ArrayList<String> includedStrings = new ArrayList<String>();
		for (final HolidayType type : HolidayType.values()) {
			if (_types.contains(type))
				includedStrings
						.add(StringUtils.resolveHolidays(where.of(type)).Command);
		}

		String included = StringUtils.join(includedStrings, ",");
		return included;
	}

	private IPredicate<HolidayType> where = new IPredicate<HolidayType>() {
		HolidayType type;

		public boolean is(HolidayType t) {
			return t == type;
		}

		public IPredicate<HolidayType> of(HolidayType t) {
			type = t;
			return this;
		}
	};
}
