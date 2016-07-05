/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.ecfeed.core.adapter.java.ChoiceValueParser;
import com.ecfeed.core.adapter.java.Constants;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.runner.RunnerException;

public class AbstractFrameworkMethod extends FrameworkMethod {

	private ChoiceValueParser fValueParser;

	public AbstractFrameworkMethod(Method method, ModelClassLoader loader) {
		super(method);
		fValueParser = new ChoiceValueParser(loader, false);
	}

	protected void invoke(Object target, List<ChoiceNode> args) throws RunnerException, Throwable{
		super.invokeExplosively(target, choiceListToParamArray(args));
	}

	protected Object[] choiceListToParamArray(List<ChoiceNode> args) throws RunnerException {
		List<Object> parameters = new ArrayList<Object>();
		for(ChoiceNode p : args){
			parameters.add(parseChoiceValue(p));
		}
		return parameters.toArray();
	}

	protected Object parseChoiceValue(ChoiceNode choice) throws RunnerException{
		String type = choice.getParameter().getType();
		Object value = fValueParser.parseValue(choice);

		if(JavaUtils.isString(type) || JavaUtils.isUserType(type)){
			//null value acceptable
			if(choice.getValueString().equals(Constants.VALUE_REPRESENTATION_NULL)){
				return null;
			}
		}
		if(value == null){
			RunnerException.report(Messages.CANNOT_PARSE_PARAMETER(type, choice.getValueString()));
		}
		return value;
	}
}
