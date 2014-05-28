/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

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
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.utils.ModelUtils;

public class ModelLabelProvider extends LabelProvider {
	
	public String getText(Object element){
		if(element instanceof GenericNode){
			return element.toString();
		}
		return null;
	}
	
	public Image getImage(Object element){
		if (element instanceof RootNode){
			return getImage("root_node.gif");
		} else if (element instanceof ClassNode){
			return getClassImage((ClassNode)element);
		} else if (element instanceof MethodNode){
			return getMethodImage((MethodNode)element);
		} else if(element instanceof TestCaseNode){
			return getTestCaseImage((TestCaseNode)element);
		} else if (element instanceof CategoryNode){
			return getCategoryImage((CategoryNode)element);
		} else if (element instanceof CategoryNode){
			return getImage("category_node.gif");
		} else if (element instanceof ConstraintNode){
			return getImage("constraint_node.gif");
		} else if (element instanceof PartitionNode){
			return getPartitionImage((PartitionNode)element);
		}
		return getImage("sample.gif");
	}
	
	private static Image getImage(String file) {
	    Bundle bundle = FrameworkUtil.getBundle(ModelLabelProvider.class);
	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
	    ImageDescriptor image = ImageDescriptor.createFromURL(url);
	    return image.createImage();

	  }
	
	private static Image getClassImage(ClassNode node) {
		if (ModelUtils.isClassImplemented(node)) {
			return getImage("class_node.gif");
		} else if (ModelUtils.isClassPartiallyImplemented(node)) {
			// TODO change icon to PARTIALLY implemented
			return getImage("sample.gif");
		} else {
			// TODO change icon to NOT implemented
			return getImage("sample.gif");
		}
	}
	
	private static Image getMethodImage(MethodNode node) {
		if (ModelUtils.isMethodImplemented(node)) {
			return getImage("method_node.gif");	
		} else if (ModelUtils.isMethodPartiallyImplemented(node)) {
			// TODO change icon to PARTIALLY implemented
			return getImage("sample.gif");
		} else {
			// TODO change icon to NOT implemented
			return getImage("sample.gif");
		}
	}
	
	private static Image getTestCaseImage(TestCaseNode node) {
		if (ModelUtils.isTestCaseImplemented(node)) {
			return getImage("test_case_node.gif");
		} else if (ModelUtils.isTestCasePartiallyImplemented(node)) {
			// TODO change icon to PARTIALLY implemented
			return getImage("sample.gif");
		} else {
			// TODO change icon to NOT implemented
			return getImage("sample.gif");
		}
	}
	
	private static Image getCategoryImage(CategoryNode node){
		if(node.isExpected()){
			if(ModelUtils.isCategoryImplemented(node)){
				return getImage("expected_value_category_node.gif");
			} else{
				// TODO change icon to NOT implemented
				return getImage("sample.gif");
			}
		} else{
			if(ModelUtils.isCategoryImplemented(node)){
				return getImage("category_node.gif");
			} else if(ModelUtils.isCategoryPartiallyImplemented(node)){
				// TODO change icon to PARTIALLY implemented
				return getImage("sample.gif");
			} else{
				// TODO change icon to NOT implemented
				return getImage("sample.gif");
			}
		}
	}
	
	private static Image getPartitionImage(PartitionNode node) {
		if(node.isAbstract()){
			return getImage("abstract_partition_node.gif");
		}
		if (ModelUtils.isPartitionImplemented(node)) {
			return getImage("partition_node.gif");
		} else {
			// TODO change icon to NOT implemented
			return getImage("sample.gif");
		}
	}
}
