/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Mariusz Strozynski (m.strozynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.utils.ModelUtils;

public class ModelLabelDecorator implements ILabelDecorator {

	@Override
    public Image decorateImage(Image image, Object element) {
		GC gc = new GC(image);
		Image decoration = null;
    	if (element instanceof ClassNode) {
    		decoration = getClassImageDecoration((ClassNode)element);
    	} else if (element instanceof MethodNode) {
    		decoration = getMethodImageDecoration((MethodNode)element);
    	} else if (element instanceof TestCaseNode) {
    		decoration = getTestCaseImageDecoration((TestCaseNode)element);
    	} else if (element instanceof CategoryNode) {
    		decoration = getCategoryImageDecoration((CategoryNode)element);
    	} else if (element instanceof PartitionNode) {
    		decoration = getPartitionImageDecoration((PartitionNode)element);
    	}
    	if (decoration != null) {
    		gc.drawImage(decoration, 0, 0);
    	}
		gc.dispose();
    	return image;
    }

	@Override
    public String decorateText(String text, Object element) {
    	return text;
    }

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	private Image getImage(String file) {
	    Bundle bundle = FrameworkUtil.getBundle(ModelLabelDecorator.class);
	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
	    ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
	    return descriptor.createImage();
	}

	private Image getClassImageDecoration(ClassNode node) {
		if (ModelUtils.isClassImplemented(node)) {
			return getImage("implemented.gif");
		} else if (ModelUtils.isClassPartiallyImplemented(node)) {
			return getImage("partially_implemented.gif");
		}
		return getImage("unimplemented.gif");
	}

	private Image getMethodImageDecoration(MethodNode node) {
		if (ModelUtils.isMethodImplemented(node)) {
			return getImage("implemented.gif");
		} else if (ModelUtils.isMethodPartiallyImplemented(node)) {
			return getImage("partially_implemented.gif");
		}
		return getImage("unimplemented.gif");
	}

	private Image getTestCaseImageDecoration(TestCaseNode node) {
		if (ModelUtils.isTestCaseImplemented(node)) {
			return getImage("implemented.gif");
		} else if (ModelUtils.isTestCasePartiallyImplemented(node)) {
			return getImage("partially_implemented.gif");
		}
		return getImage("unimplemented.gif");
	}

	private Image getCategoryImageDecoration(CategoryNode node) {
		if (ModelUtils.isCategoryImplemented(node)) {
			return getImage("implemented.gif");
		} else if(!node.isExpected() && ModelUtils.isCategoryPartiallyImplemented(node)) {
			return getImage("partially_implemented.gif");
		}
		return getImage("unimplemented.gif");
	}

	private Image getPartitionImageDecoration(PartitionNode node) {
		if (!node.isAbstract() && ModelUtils.isPartitionImplemented(node)) {
			return getImage("implemented.gif");
		}
		return getImage("unimplemented.gif");
	}
}
