/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.model;

import com.testify.ecfeed.utils.ExceptionHelper;

public class ModelConverter {

	private static final String INVALID_MODEL_VERSION = "Invalid model version.";

	public static RootNode convertToCurrentVersion(RootNode model) {
		int currentVersion = ModelVersionDistributor.getCurrentVersion();
		int modelVersion = model.getModelVersion();

		for (int version = modelVersion; version < currentVersion; version++) {
			model = convertFromVersion(model, version);
		}
		return model;
	}

	private static RootNode convertFromVersion(RootNode model, int version) {
		switch (version) {
		case 0:
			model = convertFrom0To1(model);
			break;
		default:
			ExceptionHelper.reportRuntimeException(INVALID_MODEL_VERSION);
			break;
		}

		model.setVersion(version+1);
		return model;
	}

	private static RootNode convertFrom0To1(RootNode model) {
		return model; // no changes in model internal structure, just serialization and parsing differs  
	}
}
