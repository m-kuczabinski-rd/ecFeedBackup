/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.io.FileOutputStream;

import com.testify.ecfeed.core.model.ModelVersionDistributor;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.serialization.IModelSerializer;
import com.testify.ecfeed.core.serialization.ect.EctSerializer;
import com.testify.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.core.utils.StreamHelper;

public class EctFileHelper {

	public static void createNewFile(String pathWithFileName) {
		DiskFileHelper.createNewFile(pathWithFileName);
		FileOutputStream outputStream = StreamHelper.requireCreateOutputStream(pathWithFileName);

		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String modelName = DiskFileHelper.extractFileNameWithoutExtension(fileName);
		serializeEmptyModel(modelName, outputStream);		
	}

	private static void serializeEmptyModel(String modelName, FileOutputStream outputStream) {
		RootNode model = new RootNode(modelName, ModelVersionDistributor.getCurrentVersion());

		IModelSerializer serializer = 
				new EctSerializer(outputStream, ModelVersionDistributor.getCurrentVersion());
		try {
			serializer.serialize(model);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Can not serialize empty model. " + e.getMessage());
		}
	}

}
