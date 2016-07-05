/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter;

public interface ITypeAdapter {
	public boolean compatible(String type);
	// returns null if conversion is not possible
	public String convert(String value);
	public String defaultValue();
	public boolean isNullAllowed();
}
