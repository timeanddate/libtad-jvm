package com.timeanddate.services.common;

public interface IPredicate<T> {
	IPredicate<T> of(T type);

	boolean is(T type);
}
