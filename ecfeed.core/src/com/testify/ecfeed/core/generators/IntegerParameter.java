/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.generators;

import java.util.Arrays;

import com.testify.ecfeed.core.generators.api.GeneratorException;

public class IntegerParameter extends AbstractParameter {

	private Integer[] fAllowedValues = null;
	private int fDefaultValue;
	private int fMinValue = Integer.MIN_VALUE;
	private int fMaxValue = Integer.MAX_VALUE;

	public IntegerParameter(String name, boolean required, int defaultValue){
		super(name, TYPE.INTEGER, required);
		fDefaultValue = defaultValue;
	}

	public IntegerParameter(String name, boolean required, int defaultValue, Integer[] allowedValues) throws GeneratorException {
		super(name, TYPE.INTEGER, required);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		checkAllowedValues(fDefaultValue, fAllowedValues);
	}

	private void checkAllowedValues(Integer defaultValue, Integer[] allowedValues) throws GeneratorException {
		if(!Arrays.asList(allowedValues).contains(defaultValue)){
			GeneratorException.report("Inconsistent parameter definition");
		}
	}

	public IntegerParameter(String name, boolean required, int defaultValue, int min, int max) throws GeneratorException {
		super(name, TYPE.INTEGER, required);
		fDefaultValue = defaultValue;
		fMinValue = min;
		fMaxValue = max;
		checkRange(fDefaultValue, fMinValue, fMaxValue);
	}

	private void checkRange(int value, int minValue, int maxValue) throws GeneratorException {
		if(value < minValue || value > maxValue){
			GeneratorException.report("Inconsistent parameter definition");
		}
	}

	@Override
	public Object[] allowedValues(){
		return fAllowedValues;
	}

	@Override
	public Object defaultValue() {
		return fDefaultValue;
	}

	public void setDefaultValue(Object defaultValue) throws GeneratorException {
		int tmpDefaultValue = (int)defaultValue;

		checkRange(tmpDefaultValue, fMinValue, fMaxValue);

		if (fAllowedValues != null) {
			checkAllowedValues(fDefaultValue, fAllowedValues);
		}

		fDefaultValue = (int)tmpDefaultValue;
	}	

	@Override
	public boolean test(Object value){
		if (value instanceof Integer == false){
			return false;
		}
		int intValue = (Integer)value;
		if(allowedValues() != null){
			boolean isAllowed = false;
			for(Object allowed : allowedValues()){
				if(value.equals(allowed)){
					isAllowed = true;
				}
			}
			return isAllowed;
		}
		return (intValue >= fMinValue && intValue <= fMaxValue);
	}

	public int getMin(){
		return fMinValue;
	}

	public int getMax(){
		return fMaxValue;
	}
}
