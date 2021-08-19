package com.timeanddate.services.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class InMemStore {
	private static Map<String, Object> _db = new HashMap<String, Object>();

	public static Object Get(String key) {
		return _db.get(key);
	}

	public static void Store(String key, Object val) {
		_db.put(key, val);
	}
}
