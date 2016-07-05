/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.java;

import com.ecfeed.core.model.IPrimitiveTypePredicate;

public class JavaPrimitiveTypePredicate implements IPrimitiveTypePredicate{
	@Override
	public boolean isPrimitive(String type){
		return JavaUtils.isPrimitive(type);
	}
}

