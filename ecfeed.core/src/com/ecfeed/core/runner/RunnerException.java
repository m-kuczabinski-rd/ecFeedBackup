/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.runner;

import com.ecfeed.core.utils.SystemLogger;

public class RunnerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6854958673134753644L;

	public RunnerException(String message){
		super(message);
	}
	
	public static void report(String message) throws RunnerException {
		SystemLogger.logThrow(message);
		throw new RunnerException(message);
	}	
}
