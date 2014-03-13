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

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StatementArray;

public class StatementViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider {
	public static final Object[] EMPTY_ARRAY = new Object[]{};

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Constraint){
			Constraint constraint = (Constraint)inputElement;
			return new Object[]{constraint.getPremise(), constraint.getConsequence()};
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof BasicStatement){
			return ((BasicStatement)parentElement).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof BasicStatement){
			return ((BasicStatement)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof StatementArray){
			StatementArray statementArray = (StatementArray)element;
			List<BasicStatement> children = statementArray.getChildren();
			return (children.size() > 0);
		}
		return false;
	}

}
