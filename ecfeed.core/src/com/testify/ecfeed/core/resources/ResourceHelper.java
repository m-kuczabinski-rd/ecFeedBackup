package com.testify.ecfeed.core.resources;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.core.utils.StreamHelper;

public class ResourceHelper {

	public static String readTextFromResource(
			@SuppressWarnings("rawtypes") Class theClass, 
			String templateFilePath) throws IOException {

		Bundle bundle = FrameworkUtil.getBundle(theClass);
		URL url = FileLocator.find(bundle, new Path(templateFilePath), null);
		return StreamHelper.streamToString(url.openStream());
	}	

}
