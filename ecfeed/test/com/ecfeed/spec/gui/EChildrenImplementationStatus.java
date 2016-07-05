/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.spec.gui;

public enum EChildrenImplementationStatus {
	NO_CHILDREN,
	ALL_NOT_IMPLEMENTED,
	SOME_PARTLY_IMPLEMENTED_REST_NOT_IMPLEMENTED,
	ALL_PARTLY_IMPLEMENTED,
	SOME_IMPLEMENTED_REST_NOT_IMPLEMENTED,
	SOME_IMPLEMENTED_REST_PARTLY_IMPLEMENTED,
	ALL_IMPLEMENTED,
	FULL_MIX
}
