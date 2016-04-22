package com.testify.ecfeed.core.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ResourceHelper {

	public static String readTextFromResource(
			@SuppressWarnings("rawtypes") Class theClass, 
			String templateFilePath) throws IOException {

		Bundle bundle = FrameworkUtil.getBundle(theClass);
		URL url = FileLocator.find(bundle, new Path(templateFilePath), null);

		String templateText = "";
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(url.openStream()));

		try {
			String inputLine;
			while ((inputLine = inputReader.readLine()) != null){
				templateText += inputLine + "\n";
			}
		} finally {
			inputReader.close();
		}

		return templateText;
	}	

}
