package com.testify.ecfeed.adapter.java;


public interface ILoaderProvider {
	public ModelClassLoader getLoader(boolean create, ClassLoader parent);
}
