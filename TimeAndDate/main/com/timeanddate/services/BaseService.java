package com.timeanddate.services;

import java.util.Map;

import com.timeanddate.services.common.AuthOptions;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.InMemStore;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public abstract class BaseService {
	public int Version = Constants.DefaultVersion;
	public String Language = Constants.DefaultLanguage;
	Map<String, String> AuthenticationOptions;
	protected String ServiceName;

	public BaseService(String accessKey, String secretKey, String serviceName) throws AuthenticationException {
		ServiceName = serviceName;
		Authentication auth = new Authentication(accessKey, secretKey,
				serviceName);
		AuthenticationOptions = auth.getAuthenticationArgs();
		InMemStore.Store(Authentication.class.getName(), new AuthOptions(
				accessKey, secretKey));
	}
}
