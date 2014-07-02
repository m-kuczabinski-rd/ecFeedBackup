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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
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
		Image img = new Image(Display.getCurrent(), image.getBounds().width, image.getBounds().height);
		GC gc = new GC(img);
		gc.drawImage(image, 0, 0);	
		List<Image> decorations = null;
    	if (element instanceof ClassNode) {
    		decorations = getClassImageDecoration((ClassNode)element);
    	} else if (element instanceof MethodNode) {
    		decorations = getMethodImageDecoration((MethodNode)element);
    	} else if (element instanceof TestCaseNode) {
    		decorations = getTestCaseImageDecoration((TestCaseNode)element);
    	} else if (element instanceof CategoryNode) {
    		decorations = getCategoryImageDecoration((CategoryNode)element);
    	} else if (element instanceof PartitionNode) {
    		decorations = getPartitionImageDecoration((PartitionNode)element);
    	}
    	if (decorations != null) {
    		for(Image decoration : decorations){
    			gc.drawImage(decoration, 0, 0);
    		}
    	}	
		gc.dispose();
    	return img;
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

	private List<Image> getClassImageDecoration(ClassNode node) {
		List<Image> decorations = new ArrayList<Image>();
		if (ModelUtils.isClassImplemented(node)) {
			decorations.add(getImage("implemented.png"));
		} else if (ModelUtils.isClassPartiallyImplemented(node)) {
			decorations.add(getImage("partially_implemented.png"));
		}
		else{
			decorations.add(getImage("unimplemented.png"));
		}
		return decorations;
	}

	private List<Image> getMethodImageDecoration(MethodNode node) {
		List<Image> decorations = new ArrayList<Image>();
		if (ModelUtils.isMethodImplemented(node)) {
			decorations.add(getImage("implemented.png"));
		} else if (ModelUtils.isMethodPartiallyImplemented(node)) {
			decorations.add(getImage("partially_implemented.png"));
		}
		else{
			decorations.add(getImage("unimplemented.png"));
		}
		return decorations;
	}

	private List<Image> getTestCaseImageDecoration(TestCaseNode node) {
		List<Image> decorations = new ArrayList<Image>();
		if (ModelUtils.isTestCaseImplemented(node)) {
			decorations.add(getImage("implemented.png"));
		} else if (ModelUtils.isTestCasePartiallyImplemented(node)) {
			decorations.add(getImage("partially_implemented.png"));
		}
		else{
			decorations.add(getImage("unimplemented.png"));
		}
		return decorations;
	}

	private List<Image> getCategoryImageDecoration(CategoryNode node) {
		List<Image> decorations = new ArrayList<Image>();
		if (ModelUtils.isCategoryImplemented(node)) {
			decorations.add(getImage("implemented.png"));
		} else if (ModelUtils.isCategoryPartiallyImplemented(node)) {
			decorations.add(getImage("partially_implemented.png"));
		}
		else{
			decorations.add(getImage("unimplemented.png"));
		}
		return decorations;
	}

	private List<Image> getPartitionImageDecoration(PartitionNode node) {
		List<Image> decorations = new ArrayList<>();
		
		if(node.isAbstract()){
			decorations.add(getImage("abstract.png"));
		}
		
		if (ModelUtils.isPartitionImplemented(node)) {
			decorations.add(getImage("implemented.png"));
		}
		else if(ModelUtils.isPartitionPartiallyImplemented(node)){
			decorations.add(getImage("partially_implemented.png"));
		}
		else{
			decorations.add(getImage("unimplemented.png"));
		}
		
		return decorations;
	}
}
