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
			Class<?> typeClass = super.loadClass(className.substring(0, className.lastIndexOf('.')));
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
