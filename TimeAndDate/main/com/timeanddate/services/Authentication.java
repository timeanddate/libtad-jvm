package com.timeanddate.services;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.TimeZone;
import java.text.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.timeanddate.services.common.AuthenticationException;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
class Authentication {
	private String _accessKey;
	private String _secretKey;
	private String _serviceName;
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	Authentication(String accessKey, String secretKey, String serviceName) {
		_accessKey = accessKey;
		_secretKey = secretKey;
		_serviceName = serviceName;
	}

	Map<String, String> getAuthenticationArgs() throws AuthenticationException {
		return getArgs(null);
	}

	Map<String, String> getAuthenticationArgs(Map<String, String> seed) throws AuthenticationException {
		return getArgs(seed);
	}

	private Map<String, String> getArgs(Map<String, String> seed) throws AuthenticationException {
		Map<String, String> dict = new HashMap<String, String>(
				seed != null ? seed : new HashMap<String, String>());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timestamp = sdf.format(new Date());
		String message = _accessKey + _serviceName + timestamp;
		String signature;

		try {
			SecretKeySpec signingKey = new SecretKeySpec(_secretKey.getBytes(),
					HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(message.getBytes());
			signature = DatatypeConverter.printBase64Binary(rawHmac);
		} catch (Exception e) {
			throw new AuthenticationException("Failed to generate HMAC: "
					+ e.getMessage());
		}

		dict.put("accesskey", _accessKey);
		dict.put("timestamp", timestamp);
		dict.put("signature", signature);

		return dict;
	}
}
