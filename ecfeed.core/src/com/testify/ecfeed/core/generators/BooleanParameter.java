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
