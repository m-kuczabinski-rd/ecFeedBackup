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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.SystemHelper;
import com.ecfeed.utils.EctFileHelper;

public class EditorInMemFileHelper {

	private static final String TMP_FILE_MEM_DIR =
			DiskFileHelper.joinSubdirectory(SystemHelper.getSystemTemporaryDir(), "ecFeed") + DiskFileHelper.pathSeparator();

	public static String getFilePrefix() {
		return "Untitled";
	}

	public static String createNewTmpFileName() {
		return TMP_FILE_MEM_DIR + getFilePrefix() + ModelEditorHelper.getNextFreeUntitledNumber() + ".ect";
	}

	public static boolean isInMemFile(String pathWithFileName) {
		String path = DiskFileHelper.extractPathWithSeparator(pathWithFileName);

		if (path.endsWith(TMP_FILE_MEM_DIR)) {
			return true;
		}

		return false;
	}

	public static InputStream getInitialInputStream(String pathWithFileName) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String modelName = DiskFileHelper.extractFileNameWithoutExtension(fileName);
		EctFileHelper.serializeEmptyModel(modelName, outputStream);		
		String fileContent = outputStream.toString();

		return new ByteArrayInputStream(fileContent.getBytes());
	}	

}
