package com.timeanddate.services;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.DOMException;

import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.UriUtils;
import com.timeanddate.services.common.WebClient;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.places.LocationId;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class DialCodeService extends BaseService {

	/**
	 * Return detailed information about the supplied locations.
	 * <p>
	 * <b>true</b> if include detailed information; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includeLocations;

	/**
	 * Return coordinates for the Geography object.
	 * <p>
	 * <b>true</b> if include coordinates; otherwise, <b>false</b>. <b>true</b>
	 * is default.
	 */
	private boolean _includeCoordinates;

	/**
	 * Adds current time under the location object.
	 * <p>
	 * <b>true</b> if include current time; otherwise, <b>false</b>. <b>true</b>
	 * is default.
	 */
	private boolean _includeCurrentTime;

	/**
	 * Add timezone information under the time object.
	 * <p>
	 * <b>true</b> if include timezone information; otherwise, <b>false</b>.
	 * <b>true</b> is default.
	 */
	private boolean _includeTimezoneInformation;

	private int _number = -1;

	/**
	 * The dialcode service can be used determine which phone number shall be
	 * used to call a specific location.
	 * 
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException 
	 * 			  Encryption of the authentication failed 
	 */
	public DialCodeService(String accessKey, String secretKey)
			throws AuthenticationException {
		super(accessKey, secretKey, "dialcode");
		_includeCurrentTime = true;
		_includeLocations = true;
		_includeCoordinates = true;
		_includeTimezoneInformation = true;
	}

	/**
	 * Gets the dial code for the location you want to call
	 * 
	 * @param toLocation
	 *            To location.
	 * @return The dial code.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public DialCodes getDialCode(LocationId toLocation) throws IllegalArgumentException, ServerSideException {
		if (toLocation == null)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		String id = toLocation.getId();
		if (id.isEmpty())
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		Map<String, String> opts = getOptionalArguments(AuthenticationOptions);
		opts.put("toid", id);

		return retrieveDialCode(opts);
	}

	/**
	 * Gets the dial code for the location you want to call, from where
	 * 
	 * @param toLocation
	 *            To location.
	 * @param fromLocation
	 *            From location.
	 * @return The dial code.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public DialCodes getDialCode(LocationId toLocation, LocationId fromLocation) 
			throws IllegalArgumentException, ServerSideException {
		if (toLocation == null || fromLocation == null)
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		String toId = toLocation.getId();
		String fromId = fromLocation.getId();
		if (toId.isEmpty() || fromId.isEmpty())
			throw new IllegalArgumentException(
					"A required argument is null or empty");

		Map<String, String> opts = getOptionalArguments(AuthenticationOptions);
		opts.put("toid", toId);
		opts.put("fromid", fromId);

		return retrieveDialCode(opts);
	}

	/**
	 * Gets the dial code for the location you want to call, from where with
	 * number
	 * 
	 * @param toLocation
	 *            To location.
	 * @param fromLocation
	 *            From location.
	 * @param number
	 *            Number.
	 * @return The dial code.
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException 
	 * 			  A required argument was not as expected
	 */
	public DialCodes getDialCode(LocationId toLocation,
			LocationId fromLocation, int number) throws IllegalArgumentException, ServerSideException {
		_number = number;
		return getDialCode(toLocation, fromLocation);
	}
	
	public void setIncludeCurrentTime(boolean bool) {
		_includeCurrentTime = bool;
	}
	
	public boolean getIncludeCurrentTime() {
		return _includeCurrentTime;
	}
	
	public void setIncludeLocations(boolean bool) {
		_includeLocations = bool;
	}
	
	public boolean getIncludeLocations() {
		return _includeLocations;
	}
	
	public void setIncludeCoordinates(boolean bool) {
		_includeCoordinates = bool;
	}
	
	public boolean getIncludeCoordinates() {
		return _includeCoordinates;
	}
	
	public void setIncludeTimezoneInformation(boolean bool) {
		_includeTimezoneInformation = bool;
	}
	
	public boolean getIncludeTimezoneInformation() {
		return _includeTimezoneInformation;
	}

	private DialCodes retrieveDialCode(Map<String, String> args) throws ServerSideException {
		String result = new String();
		try {
			String query = UriUtils.BuildUriString(args);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);

			WebClient client = new WebClient();
			result = client.downloadString(uri);
			
		} catch (DOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		XmlUtils.checkForErrors(result);
		return DialCodes.fromXml(result);
	}

	private Map<String, String> getOptionalArguments(
			Map<String, String> existingArguments) {
		Map<String, String> args = new HashMap<String, String>(
				existingArguments);

		args.put("locinfo", StringUtils.BoolToNum(_includeLocations));
		args.put("geo", StringUtils.BoolToNum(_includeCoordinates));
		args.put("time", StringUtils.BoolToNum(_includeCurrentTime));
		args.put("tz", StringUtils.BoolToNum(_includeTimezoneInformation));
		args.put("out", Constants.DefaultReturnFormat);
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));

		if (_number >= 0)
			args.put("number", Integer.toString(_number));

		return args;
	}	
}
