/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.modeleditor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class ModelLabelProvider extends LabelProvider {

	public String getText(Object element){
		if(element instanceof GenericNode){
			return element.toString();
		}
		return null;
	}
	
	public Image getImage(Object element){
		if(element instanceof RootNode){
			return getImage("root_node.gif");
		}
		if(element instanceof ClassNode){
			return getImage("class_node.gif");
		}
		if(element instanceof MethodNode){
			return getImage("method_node.gif");
		}
		if(element instanceof TestCaseNode){
			return getImage("test_case_node.gif");
		}
		if(element instanceof ExpectedValueCategoryNode){
			return getImage("expected_value_category_node.gif");
		}
		if(element instanceof CategoryNode){
			return getImage("category_node.gif");
		}
		if(element instanceof ConstraintNode){
			return getImage("constraint_node.gif");
		}
		if(element instanceof PartitionNode){
			if(((PartitionNode)element).isAbstract()){
				return getImage("abstract_partition_node.gif");
			}
			return getImage("partition_node.gif");
		}
		return getImage("sample.gif");
	}
	
	private static Image getImage(String file) {
	    Bundle bundle = FrameworkUtil.getBundle(ModelLabelProvider.class);
	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
	    ImageDescriptor image = ImageDescriptor.createFromURL(url);
	    return image.createImage();

	  }

}
