package com.testify.ecfeed.generators;

import java.util.Arrays;

import com.testify.ecfeed.generators.api.GeneratorException;

public class StringParameter extends AbstractParameter {

	private String[] fAllowedValues = null;
	private String fDefaultValue;

	public StringParameter(String name, boolean required, String defaultValue, String[] allowedValues) throws GeneratorException {
		super(name, TYPE.STRING, required);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		if(!Arrays.asList(fAllowedValues).contains(fDefaultValue)){
			throw new GeneratorException("Inconsistent parameter definition");
		}
	}

	public StringParameter(String name, boolean required, String defaultValue){
		super(name, TYPE.STRING, required);
		fDefaultValue = defaultValue;
	}

	@Override
	public Object[] allowedValues(){
		return fAllowedValues;
	}

	@Override
	public Object defaultValue() {
		return fDefaultValue;
	}

	@Override
	public boolean test(Object value){
		if (value instanceof String == false){
			return false;
		}
		if(allowedValues() != null){
			boolean isAllowed = false;
			for(Object allowed : allowedValues()){
				if(value.equals(allowed)){
					isAllowed = true;
				}
			}
			return isAllowed;
		}
		return true;
	}

}
