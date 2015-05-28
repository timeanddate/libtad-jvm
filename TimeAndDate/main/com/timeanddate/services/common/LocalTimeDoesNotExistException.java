package com.timeanddate.services.common;

public class LocalTimeDoesNotExistException extends Exception {
	public LocalTimeDoesNotExistException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
