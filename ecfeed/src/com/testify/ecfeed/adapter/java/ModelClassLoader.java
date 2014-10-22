package com.testify.ecfeed.adapter.java;

import java.net.URL;
import java.net.URLClassLoader;

public class ModelClassLoader extends URLClassLoader {

	public ModelClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public ModelClassLoader(URL[] urls) {
		super(urls, null);
	}

	@Override
	public Class<?> loadClass(String className) {
		try {
			return super.loadClass(className);
		} catch (Throwable e) {
		}

		try {
			String topClass = className.substring(0, className.lastIndexOf('.'));
			Class<?> typeClass = super.loadClass(topClass);
			for (Class<?> innerClass : typeClass.getDeclaredClasses()) {
				if (innerClass.getCanonicalName().equals(className)) {
					return innerClass;
				}
			}
		} catch (Throwable e) {
		}

		return null;
	}

}
