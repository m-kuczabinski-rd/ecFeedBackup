/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators.api;

public class GeneratorException extends Exception {

	private static final long serialVersionUID = 7963877928833442039L;
	private static final String EC_FEED_ERROR = "ECFEEDERR";

	private GeneratorException(String message) {
		super(message);
	}

	public static void report(String message) throws GeneratorException {
		logThrowGeneratorException(message);
		throw new GeneratorException(message);
	}	

	public static void logThrowGeneratorException(String message) {
		System.out.println( EC_FEED_ERROR + ": Exception thrown");
		System.out.println("\t" +"Message: " + message + "\n");
	}
}
