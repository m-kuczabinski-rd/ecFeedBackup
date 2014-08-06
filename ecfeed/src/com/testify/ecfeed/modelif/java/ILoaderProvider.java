package com.testify.ecfeed.modelif.java;

import java.net.URLClassLoader;

public interface ILoaderProvider {
	public ModelClassLoader getLoader(boolean create, URLClassLoader parent);
}
