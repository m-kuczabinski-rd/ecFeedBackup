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

import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.runner.RunnerException;

public class AndroidRuntimeMethod extends AbstractFrameworkMethod{

	private IGenerator<ChoiceNode> fGenerator;
	private ITestMethodInvoker fMethodInvoker;
	private String fClassName;

	public AndroidRuntimeMethod(
			String className,
			Method method,
			IGenerator<ChoiceNode> generator, 
			ModelClassLoader loader,
			ITestMethodInvoker methodInvoker
			) throws RunnerException{
		super(method, loader);
		fClassName = className;
		fGenerator = generator;
		fMethodInvoker = methodInvoker;
	}

	@Override
	public Object invokeExplosively(Object target, Object... notUsed) throws RunnerException{

		List<ChoiceNode> next = new ArrayList<>();

		try {
			while((next = fGenerator.next()) !=null){
				fMethodInvoker.invoke(
						getMethod(), 
						fClassName,
						target, 
						choiceListToParamArray(next), 
						next.toString());		
			}
		} catch (GeneratorException e) {
			RunnerException.report(Messages.RUNNER_EXCEPTION(e.getMessage()));
		}

		return null;
	}

}
