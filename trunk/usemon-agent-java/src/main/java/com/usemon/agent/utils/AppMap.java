package com.usemon.agent.utils;

/** 
 * Case Insensitive HashMap() - If the maps key is a string then the following keys are all considered equal:
 *   myKey<br>
 *   MYKEY<br>
 *   MyKey<br>
 *   ...
 * 
 * Other than that this class works like a regular HashMap.
 */

import java.util.HashMap;
import java.util.Map;

public class AppMap extends HashMap {
	private static final long serialVersionUID = -6412857038808994083L;

	public AppMap() {

	}

	/**
	 * Constructs an empty HashMap with the default initial capacity (16) and the default load factor (0.75).
	 * 
	 */

	public AppMap(int initialCapacity) {
		super(initialCapacity);
	}

	/** Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75). */
	public AppMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/** Constructs an empty HashMap with the specified initial capacity and load factor. */
	public AppMap(Map m) {
		putAll(m);
	}

	public Object put(Object key, Object object) {
		return super.put(convertKey(key), object);
	}

	public boolean containsKey(Object key) {
		// The normal case is done first as a performance optimization. It seems to make checks around 30% faster
		// due to only converting the case of the string comparison only when required.
		return super.containsKey(key) || super.containsKey(convertKey(key));
	}

	public Object get(Object key) {
		return super.get(convertKey(key));
	}

	public static Object get(Map map, Object key) throws Exception {
		Object object = map.get(key);

		if (object != null)
			return object;
		else
			throw new Exception(key + " does not exist in the HashMap.");
	}

	protected Object convertKey(Object key) {
		if (key instanceof String && key != null)
			key = key.toString().toLowerCase();

		return key;

	}

	public static Map createInstance() {
		return new AppMap();
	}

	public static void main(String[] args) {
		Map map = new HashMap();
		map.put("HeLLo", "world");
		System.out.println("Should return null: " + map.get("HELLO"));
		System.out.println("Contents of HashMap=" + map);
		map = new AppMap(map);
		System.out.println("Should return 'world': " + map.get("HELLO"));
		System.out.println("Contents of AppMap=" + map);
	}

}