package com.timeanddate.services;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;

import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.UriUtils;
import com.timeanddate.services.common.WebClient;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.time.TADDateTime;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class ConvertTimeService extends BaseService {

	/**
	 * Search radius for translating coordinates (parameters fromid and toid) to
	 * locations. Coordinates that could not be translated will yield results
	 * for the actual geographical position.
	 * <p>
	 * The radius in kilometers.
	 */
	private int _radius;

	/**
	 * Add a list of time changes during the year to the location object. This
	 * listing e.g. shows changes caused by daylight savings time.
	 * <p>
	 * <b>true</b> if include time changes; otherwise, <b>false</b>. <b>true</b>
	 * is default.
	 */
	private boolean _includeTimeChanges;

	/**
	 * Add timezone information under the time object.
	 * <p>
	 * <b>true</b> if include timezone information; otherwise,
	 * <b>false</b>.<b>true</b> is default.
	 */
	private boolean _includeTimezoneInformation;

	/**
	 * The converttime service can be used to convert any time from UTC or any
	 * of the supported locations to any other of the supported locations.
	 * 
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException 
	 * 			  Encryption of the authentication failed 
	 */
	public ConvertTimeService(String accessKey, String secretKey)
			throws AuthenticationException {
		super(accessKey, secretKey, "converttime");
		_includeTimeChanges = true;
		_includeTimezoneInformation = true;
	}

	/**
	 * Converts the time by using a LocationId, a ISO-string and a list of IDs
	 * to convert to.
	 * 
	 * @param fromId
	 *            The places identifier
	 * @param iso
	 *            ISO 8601-formatted string.
	 * @param toIds
	 *            The place IDs to convert to.
	 * @return The converted time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public ConvertedTimes convertTime(LocationId fromId, String iso,
			List<LocationId> toIds) throws IllegalArgumentException, ServerSideException {
		if (fromId == null || iso == null || (iso != null && iso.isEmpty()))
			throw new IllegalArgumentException("A required argument is null or empty");

		String id = fromId.getId();
		if (id.isEmpty())
			throw new IllegalArgumentException("ID empty");

		return executeConvertTime(id, iso, toIds);
	}

	/**
	 * 
	 * @param fromId
	 * 			  The places identifier
	 * @param iso
	 * 			  ISO 8601-formatted string.
	 * @return The converted time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public ConvertedTimes convertTime(LocationId fromId, String iso) 
			throws IllegalArgumentException, ServerSideException {
		return convertTime(fromId, iso, null);
	}

	/**
	 * Converts the time by using a LocationId, a Calendar and a list of IDs to
	 * convert to.
	 * 
	 * @param fromId
	 *            The places identifier
	 * @param date
	 *            Date.
	 * @param toIds
	 *            The place IDs to convert to.
	 * @return The converted time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public ConvertedTimes convertTime(LocationId fromId, Calendar date,
			List<LocationId> toIds) throws IllegalArgumentException, ServerSideException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSXXX");
		return convertTime(fromId, sdf.format(date), toIds);
	}
	 
	/**
	 * Converts the time by using a LocationId, a Calendar and a list of IDs to
	 * convert to.
	 * 
	 * @param fromId
	 *            The places identifier
	 * @param date
	 *            Date.
	 * @param toIds
	 *            The place IDs to convert to.
	 * @return The converted time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public ConvertedTimes convertTime(LocationId fromId, TADDateTime date,
			List<LocationId> toIds) throws IllegalArgumentException, ServerSideException {
		return convertTime(fromId, date.toString(), toIds);
	}

	/**
	 * Converts the time by using a LocationId and a Calendar to convert to.
	 * 
	 * @param fromId
	 *            The places identifier
	 * @param date
	 *            Date.
	 * @return The converted time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public ConvertedTimes convertTime(LocationId fromId, Calendar date) 
			throws IllegalArgumentException, ServerSideException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSXXX");
		return convertTime(fromId, sdf.format(date));
	}
	
	/**
	 * Converts the time by using a LocationId and a Calendar to convert to.
	 * 
	 * @param fromId
	 *            The places identifier
	 * @param date
	 *            Date.
	 * @return The converted time.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public ConvertedTimes convertTime(LocationId fromId, TADDateTime date) 
			throws IllegalArgumentException, ServerSideException {
		return convertTime(fromId, date.toString());
	}
	
	public void setRadius(int radius) {
		_radius = radius;
	}
	
	public int getRadius() {
		return _radius;
	}
	
	public void setIncludeTimeChanges(boolean bool) {
		_includeTimeChanges = bool;
	}
	
	public boolean getIncludeTimeChanges() {
		return _includeTimeChanges;
	}
	
	public void setIncludeTimezoneInformation(boolean bool) {
		_includeTimezoneInformation = bool;
	}
	
	public boolean getIncludeTimezoneInformation() {
		return _includeTimezoneInformation;
	}

	private ConvertedTimes executeConvertTime(String fromId, String iso,
			List<LocationId> toIds) throws IllegalArgumentException, ServerSideException {
		if ((fromId == null || (fromId != null && fromId.isEmpty()))
				|| (iso == null || (iso != null && iso.isEmpty())))
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		Map<String, String> arguments = getCommonArguments(fromId);
		String result = new String();
		arguments.put("iso", iso);

		if (toIds != null)
			arguments.putAll(getArgumentsForToIds(toIds));

		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);
			XmlUtils.checkForErrors(result);
			
		} catch (DOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ConvertedTimes.fromXml(result);
	}

	private Map<String, String> getArgumentsForToIds(List<LocationId> toIds)
			throws IllegalArgumentException {
		Map<String, String> args = new HashMap<String, String>();
		List<String> list = new ArrayList<String>();

		for (LocationId id : toIds) {
			String idstr = id.getId();
			if (idstr != null && !idstr.isEmpty() && !idstr.contains(","))
				list.add(idstr);
			else if (idstr != null && !idstr.isEmpty() && idstr.contains(","))
				throw new IllegalArgumentException("Place ID cannot contain any commas");
		}

		args.put("toid", StringUtils.join(list, ","));

		return args;
	}

	private Map<String, String> getCommonArguments(String fromId) {
		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		args.put("timechanges", StringUtils.BoolToNum(_includeTimeChanges));
		args.put("tz", StringUtils.BoolToNum(_includeTimezoneInformation));
		args.put("fromid", fromId);
		args.put("lang", Language);
		args.put("version", Integer.toString(Version));
		args.put("radius", Integer.toString(_radius));
		args.put("out", Constants.DefaultReturnFormat);
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));
		return args;
	}
}
