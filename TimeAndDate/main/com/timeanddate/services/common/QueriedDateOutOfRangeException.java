package com.timeanddate.services.common;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class QueriedDateOutOfRangeException extends Exception {
	public QueriedDateOutOfRangeException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
