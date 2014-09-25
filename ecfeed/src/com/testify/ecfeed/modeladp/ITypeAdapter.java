package com.testify.ecfeed.modeladp;

public interface ITypeAdapter {
	public boolean compatible(String type);
	// returns null if conversion is not possible
	public String convert(String value);
	public String defaultValue();
}
