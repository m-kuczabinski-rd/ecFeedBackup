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

public class StringParameter extends AbstractParameter {

	private String[] fAllowedValues = null;
	private String fDefaultValue;

	public StringParameter(String name, boolean required, String defaultValue, String[] allowedValues) throws GeneratorException {
		super(name, TYPE.STRING, required);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		if(!Arrays.asList(fAllowedValues).contains(fDefaultValue)){
			GeneratorException.report("Inconsistent parameter definition");
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
