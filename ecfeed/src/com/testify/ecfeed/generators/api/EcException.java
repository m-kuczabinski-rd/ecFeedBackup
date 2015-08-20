/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.generators.api;

import com.testify.ecfeed.utils.SystemLogger;

public class EcException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2866617803127770757L;

	private EcException(String message) {
		super(message);
	}
	
	private EcException(String message, Throwable chainedThrowable) {
		super(message, chainedThrowable);
	}	
	
	public static void report(String message) throws EcException {
		SystemLogger.logThrow(message);
		throw new EcException(message);
	}	
	
	public static void report(String message, Throwable throwable) throws EcException {
		SystemLogger.logThrow(message);
		throw new EcException(message, throwable);
	}	
}
