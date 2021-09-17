package com.timeanddate.services;

import java.util.stream.Collectors;
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

import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.tides.Station;
import com.timeanddate.services.dataTypes.time.TADDateTime;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class TidesService extends BaseService {

	private Boolean _onlyHighLow;
	private TADDateTime _startDate;
	private TADDateTime _endDate;
	private Integer _radius;
	private Boolean _subordinate;
	private Integer _interval;
	private Boolean _localTime;

	/**
	 * The Tides service can be used to retrieve predicted tidal data over a given time
	 * interval for one or multiple places.
	 *
	 * @param accessKey
	 * 		Access key.
	 * @param secretKey
	 * 		Secret key.
	 * @throws AuthenticationException
	 * 		Encryption of the authentication failed
	 */
	public TidesService(String accessKey, String secretKey)
		throws AuthenticationException {
		super(accessKey, secretKey, "tides");
	}

	public void setOnlyHighLow(Boolean bool) {
		_onlyHighLow = bool;
	}

	public Boolean getOnlyHighLow() {
		return _onlyHighLow;
	}

	public void setStartDate(TADDateTime startDate) {
		_startDate = startDate;
	}

	public TADDateTime getStartDate() {
		return _startDate;
	}

	public void setEndDate(TADDateTime endDate) {
		_endDate = endDate;
	}

	public TADDateTime getEndDate() {
		return _endDate;
	}

	public void setRadius(Integer radius) {
		_radius = radius;
	}

	public Integer getRadius() {
		return _radius;
	}

	public void setSubordinate(Boolean bool) {
		_subordinate = bool;
	}

	public Boolean getSubordinate() {
		return _subordinate;
	}

	public void setInterval(Integer interval) {
		_interval = interval;
	}

	public Integer getInterval() {
		return _interval;
	}

	public void setLocalTime(Boolean bool) {
		_localTime = bool;
	}

	public Boolean getLocalTime() {
		return _localTime;
	}

	/**
	 * The Tides service can be used to retrieve predicted tidal data over a given time
	 * interval for one or multiple places.
	 *
	 * @param locationId
	 * 		Location id to request tidal data for.
	 * @return List of requested tidal data for the given location id.
	 * @throws ServerSideException
	 * 		The server produced an error message
	 * @throws IllegalArgumentException
	 * 		A required argument was not as expected
	 */
	public List<Station> getTidalData(LocationId locationId) throws IllegalArgumentException, ServerSideException {
		var list = new ArrayList<LocationId>();
		list.add(locationId);
		Map<String, String> arguments = getArguments(list);
		String result = new String();

		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			e.printStackTrace();
		}
		
		XmlUtils.checkForErrors(result);
		return fromXml(result);
	}

	/**
	 * The Tides service can be used to retrieve predicted tidal data over a given time
	 * interval for one or multiple places.
	 *
	 * @param locationId
	 * 		List of location ids to request tidal data for.
	 * @return List of requested tidal data for the given location id.
	 * @throws ServerSideException
	 * 		The server produced an error message
	 * @throws IllegalArgumentException
	 * 		A required argument was not as expected
	 */
	public List<Station> getTidalData(List<LocationId> locationId) throws IllegalArgumentException, ServerSideException {
		Map<String, String> arguments = getArguments(locationId);
		String result = new String();

		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			e.printStackTrace();
		}
		
		XmlUtils.checkForErrors(result);
		return fromXml(result);
	}


	private Map<String, String> getArguments(List<LocationId> locationId) {
		Map<String, String> args = new HashMap<String, String>(AuthenticationOptions);

		args.put("placeid",
				StringUtils.join(locationId
					.stream()
					.map(i -> i.getId())
					.collect(Collectors.toList())
				, ",")
		);
		args.put("version", Integer.toString(Version));
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));
		args.put("out", Constants.DefaultReturnFormat);

		if (_onlyHighLow != null) {
			args.put("onlyhighlow", StringUtils.BoolToNum(_onlyHighLow));
		}
		
		if (_startDate != null) {
			args.put("startdt", _startDate.toString());
		}

		if (_endDate != null) {
			args.put("enddt", _endDate.toString());
		}

		if (_radius != null) {
			args.put("radius", _radius.toString());
		}

		if (_subordinate != null) {
			args.put("subordinate", StringUtils.BoolToNum(_subordinate));
		}

		if (_interval != null) {
			args.put("interval", _interval.toString());
		}

		if (_localTime != null) {
			args.put("localtime", StringUtils.BoolToNum(_localTime));
		}

		return args;
	}

	private static List<Station> fromXml(String result) {
		ArrayList<Station> list = new ArrayList<Station>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();

			NodeList nodes = root.getElementsByTagName("station");

			for (Node node : XmlUtils.asList(nodes)) {
				list.add(Station.fromNode(node));
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		return list;
	}
}
