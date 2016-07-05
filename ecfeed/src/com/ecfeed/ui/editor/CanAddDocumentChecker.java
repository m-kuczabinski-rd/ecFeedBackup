/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.editor;

import com.ecfeed.utils.IChecker;

public class CanAddDocumentChecker implements IChecker { 


	@Override
	public boolean check(String pathWithFileName) {
		if (ModelEditorHelper.isFileAlreadyOpen(pathWithFileName)) {
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage(String pathWithFileName) {
		return "File: " + pathWithFileName + " is already open in editor. Can not save document under this name."; 
	}

}
