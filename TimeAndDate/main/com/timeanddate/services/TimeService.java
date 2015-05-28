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
import com.timeanddate.services.dataTypes.places.*;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class TimeService extends BaseService {
	/**
	 * Search radius for translating coordinates (parameter placeid) to
	 * locations. Coordinates that could not be translated will yield results
	 * for the actual geographical position.
	 * <p>
	 * The radius in kilometers. Default is infinite, but only locations within
	 * the same country and time zone are considered.
	 */
	private int _radius;

	/**
	 * Return coordinates for the Geography object.
	 * <p>
	 * <b>true</b> if return coordinates; otherwise, <b>false</b>. <b>true</b>
	 * is default.
	 */
	private boolean _includeCoordinates;

	/**
	 * Controls if the astronomy element with information about sunrise and
	 * sunset shall be added to the result.
	 * <p>
	 * <b>true</b> if return sunrise and sunset; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includeSunriseAndSunset;

	/**
	 * Adds current time under the location object.
	 * <p>
	 * <b>true</b> if add current time to location; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includeCurrentTimeToLocation;

	/**
	 * Add a list of time changes during the year to the location object. This
	 * listing e.g. shows changes caused by daylight savings time.
	 * <p>
	 * <b>true</b> if add list of time changes; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includeListOfTimeChanges;

	/**
	 * Add timezone information under the time object.
	 * <p>
	 * <b>true</b> if add timezone information; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includeTimezoneInformation;

	/**
	 * The timeservice service can be used to retrieve the current time in one
	 * or more places. Additionally, information about time zones and related
	 * changes and the time of sunrise and sunset can be queried.
	 * 
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException 
	 * 			  Encryption of the authentication failed 
	 */
	public TimeService(String accessKey, String secretKey)
			throws AuthenticationException { 
		super(accessKey, secretKey, "timeservice");

		_includeCoordinates = true;
		_includeSunriseAndSunset = true;
		_includeCurrentTimeToLocation = true;
		_includeListOfTimeChanges = true;
		_includeTimezoneInformation = true;
	}

	/**
	 * Retrieves the current time for place by ID.
	 * 
	 * @param placeId
	 *            Place identifier.
	 * @return The current time for place.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<Location> currentTimeForPlace(LocationId placeId) throws IllegalArgumentException, ServerSideException {
		if (placeId == null)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		String id = placeId.getId();
		if (id != null && id.isEmpty())
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		return retrieveCurrentTime(id);
	}
	
	public void setIncludeCoordinates(boolean bool) {
		_includeCoordinates = bool;
	}
	
	public boolean getIncludeCoordinates() {
		return _includeCoordinates;
	}
	
	public void setRadius(int radius) {
		_radius = radius;
	}
	
	public int getRadius() {
		return _radius;
	}
	
	public void setIncludeSunriseAndSunset(boolean bool) {
		_includeSunriseAndSunset = bool;
	}
	
	public boolean getIncludeSunriseAndSunset() {
		return _includeSunriseAndSunset;
	}
	
	public void setIncludeCurrentTimeToLocation(boolean bool) {
		_includeCurrentTimeToLocation = bool;
	}
	
	public boolean getIncludeCurrentTimeToLocation() {
		return _includeCurrentTimeToLocation;
	}
	
	public void setIncludeListOfTimeChanges(boolean bool) {
		_includeListOfTimeChanges = bool;
	}
	
	public boolean getIncludeListOfTimeChanges() {
		return _includeListOfTimeChanges;
	}

	private List<Location> retrieveCurrentTime(String placeid) throws ServerSideException {
		Map<String, String> arguments = getArguments(placeid);
		String result = new String();
		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);			
		} catch(UnsupportedEncodingException | MalformedURLException e) {
			
		}
		
		XmlUtils.checkForErrors(result);
		return FromXml(result);
	}

	private Map<String, String> getArguments(String placeId) {
		HashMap<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		args.put("geo", StringUtils.BoolToNum(_includeCoordinates));
		args.put("lang", Language);
		args.put("radius", Integer.toString(_radius));
		args.put("sun", StringUtils.BoolToNum(_includeSunriseAndSunset));
		args.put("time", StringUtils.BoolToNum(_includeCurrentTimeToLocation));
		args.put("timechanges", StringUtils.BoolToNum(_includeListOfTimeChanges));
		args.put("tz", StringUtils.BoolToNum(_includeTimezoneInformation));
		args.put("out", Constants.DefaultReturnFormat);
		args.put("placeid", placeId);
		args.put("version", Integer.toString(Version));
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));

		return args;
	}

	private static List<Location> FromXml(String result) {
		ArrayList<Location> list = new ArrayList<Location>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			NodeList nodes = root.getElementsByTagName("location");

			for (Node node : XmlUtils.asList(nodes)) {
				list.add(Location.fromNode(node));
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return list;
	}
}
