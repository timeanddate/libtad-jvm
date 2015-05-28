package com.timeanddate.services;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.Map;

import com.timeanddate.services.common.AuthOptions;
import com.timeanddate.services.common.InMemStore;

public abstract class BaseService {
	public int Version = Constants.DefaultVersion;
	public String Language = Constants.DefaultLanguage;
	Map<String, String> AuthenticationOptions;
	protected String ServiceName;

	public BaseService(String accessKey, String secretKey, String serviceName)
			throws SignatureException, UnsupportedEncodingException {
		ServiceName = serviceName;
		Authentication auth = new Authentication(accessKey, secretKey,
				serviceName);
		AuthenticationOptions = auth.getAuthenticationArgs();
		InMemStore.Store(Authentication.class.getName(), new AuthOptions(
				accessKey, secretKey));
	}
}
