package com.timeanddate.services.common;

public class MissingTimeChangesException extends Exception {
	public MissingTimeChangesException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;
}
