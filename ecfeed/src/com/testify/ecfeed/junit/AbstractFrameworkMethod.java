/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.adapter.java.ChoiceValueParser;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;

public class AbstractFrameworkMethod extends FrameworkMethod {

	private ChoiceValueParser fValueParser;

	public AbstractFrameworkMethod(Method method, ModelClassLoader loader) {
		super(method);
		fValueParser = new ChoiceValueParser(loader);
	}
	
	protected void invoke(Object target, List<ChoiceNode> args) throws RunnerException, Throwable{
		List<Object> parameters = new ArrayList<Object>();
		for(ChoiceNode p : args){
			parameters.add(parseChoiceValue(p));
		}
		super.invokeExplosively(target, parameters.toArray());
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
			throw new RunnerException(Messages.CANNOT_PARSE_PARAMETER(type, choice.getValueString()));
		}
		return value;
	}
}
