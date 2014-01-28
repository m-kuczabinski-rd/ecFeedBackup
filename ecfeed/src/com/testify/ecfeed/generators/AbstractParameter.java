package com.testify.ecfeed.generators;

import com.testify.ecfeed.generators.api.IGeneratorParameter;

public class AbstractParameter implements IGeneratorParameter {

	private String fName;
	private TYPE fType;
	private boolean fRequired;

	public AbstractParameter(String name, TYPE type, boolean required){
		fName = name;
		fType = type;
		fRequired = required;
	}
	
	public AbstractParameter(String name, TYPE type, boolean required,
			Object defaultValue, Object[] allowedValues){
		fName = name;
		fType = type;
		fRequired = required;
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
		return null;
	}

	@Override
	public Object[] allowedValues() {
		return null;
	}

	@Override
	public boolean test(Object value){
		return false;
	}
}
