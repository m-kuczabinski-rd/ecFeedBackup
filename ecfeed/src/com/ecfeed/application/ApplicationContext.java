/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.application;

public class ApplicationContext {

	static boolean fIsStandaloneApplication = false;
	static String fExportFileName;

	public static boolean isStandaloneApplication() {
		return fIsStandaloneApplication;
	}

	public static void setStandaloneApplication() {
		fIsStandaloneApplication = true;
	}

	public static void setExportTargetFile(String exportFileName) {
		fExportFileName = exportFileName;
	}

	public static String getExportTargetFile() {
		return fExportFileName;
	}	

}
