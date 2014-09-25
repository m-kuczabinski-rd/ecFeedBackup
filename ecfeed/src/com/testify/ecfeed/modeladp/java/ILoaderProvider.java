package com.testify.ecfeed.modeladp.java;


public interface ILoaderProvider {
	public ModelClassLoader getLoader(boolean create, ClassLoader parent);
}
