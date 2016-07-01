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

package com.ecfeed.ui.modelif;

import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.operations.FactoryShiftOperation;
import com.ecfeed.core.adapter.operations.GenericMoveOperation;
import com.ecfeed.core.adapter.operations.GenericRemoveNodesOperation;
import com.ecfeed.core.adapter.operations.GenericShiftOperation;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.Messages;

public class SelectionInterface extends OperationExecuter {

	private ITypeAdapterProvider fAdapterProvider;

	public SelectionInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	private List<? extends AbstractNode> fSelected;

	public void setTarget(List<AbstractNode> target){
		fSelected = target;
	}

	public boolean delete(){
		if(fSelected.size() > 0){
			return execute(new GenericRemoveNodesOperation(fSelected, fAdapterProvider, true), Messages.DIALOG_REMOVE_NODES_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean deleteEnabled(){
		if(fSelected.size() == 0) return false;
		AbstractNode root = fSelected.get(0).getRoot();
		for(AbstractNode selected : fSelected){
			if(selected == root) return false;
		}
		return true;
	}

	public boolean move(AbstractNode newParent){
		return move(newParent, -1);
	}

	public boolean move(AbstractNode newParent, int newIndex){
		try{
			IModelOperation operation;
			if(newIndex == -1){
				operation = new GenericMoveOperation(fSelected, newParent, fAdapterProvider);
			}
			else{
				operation = new GenericMoveOperation(fSelected, newParent, fAdapterProvider, newIndex);
			}
			return execute(operation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
		}catch(ModelOperationException e){
			return false;
		}
	}

	public boolean moveUpDown(boolean up) {
		AbstractNode parent = fSelected.get(0).getParent();
		for(AbstractNode node : fSelected){
			if(node.getParent() != parent){
				return false;
			}
		}
		try{
			IModelOperation operation = FactoryShiftOperation.getShiftOperation(fSelected, up);
			executeMoveOperation(operation);
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	public boolean moveUpDownEnabed(boolean up) {

		AbstractNode parent = getCommonParent();
		if(parent != null){
			try {
				GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(fSelected, up);
				return operation.getShift() != 0;
			} catch (Exception e) {SystemLogger.logCatch(e.getMessage());}
		}
		return false;
	}

	public AbstractNode getCommonParent() {
		if(fSelected == null || fSelected.size() == 0) return null;
		AbstractNode parent = fSelected.get(0).getParent();
		for(AbstractNode node : fSelected){
			if(node.getParent() != parent) return null;
		}
		return parent;
	}

	public boolean isSingleType(){
		if(fSelected == null || fSelected.size() == 0) return false;
		Class<?> type = fSelected.get(0).getClass();
		for(AbstractNode node : fSelected){
			if(node.getClass().equals(type) == false) return false;
		}
		return true;

	}

	protected boolean executeMoveOperation(IModelOperation moveOperation) {
		return execute(moveOperation, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);
	}
}
