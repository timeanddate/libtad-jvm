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
import com.timeanddate.services.dataTypes.places.Place;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class PlacesService extends BaseService {
	/**
	 * Return coordinates for the Geography object.
	 * <p>
	 * <b>true</b> to include coordinates; otherwise, <b>false</b>. <b>true</b>
	 * is default.
	 */
	private boolean _includeCoordinates;

	/**
	 * The places service can be used to retrieve the list of supported places.
	 * The ids for the places are then used in the other services to indicate
	 * the location to be queried.
	 * 
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException 
	 * 			  Encryption of the authentication failed 
	 */
	public PlacesService(String accessKey, String secretKey)
			throws AuthenticationException {
		super(accessKey, secretKey, "places");
		_includeCoordinates = true;
	}

	/**
	 * Gets list of supported places
	 * 
	 * @return The places
	 * @throws ServerSideException 
	 * 			  The server produced an error message
	 */
	public List<Place> getPlaces() throws ServerSideException {
		Map<String, String> arguments = getArguments();
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
	
	public void setIncludeCoordinates(boolean bool) {
		_includeCoordinates = bool;
	}
	
	public boolean getIncludeCoordinates() {
		return _includeCoordinates;
	}

	private Map<String, String> getArguments() {
		HashMap<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);
		args.put("lang", Language);
		args.put("geo", StringUtils.BoolToNum(_includeCoordinates));
		args.put("version", Integer.toString(Version));
		args.put("out", Constants.DefaultReturnFormat);
		args.put("verbosetime",
				Integer.toString(Constants.DefaultVerboseTimeValue));

		return args;
	}

	private static List<Place> fromXml(String result) {
		ArrayList<Place> list = new ArrayList<Place>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(
					result.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			NodeList nodes = root.getElementsByTagName("place");

			for (Node node : XmlUtils.asList(nodes)) {
				list.add(Place.fromNode(node));
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return list;
	}

}
