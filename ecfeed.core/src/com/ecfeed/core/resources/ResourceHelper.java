/*******************************************************************************
 * Copyright (c) 2016 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ecfeed.core.resources;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.ecfeed.core.utils.StreamHelper;

public class ResourceHelper {

	public static String readTextFromResource(
			@SuppressWarnings("rawtypes") Class theClass, 
			String templateFilePath) throws IOException {

		Bundle bundle = FrameworkUtil.getBundle(theClass);
		URL url = FileLocator.find(bundle, new Path(templateFilePath), null);
		return StreamHelper.streamToString(url.openStream());
	}	

}
