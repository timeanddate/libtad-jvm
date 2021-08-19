package com.timeanddate.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.UriUtils;
import com.timeanddate.services.common.WebClient;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.dst.DST;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class DSTService extends BaseService {
	/**
	 * Add a list of time changes during the year to the dstentry object. This
	 * listing e.g. shows changes caused by daylight savings time.
	 * <p>
	 * <b>true</b> if include time changes; otherwise, <b>false</b>.
	 * <b>false</b> is default.
	 */
	private boolean _includeTimeChanges;

	/**
	 * Return only countries which actually observe DST in the queried year.
	 * Other countries will be suppressed.
	 * <p>
	 * <b>true</b> if include only dst countries; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includeOnlyDstCountries;

	/**
	 * For every timezone/country, list the individual places that belong to
	 * each record.
	 * <p>
	 * <b>true</b> if include places for every country; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includePlacesForEveryCountry;

	/**
	 * The dstlist service can be used to obtain data about timezones in all
	 * supported countries, eventual start and end date of daylight savings
	 * time, and UTC offset for the timezones.
	 * <p>
	 * The result data is aggregated on country/timezone level. By default, only
	 * information from countries that actually observe DST is returned without
	 * listing the individually affected locations – see the parameters
	 * listplaces and onlydst to change this behavior.
	 * 
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException 
	 * 			  Encryption of the authentication failed 
	 */
	public DSTService(String accessKey, String secretKey)
			throws AuthenticationException {
		super(accessKey, secretKey, "dstlist");
		_includeTimeChanges = false;
		_includePlacesForEveryCountry = true;
		_includeOnlyDstCountries = true;
	}

	/**
	 * Gets the all entries with daylight saving time
	 * 
	 * @return The daylight saving time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<DST> getDaylightSavingTime() throws IllegalArgumentException, ServerSideException {
		return retrieveDstList(null);
	}

	/**
	 * Gets the daylight saving time by ISO3166-1-alpha-2 Country Code
	 * 
	 * @param countryCode
	 * @return The daylight saving time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<DST> getDaylightSavingTime(String countryCode) throws IllegalArgumentException, ServerSideException {
		if (countryCode == null
				|| (countryCode != null && countryCode.isEmpty()))
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		_includeOnlyDstCountries = false;
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("country", countryCode);

		return retrieveDstList(args);
	}

	/**
	 * Gets the daylight saving time by year.
	 * 
	 * @param year
	 *            Year
	 * @return The daylight saving time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<DST> getDaylightSavingTime(int year) throws IllegalArgumentException, ServerSideException {
		if (year <= 0)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		Map<String, String> args = new HashMap<String, String>();
		args.put("year", Integer.toString(year));

		return retrieveDstList(args);
	}

	/**
	 * Gets the daylight saving time by country and year.
	 * 
	 * @param countryCode
	 *            ISO3166-1-alpha-2 Country Code
	 * @param year
	 *            Year.
	 * @return The daylight saving time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<DST> getDaylightSavingTime(String countryCode, int year) throws ServerSideException {
		if ((countryCode == null || (countryCode != null && countryCode
				.isEmpty())) && year <= 0)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		_includeOnlyDstCountries = false;
		Map<String, String> args = new HashMap<String, String>();
		args.put("country", countryCode);
		args.put("year", Integer.toString(year));

		return retrieveDstList(args);
	}
	
	public void setIncludePlacesForEveryCountry(boolean bool) {
		_includePlacesForEveryCountry = bool;
	}
	
	public boolean getIncludePlacesForEveryCountry() {
		return _includePlacesForEveryCountry;
	}
	
	public void setIncludeOnlyDstCountries(boolean bool) {
		_includeOnlyDstCountries = bool;
	}
	
	public boolean getIncludeOnlyDstCountries() {
		return _includeOnlyDstCountries;
	}
	
	public void setIncludeTimeChanges(boolean bool) {
		_includeTimeChanges = bool;
	}
	
	public boolean getIncludeTimeChanges() {
		return _includeTimeChanges;
	}

	private List<DST> retrieveDstList(Map<String, String> args)
			throws ServerSideException {
		Map<String, String> arguments = getArguments();
		if (args != null)
			arguments.putAll(args);

		String result = new String();
		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		XmlUtils.checkForErrors(result);
		return fromXml(result);
	}

	private Map<String, String> getArguments() {
		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		args.put("lang", Language);
		args.put("timechanges", StringUtils.BoolToNum(_includeTimeChanges));
		args.put("onlydst", StringUtils.BoolToNum(_includeOnlyDstCountries));
		args.put("listplaces",
				StringUtils.BoolToNum(_includePlacesForEveryCountry));
		args.put("version", Integer.toString(Version));
		args.put("out", Constants.DefaultReturnFormat);
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));

		return args;
	}

	private static List<DST> fromXml(String result) {
		List<DST> list = new ArrayList<DST>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			NodeList dstlist = root.getElementsByTagName("dstentry");

			for (Node node : XmlUtils.asList(dstlist))
				list.add(DST.fromNode(node));

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		return list;
	}
}
