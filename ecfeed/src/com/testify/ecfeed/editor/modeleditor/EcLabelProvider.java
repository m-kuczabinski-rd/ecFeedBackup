package com.testify.ecfeed.editor.modeleditor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.model.GenericNode;

public class EcLabelProvider extends LabelProvider {

	public String getText(Object element){
		if(element instanceof GenericNode){
			return element.toString();
		}
		return null;
	}
	
	public Image getImage(Object element){
		return getImage("sample.gif");
	}
	
	private static Image getImage(String file) {
	    Bundle bundle = FrameworkUtil.getBundle(EcLabelProvider.class);
	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
	    ImageDescriptor image = ImageDescriptor.createFromURL(url);
	    return image.createImage();

	  }

}
