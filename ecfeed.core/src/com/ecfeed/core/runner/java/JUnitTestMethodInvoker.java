/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.runner.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ecfeed.core.runner.ITestMethodInvoker;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.utils.ExceptionHelper;

public class JUnitTestMethodInvoker implements ITestMethodInvoker {

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public void invoke(
			Method testMethod, 
			String className, 
			Object instance,
			Object[] arguments, 
			String argumentsDescription) throws RuntimeException {
		try {
			testMethod.invoke(instance, arguments);
		} catch (InvocationTargetException e) {
			String message = testMethod.getName() + " " + argumentsDescription + ": " + e.getTargetException().toString();
			ExceptionHelper.reportRuntimeException(message);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			String message = Messages.CANNOT_INVOKE_TEST_METHOD(testMethod.getName(), argumentsDescription, e.getMessage());
			ExceptionHelper.reportRuntimeException(message);			
		}
	}
}
