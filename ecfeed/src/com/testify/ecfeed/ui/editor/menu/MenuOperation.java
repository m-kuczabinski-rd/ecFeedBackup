/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.menu;

public abstract class MenuOperation{
	private String fName;

	public abstract Object execute();

	public abstract boolean isEnabled();

	public MenuOperation(String name){
		fName = name;
	}

	public String getName(){
		return fName;
	}

}
