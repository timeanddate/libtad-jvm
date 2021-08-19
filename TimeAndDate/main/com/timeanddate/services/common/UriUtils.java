package com.timeanddate.services.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class UriUtils {
	private static String UTF_8 = StandardCharsets.UTF_8.toString();

	public static String BuildUriString(Map<String, String> args)
			throws UnsupportedEncodingException {
		List<String> items = new ArrayList<String>();
		for (Entry<String, String> entry : args.entrySet())
			items.add(URLEncoder.encode(entry.getKey(), UTF_8).concat("=")
					.concat(URLEncoder.encode(entry.getValue(), UTF_8)));

		return "?" + StringUtils.join(items, "&");
	}
}
