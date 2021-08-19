package com.timeanddate.services.common;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class ServerSideException extends Exception {
	private static final long serialVersionUID = 1L;

	public ServerSideException(String message) {
		super(message);
	}
}
