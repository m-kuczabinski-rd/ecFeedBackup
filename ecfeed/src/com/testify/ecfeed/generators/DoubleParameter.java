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

package com.testify.ecfeed.generators;

import java.util.Arrays;

import com.testify.ecfeed.generators.api.GeneratorException;

public class DoubleParameter extends AbstractParameter {

	private Double[] fAllowedValues = null;
	private double fDefaultValue;
	private double fMinValue = -Double.MAX_VALUE;
	private double fMaxValue = Double.MAX_VALUE;

	public DoubleParameter(String name, boolean required, double defaultValue){
		super(name, TYPE.DOUBLE, required);
		fDefaultValue = defaultValue;
	}

	public DoubleParameter(String name, boolean required, double defaultValue, Double[] allowedValues) throws GeneratorException {
		super(name, TYPE.DOUBLE, required);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		if(!Arrays.asList(fAllowedValues).contains(fDefaultValue)){
			GeneratorException.report("Inconsistent parameter definition");
		}
	}

	public DoubleParameter(String name, boolean required, double defaultValue, double min, double max) throws GeneratorException {
		super(name, TYPE.DOUBLE, required);
		fDefaultValue = defaultValue;
		fMinValue = min;
		fMaxValue = max;
		if(fDefaultValue <= fMinValue || fDefaultValue >= fMaxValue){
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

	@Override
	public boolean test(Object value){
		if (value instanceof Double == false){
			return false;
		}
		double intValue = (double)value;
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

	public double getMin(){
		return fMinValue;
	}

	public double getMax(){
		return fMaxValue;
	}
}
