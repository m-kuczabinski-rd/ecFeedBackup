/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization;

import com.ecfeed.core.utils.SystemLogger;

public class ParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5386419314543963856L;

	private ParserException(String message) {
		super(message);
	}
	
	public static void report(String message) throws ParserException {
		SystemLogger.logThrow(message);
		throw new ParserException(message);
	}
}
