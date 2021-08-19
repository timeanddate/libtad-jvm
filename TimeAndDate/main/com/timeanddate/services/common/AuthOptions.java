package com.timeanddate.services.common;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class AuthOptions {
	public String accessKey;
	public String secretKey;

	public AuthOptions(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}
}
