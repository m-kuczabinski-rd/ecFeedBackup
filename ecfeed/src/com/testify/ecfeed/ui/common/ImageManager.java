package com.testify.ecfeed.ui.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageManager {

	public static final String ICONS_FOLDER_NAME = "icons";

	private static ImageManager fInstance;
	private Map<String, ImageDescriptor> fDescriptors;
	private Map<String, Image> fImages;

	public static ImageManager getInstance(){
		if(fInstance == null){
			fInstance = new ImageManager();
		}
		return fInstance;
	}

	public ImageDescriptor getImageDescriptor(String fileName){
		String path = Constants.ICONS_FOLDER_NAME + "/" + fileName;
		if(fDescriptors.containsKey(path) == false){
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			URL url = FileLocator.find(bundle, new Path(path), null);
			fDescriptors.put(path, ImageDescriptor.createFromURL(url));
		}
		return fDescriptors.get(path);
	}

	public Image getImage(String fileName){
		Image image = fImages.get(fileName);
		if(image == null){
			fImages.put(fileName, getImageDescriptor(fileName).createImage());
		}
		return image;
	}

	private ImageManager(){
		fDescriptors = new HashMap<>();
		fImages = new HashMap<>();
	}
}
