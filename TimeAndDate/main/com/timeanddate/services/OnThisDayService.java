package com.timeanddate.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.IPredicate;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.UriUtils;
import com.timeanddate.services.common.WebClient;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.dataTypes.onthisday.OTDEventType;

/**
 *
 * @author Daniel Alvsåker <daniel@timeanddate.com>
 *
 */
public class OnThisDayService extends BaseService {
	private EnumSet<OTDEventType> _types;

	/**
	 * The onthisday service can be used to retrieve events, births and deaths for
	 * a specific date.
	 *
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException
	 * 			  Encryption of the authentication failed
	 */
	private OnThisDayService(String accessKey, String secretKey)
			throws AuthenticationException {
		super(accessKey, secretKey, "onthisday");
	}

	/**
	 * The onthisday service can be used to retrieve events, births and deaths for
	 * a specific date.
	 *
	 * @param month
	 *            The month for which the events should be retrieved.
	 * @param day
	 *            The day for which the events should be retrieved.
	 * @return List of requested event types for the given date.
	 * @throws ServerSideException
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException
	 * 			  A required argument was not as expected
	 */
	public OnThisDayEvents eventsOnThisDay(int month, int day) throws IllegalArgumentException, ServerSideException {
		if (month <= 0 || day <= 0)
			throw new IllegalArgumentException("Month or day cannot be 0 or less.");

		return retrieveEventsOnThisDay(month, day);
	}

	/**
	 * The onthisday service can be used to retrieve events, births and deaths for
	 * a specific date. This overload uses the current date by default.
	 *
	 * @return List of requested event types for the current date.
	 * @throws ServerSideException
	 * 			  The server produced an error message
	 * @throws IllegalArgumentException
	 * 			  A required argument was not as expected
	 */
	public OnThisDayEvents eventsOnThisDay() throws IllegalArgumentException, ServerSideException {
		return retrieveEventsOnThisDay(
			Calendar.getInstance().get(Calendar.MONTH),
			Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
		);
	}

	private OnThisDayEvents retrieveEventsOnThisDay(int year, int month) throws ServerSideException {
		Map<String, String> arguments = getArguments(year, month);
		String result = new String();

		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);
			XmlUtils.checkForErrors(result);

		} catch (UnsupportedEncodingException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return OnThisDayEvents.fromXml(result);
	}

	public void setEventTypes(EnumSet<OTDEventType> types) {
		_types = types;
	}

	public void addEventType(OTDEventType type) {
		_types.add(type);
	}

	public EnumSet<OTDEventType> getEventTypes() {
		return _types;
	}

	private Map<String, String> getArguments(int month, int day) {
		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		String types = getEventTypesAsStr();
		args.put("month", Integer.toString(month));
		args.put("day", Integer.toString(day));
		args.put("lang", Language);
		args.put("version", Integer.toString(Version));
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));
		args.put("out", Constants.DefaultReturnFormat);

		if (types != null && !types.isEmpty())
			args.put("types", types);

		if (month > 0)
			args.put("month", Integer.toString(month));

		if (day > 0)
			args.put("day", Integer.toString(day));

		return args;
	}

	private String getEventTypesAsStr() {
		if (_types == null)
			return "";

		ArrayList<String> includedStrings = new ArrayList<String>();
		for (final OTDEventType type : OTDEventType.values()) {
			if (_types.contains(type))
				includedStrings
						.add(StringUtils.resolveOTDEventTypes(where.of(type)).Command);
		}

		String included = StringUtils.join(includedStrings, ",");
		return included;
	}

	private IPredicate<OTDEventType> where = new IPredicate<OTDEventType>() {
		OTDEventType type;

		public boolean is(OTDEventType t) {
			return t == type;
		}

		public IPredicate<OTDEventType> of(OTDEventType t) {
			type = t;
			return this;
		}
	};
}
