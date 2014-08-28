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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.utils.Constants;
import com.testify.ecfeed.utils.ModelUtils;

public class ModelContentProvider extends TreeNodeContentProvider implements ITreeContentProvider {

	public static final Object[] EMPTY_ARRAY = new Object[]{};
	
	public interface IModelWrapper{
		public RootNode getModel();
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof IModelWrapper){
			RootNode root = ((IModelWrapper)inputElement).getModel(); 
			return new Object[]{root};
		}
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		//Because of potentially large amount of children, MethodNode is special case
		//We filter out test suites with too many test cases
		if(parentElement instanceof MethodNode){
			MethodNode method = (MethodNode)parentElement;
			ArrayList<Object> children = new ArrayList<Object>();
			children.addAll(method.getCategories());
			children.addAll(method.getConstraintNodes());
			for(String testSuite : method.getTestSuites()){
				Collection<TestCaseNode> testCases = method.getTestCases(testSuite);
				if(testCases.size() <= Constants.MAX_DISPLAYED_TEST_CASES_PER_SUITE){
					children.addAll(testCases);
				}
			}
			return children.toArray();
		}
		else if(parentElement instanceof CategoryNode){
			CategoryNode category = (CategoryNode)parentElement;
			if(category.isExpected() && ModelUtils.isTypePrimitive(category.getType())){
				return EMPTY_ARRAY;
			}
		}
		
		if(parentElement instanceof GenericNode){
			GenericNode node = (GenericNode)parentElement;
			if(node.getChildren().size() < Constants.MAX_DISPLAYED_CHILDREN_PER_NODE){
				return node.getChildren().toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof GenericNode){
			return ((GenericNode)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}
