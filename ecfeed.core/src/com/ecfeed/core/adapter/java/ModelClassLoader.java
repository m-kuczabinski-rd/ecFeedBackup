/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.java;

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
