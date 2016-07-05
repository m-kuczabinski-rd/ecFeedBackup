/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

public class ItemHolder<Type> {
	private Type fItem;

	public ItemHolder() {
		set(null);
	}

	public Type get() {
		return fItem;
	}

	public void set(Type str) {
		fItem = str;
	}

	public boolean isNull() {
		if (fItem == null) {
			return true;
		}
		return false;
	}
}
