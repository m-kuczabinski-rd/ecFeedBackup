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

package com.testify.ecfeed.junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;
import com.testify.ecfeed.runner.android.AndroidTestMethodInvoker;

public class AndroidRuntimeMethod extends AbstractFrameworkMethod{

	IGenerator<ChoiceNode> fGenerator;
	private String fTestRunner = null;

	public AndroidRuntimeMethod(
			Method method, 
			IGenerator<ChoiceNode> generator, 
			ModelClassLoader loader,
			String testRunner
			) throws RunnerException{
		super(method, loader);
		fGenerator = generator;
		fTestRunner = testRunner;
	}

	@Override
	public Object invokeExplosively(Object target, Object... notUsed) throws RunnerException{

		List<ChoiceNode> next = new ArrayList<>();

		try {
			while((next = fGenerator.next()) !=null){
				invokeRemotely(target, next);
			}
		} catch (GeneratorException e) {
			throw new RunnerException(Messages.RUNNER_EXCEPTION(e.getMessage()));
		}
		
		return null;
	}

	private void invokeRemotely(Object target, List<ChoiceNode> arguments) throws RunnerException {
		AndroidTestMethodInvoker androidInvoker = new AndroidTestMethodInvoker(fTestRunner);
		androidInvoker.invoke(target.getClass().getName(), getMethod(), choiceListToParamArray(arguments));
	}

}
