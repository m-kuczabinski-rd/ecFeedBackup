package com.testify.ecfeed.abstraction.java;


public interface ILoaderProvider {
	public ModelClassLoader getLoader(boolean create, ClassLoader parent);
}
