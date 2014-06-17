/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/
package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.CategoryNodeAbstractOperations;

public class MenuCutOperation extends MenuOperation{
	private NodeClipboard fSource;
	private IGenericNode fTarget;
	private ModelMasterSection fModel;

	@Override
	public boolean isEnabled(){
		if(fTarget == null || (fTarget instanceof RootNode) || (fTarget instanceof CategoryNode && ((CategoryNode)fTarget).isExpected()))
			return false;
		return true;
	}

	@Override
	public void execute(){
		if(fTarget != null){
			fSource.setClipboardNode(fTarget);
			cut();
			fModel.markDirty();
			fModel.refresh();
		}
	}
	
	public MenuCutOperation(IGenericNode target, NodeClipboard source, ModelMasterSection model){
		super("Cut");
		fTarget = target;
		fSource = source;
		fModel = model;
	}

	public void cut(){
		if(fTarget instanceof PartitionNode){
			PartitionNode target = (PartitionNode)fTarget;
			if(target.getParent() != null){
				target.getParent().removePartition(target);
			}
		} else if(fTarget instanceof CategoryNode){
			CategoryNode target = (CategoryNode)fTarget;
			MethodNode method = target.getMethod();
			if(target.getMethod() != null){
				CategoryNodeAbstractOperations.removeCategory(target, method);
			}
		} else if(fTarget instanceof MethodNode){
			MethodNode target = (MethodNode)fTarget;
			if(target.getClassNode() != null){
				target.getClassNode().removeMethod(target);
			}
		} else if(fTarget instanceof ConstraintNode){
			ConstraintNode target = (ConstraintNode)fTarget;
			if(target.getMethod() != null){
				target.getMethod().removeConstraint(target);
			}
		} else if(fTarget instanceof TestCaseNode){
			TestCaseNode target = (TestCaseNode)fTarget;
			if(target.getMethod() != null){
				target.getMethod().removeTestCase(target);
			}
		} else if(fTarget instanceof ClassNode){
			ClassNode target = (ClassNode)fTarget;
			if(target.getRoot() != null){
				target.getRoot().removeClass(target);
			}
		}
	}

}
