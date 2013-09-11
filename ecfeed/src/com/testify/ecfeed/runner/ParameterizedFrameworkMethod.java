/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.runners.model.FrameworkMethod;

public class ParameterizedFrameworkMethod extends FrameworkMethod {

	protected ArrayList<Object> fParameters;

	public ParameterizedFrameworkMethod(Method method, ArrayList<Object> parameters) {
		super(method);
		fParameters = parameters;
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{
		return super.invokeExplosively(target, fParameters.toArray());
	}
	
	@Override
	public String toString(){
		String result = getMethod().getName() + "(";
		for(int i = 0; i < fParameters.size(); i++){
			result += fParameters.get(i).toString();
			if(i < fParameters.size() - 1){
				result += ", ";
			}
		}
		result += ")";
		return result;
	}
}
