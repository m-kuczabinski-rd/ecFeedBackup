/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators.api;

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

