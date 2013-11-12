package com.testify.generators;

import com.testify.ecfeed.api.IGeneratorParameter;

public class AbstractParameter implements IGeneratorParameter {

	private String fName;
	private TYPE fType;
	private boolean fRequired;
	private Object fDefault;
	private Object[] fAllowedValues;
	private long fMinValue;
	private long fMaxValue;

	public AbstractParameter(String name, TYPE type, boolean required,
			Object defaultValue, Object[] allowedValues, int minValue, int maxValue){
		fName = name;
		fType = type;
		fRequired = required;
		fDefault = defaultValue;
		fAllowedValues = allowedValues;
		fMinValue = minValue;
		fMaxValue = maxValue;
	}
	
	public AbstractParameter(String name, TYPE type, boolean required,
			Object defaultValue, Object[] allowedValues){
		fName = name;
		fType = type;
		fRequired = required;
		fDefault = defaultValue;
		fAllowedValues = allowedValues;
		fMinValue = 0;
		fMaxValue = Integer.MAX_VALUE;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public TYPE getType() {
		return fType;
	}

	@Override
	public boolean isRequired() {
		return fRequired;
	}

	@Override
	public Object defaultValue() {
		return fDefault;
	}

	@Override
	public Object[] allowedValues() {
		return fAllowedValues;
	}

	@Override
	public long minValue() {
		return fMinValue;
	}

	@Override
	public long maxValue() {
		return fMaxValue;
	}

}
