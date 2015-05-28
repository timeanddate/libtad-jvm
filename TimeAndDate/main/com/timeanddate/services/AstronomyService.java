package com.timeanddate.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.IPredicate;
import com.timeanddate.services.common.QueriedDateOutOfRangeException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.UriUtils;
import com.timeanddate.services.common.WebClient;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.astro.AstronomyEventClass;
import com.timeanddate.services.dataTypes.astro.AstronomyLocation;
import com.timeanddate.services.dataTypes.astro.AstronomyObjectType;
import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.time.TADDateTime;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class AstronomyService extends BaseService {
	/**
	 * Return longitude and latitude for the geo object.
	 * <p>
	 * <b>true</b> if return latitude and longitude; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private EnumSet<AstronomyEventClass> _types;

	/**
	 * Adds coordinates to the geography object
	 * <p>
	 * <b>true</b> if return coordinates; otherwise, <b>false</b>. <b>true</b>
	 * is default.
	 */
	private boolean _includeCoordinates;

	/**
	 * Adds timestamps (local time) to all events.
	 * <p>
	 * <b>true</b> if include ISO time; otherwise, <b>false</b>. <b>false</b> is
	 * default.
	 */
	private boolean _includeISOTime;

	/**
	 * Adds UTC timestamps to all events.
	 * <p>
	 * <b>true</b> if include UTC time; otherwise, <b>false</b>.<b>false</b> is
	 * default.
	 */
	private boolean _includeUTCTime;

	/**
	 * Search radius for translating coordinates (parameter placeid) to
	 * locations. Coordinates that could not be translated will yield results
	 * for the actual geographical position – if you would like to query for
	 * times at an exact location, specify a radius of zero (0).
	 * <p>
	 * The radius in kilometers. Default is infinite, but only locations within
	 * the same country and time zone are considered.
	 */
	private int _radius;

	/**
	 * The astronomy service can be used retrieve rise, set, noon and twilight
	 * times for sun and moon for all locations. The service also exposes the
	 * azimuth of the events and altitude and distance (for the noon event).
	 * 
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException
	 * 			  Encryption of the authentication failed 
	 */
	public AstronomyService(String accessKey, String secretKey) throws AuthenticationException {
		super(accessKey, secretKey, "astronomy");
		_includeCoordinates = true;
		_includeISOTime = false;
		_includeUTCTime = false;
	}
	
	/**
	 * Gets the specified object type (Moon, Sun) for a specified place by start
	 * date.
	 * 
	 * @param objectType
	 *            The astronomical object type (Moon or Sun)
	 * @param placeId
	 *            Place identifier.
	 * @param startDate
	 *            Start date.
	 * @return A list of astronomical information.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<AstronomyLocation> getAstronomicalInfo(
			AstronomyObjectType objectType, LocationId placeId,
			Calendar startDate) throws IllegalArgumentException, ServerSideException  {
		TADDateTime dt = new TADDateTime(
				startDate.get(Calendar.YEAR),
				startDate.get(Calendar.MONTH),
				startDate.get(Calendar.DAY_OF_MONTH),
				startDate.get(Calendar.HOUR),
				startDate.get(Calendar.MINUTE),
				startDate.get(Calendar.SECOND));
		
		return getAstronomicalInfo(objectType, placeId, dt);
	}

	/**
	 * Gets the specified object type (Moon, Sun) for a specified place by start
	 * date.
	 * 
	 * @param objectType
	 *            The astronomical object type (Moon or Sun)
	 * @param placeId
	 *            Place identifier.
	 * @param startDate
	 *            Start date.
	 * @return A list of astronomical information.
 	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<AstronomyLocation> getAstronomicalInfo(
			AstronomyObjectType objectType, LocationId placeId,
			TADDateTime startDate) throws IllegalArgumentException, ServerSideException {
		if (placeId == null || startDate == null)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		String id = placeId.getId();
		if (id.isEmpty())
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		args.put("placeid", id);
		args.put("object", objectType.toString().toLowerCase());
		args.put("startdt", startDate.getISO8601Date());

		return retrieveAstronomicalInfo(args);
	}

	/**
	 * Gets the specified object type (Moon, Sun) for a specified place by start
	 * date.
	 * 
	 * @param objectType
	 *            The astronomical object type (Moon or Sun)
	 * @param placeId
	 *            Place identifier.
	 * @param startDate
	 *            Start date.
	 * @param endDate
	 *            End date.
	 * @return A list of astronomical information.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws QueriedDateOutOfRangeException 
	 * 			  There was a mismatch between the two dates provided
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<AstronomyLocation> getAstronomicalInfo(
			AstronomyObjectType objectType, LocationId placeId,
			Calendar startDate, Calendar endDate) 
					throws IllegalArgumentException, QueriedDateOutOfRangeException, ServerSideException {
		TADDateTime sd = new TADDateTime(
				startDate.get(Calendar.YEAR),
				startDate.get(Calendar.MONTH),
				startDate.get(Calendar.DAY_OF_MONTH),
				startDate.get(Calendar.HOUR),
				startDate.get(Calendar.MINUTE),
				startDate.get(Calendar.SECOND));
		
		TADDateTime ed = new TADDateTime(
				endDate.get(Calendar.YEAR),
				endDate.get(Calendar.MONTH),
				endDate.get(Calendar.DAY_OF_MONTH),
				endDate.get(Calendar.HOUR),
				endDate.get(Calendar.MINUTE),
				endDate.get(Calendar.SECOND));
		return getAstronomicalInfo(objectType, placeId, sd, ed);
	}
	
	/**
	 * Gets the specified object type (Moon, Sun) for a specified place by start
	 * date.
	 * 
	 * @param objectType
	 *            The astronomical object type (Moon or Sun)
	 * @param placeId
	 *            Place identifier.
	 * @param startDate
	 *            Start date.
	 * @param endDate
	 *            End date.
	 * @return A list of astronomical information.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws QueriedDateOutOfRangeException 
	 * 			  There was a mismatch between the two dates provided
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public List<AstronomyLocation> getAstronomicalInfo(AstronomyObjectType objectType, LocationId placeId,
			TADDateTime startDate, TADDateTime endDate) 
					throws IllegalArgumentException, QueriedDateOutOfRangeException, ServerSideException {
		if (placeId == null || startDate == null || endDate == null)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		String id = placeId.getId();
		if (id.isEmpty())
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		if (endDate.getTimeInTicks() < startDate.getTimeInTicks())
			throw new QueriedDateOutOfRangeException(
					"End date cannot be before Start Date");

		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		args.put("placeid", id);
		args.put("object", objectType.toString().toLowerCase());

		args.put("startdt", startDate.getISO8601Date());
		args.put("enddt", endDate.getISO8601Date());

		return retrieveAstronomicalInfo(args);
	}
	
	public void setRadius(int radius) {
		_radius = radius;
	}
	
	public int getRadius() {
		return _radius;
	}
	
	public void setIncludeUTCTime(boolean bool) {
		_includeUTCTime = bool;
	}
	
	public boolean getIncludeUTCTime() {
		return _includeUTCTime;
	}
	
	public void setIncludeISOTime(boolean bool) {
		_includeISOTime = bool;
	}
	
	public boolean getIncludeISOTime() {
		return _includeISOTime;
	}
	
	public void setIncludeCoordinates(boolean bool) {
		_includeCoordinates = bool;
	}
	
	public boolean getIncludeCoordinates() {
		return _includeCoordinates;
	}
	
	public void setAstronomyEventTypes(EnumSet<AstronomyEventClass> types) {
		_types = types;
	}
	
	public void addAstronomyEventType(AstronomyEventClass type) {
		_types.add(type);
	}
	
	public EnumSet<AstronomyEventClass> getAstronomyEventTypes() {
		return _types;
	}

	private List<AstronomyLocation> retrieveAstronomicalInfo(
			Map<String, String> args) throws ServerSideException  {
		Map<String, String> arguments = getOptionalArguments(args);
		String result = new String();
		
		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);
			
		} catch (DOMException | IOException e) { 
				e.printStackTrace();
		}
		
		XmlUtils.checkForErrors(result);
		return fromXml(result); 		
	}

	private Map<String, String> getOptionalArguments(Map<String, String> args) {
		Map<String, String> optionalArgs = new HashMap<String, String>(args);
		String types = getAstronomyEventTypesAsStr();

		optionalArgs.put("geo", StringUtils.BoolToNum(_includeCoordinates));
		optionalArgs.put("isotime", StringUtils.BoolToNum(_includeISOTime));
		optionalArgs.put("lang", Language);
		optionalArgs.put("radius", Integer.toString(_radius));
		optionalArgs.put("utctime", StringUtils.BoolToNum(_includeUTCTime));
		optionalArgs.put("out", Constants.DefaultReturnFormat);
		optionalArgs.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));

		if (types != null && !types.isEmpty())
			optionalArgs.put("types", types);

		return optionalArgs;
	}

	private static List<AstronomyLocation> fromXml(String result)
			throws DOMException {
		List<AstronomyLocation> list = new ArrayList<AstronomyLocation>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			NodeList nodes = root.getElementsByTagName("location");
			
			for (Node location : XmlUtils.asList(nodes)) {
				list.add(AstronomyLocation.fromNode(location));
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	private String getAstronomyEventTypesAsStr() {
		if (_types == null)
			return "";

		ArrayList<String> includedStrings = new ArrayList<String>();
		for (final AstronomyEventClass type : AstronomyEventClass.values()) {
			if (_types.contains(type))
				includedStrings.add(StringUtils
						.resolveAstronomyEventClass(where.of(type)).Command);
		}

		String included = StringUtils.join(includedStrings, ",");
		return included;
	}

	private IPredicate<AstronomyEventClass> where = new IPredicate<AstronomyEventClass>() {
		AstronomyEventClass type;

		public boolean is(AstronomyEventClass t) {
			return t == type;
		}

		public IPredicate<AstronomyEventClass> of(AstronomyEventClass t) {
			type = t;
			return this;
		}
	};
}
