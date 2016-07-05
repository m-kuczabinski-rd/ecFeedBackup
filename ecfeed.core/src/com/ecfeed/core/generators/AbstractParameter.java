/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.IGeneratorParameter;

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

	@Override
	public String toString(){
		return fName + "[" + fType + "]";
	}
}
