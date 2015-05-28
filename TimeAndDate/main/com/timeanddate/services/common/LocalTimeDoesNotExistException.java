package com.timeanddate.services.common;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class LocalTimeDoesNotExistException extends Exception {
	public LocalTimeDoesNotExistException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
