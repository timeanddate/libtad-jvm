package com.timeanddate.services.common;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public interface IPredicate<T> {
	IPredicate<T> of(T type);

	boolean is(T type);
}
