package com.testify.ecfeed.generators.api;

public interface IGeneratorParameter {
	public enum TYPE{
		BOOLEAN, INTEGER, DOUBLE, STRING
	}

	/*
	 * Name of a parameter
	 */
	public String getName();
	
	/*
	 * Parameter's type
	 */
	public TYPE getType();
	
	/*
	 * Information, whether this parameter is required or optional.
	 */
	public boolean isRequired();
	
	/*
	 * Default value of parameter.
	 */
	public Object defaultValue();
	
	/*
	 * Set of allowed values of the parameter. If any value is permitted this 
	 * function should return null
	 */
	public Object[] allowedValues();
	
	/*
	 * Checks if provided value is valid for this parameter
	 */
	public boolean test(Object value);
	
}

