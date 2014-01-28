package com.testify.ecfeed.generators;

public class BooleanParameter extends AbstractParameter {

	private boolean fDefaultValue;

	public BooleanParameter(String name, boolean required, boolean defaultValue){
		super(name, TYPE.BOOLEAN, required);
		fDefaultValue = defaultValue;
	}

	@Override
	public Object defaultValue() {
		return fDefaultValue;
	}

	@Override
	public boolean test(Object value){
		if (value instanceof Boolean == false){
			return false;
		}
		return true;
	}
}
