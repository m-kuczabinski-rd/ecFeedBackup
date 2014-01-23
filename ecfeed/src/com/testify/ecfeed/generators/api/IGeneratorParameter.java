package com.testify.ecfeed.generators.api;

public interface IGeneratorParameter {
	public enum TYPE{
		BOOLEAN, INTEGER, FLOAT, STRING
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
	 * Minimal value of numeric parameters.
	 */
	public long minValue();
	
	/*
	 * Maximum value of numeric parameters
	 */
	public long maxValue();
}

