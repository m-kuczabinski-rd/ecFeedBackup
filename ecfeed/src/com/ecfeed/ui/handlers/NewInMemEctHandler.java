/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.handlers;

import com.ecfeed.ui.editor.EditorInMemFileHelper;
import com.ecfeed.utils.EclipseHelper;


public class NewInMemEctHandler {

	public static void execute() {
		String tmpFileName = EditorInMemFileHelper.createNewTmpFileName();
		EclipseHelper.openEditorOnFileInMemory(tmpFileName);
	}

}
