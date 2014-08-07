package com.testify.ecfeed.modelif.java.category;

public interface ITypeAdapter {
	public boolean compatible(String type);
	// returns null if conversion is not possible
	public String convert(String value);
	public String defaultValue();
}
