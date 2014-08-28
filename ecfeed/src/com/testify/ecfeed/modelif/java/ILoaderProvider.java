package com.testify.ecfeed.modelif.java;


public interface ILoaderProvider {
	public ModelClassLoader getLoader(boolean create, ClassLoader parent);
}
