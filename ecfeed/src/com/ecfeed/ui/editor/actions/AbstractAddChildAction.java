/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractAddChildAction extends ModelModifyingAction{

	protected final static String ADD_CLASS_ACTION_NAME = "Add class";
	protected final static String ADD_METHOD_ACTION_NAME = "Add method";
	protected final static String ADD_METHOD_PARAMETER_ACTION_NAME = "Add parameter";
	protected final static String ADD_GLOBAL_PARAMETER_ACTION_NAME = "Add global parameter";
	protected final static String ADD_TEST_CASE_ACTION_NAME = "Add test case";
	protected final static String ADD_TEST_SUITE_ACTION_NAME = "Generate test suite";
	protected final static String ADD_PARTITION_ACTION_NAME = "Add choice";
	protected final static String ADD_CONSTRAINT_ACTION_NAME = "Add constraint";

	protected final static String ADD_CLASS_ACTION_ID = "addClass";
	protected final static String ADD_METHOD_ACTION_ID = "addMethod";
	protected final static String ADD_GLOBAL_PARAMETER_ACTION_ID = "addGlobalParameter";
	protected final static String ADD_METHOD_PARAMETER_ACTION_ID = "addMethodParameter";
	protected final static String ADD_TEST_CASE_ACTION_ID = "addTestCase";
	protected final static String ADD_TEST_SUITE_ACTION_ID = "addTestCase";
	protected final static String ADD_PARTITION_ACTION_ID = "addChoice";
	protected final static String ADD_CONSTRAINT_ACTION_ID = "addConstraint";

	private StructuredViewer fViewer;

	public AbstractAddChildAction(String id, String name, StructuredViewer viewer, IModelUpdateContext updateContext) {
		super(id, name, viewer, updateContext);
		fViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		return getParentInterface() != null;
	}

	protected void select(AbstractNode node){
		if(fViewer != null && node != null){
			fViewer.setSelection(new StructuredSelection(node));
		}
	}

	protected abstract AbstractNodeInterface getParentInterface();
}
