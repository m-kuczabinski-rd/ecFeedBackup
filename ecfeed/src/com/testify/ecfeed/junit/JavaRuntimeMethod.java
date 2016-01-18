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

import com.testify.ecfeed.core.adapter.java.ModelClassLoader;
import com.testify.ecfeed.core.generators.api.GeneratorException;
import com.testify.ecfeed.core.generators.api.IGenerator;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.runner.Messages;
import com.testify.ecfeed.core.runner.RunnerException;
import com.testify.ecfeed.core.utils.EcException;

public class JavaRuntimeMethod extends AbstractFrameworkMethod{

	IGenerator<ChoiceNode> fGenerator;

	public JavaRuntimeMethod(Method method, IGenerator<ChoiceNode> initializedGenerator, ModelClassLoader loader) throws RunnerException{
		super(method, loader);
		fGenerator = initializedGenerator;
	}

	@Override
	public Object invokeExplosively(Object target, Object... p) throws Throwable{
		List<ChoiceNode> next = new ArrayList<>();
		try {
			while((next = fGenerator.next()) !=null){
				super.invoke(target, next);
			}
		} catch (GeneratorException e) {
			RunnerException.report(Messages.RUNNER_EXCEPTION(e.getMessage()));
		} catch (Throwable e){
			String message = getName() + "(" + next.toString() + "): " + e.getMessage();
			EcException.report(message, e);
		}
		return null;
	}
}
