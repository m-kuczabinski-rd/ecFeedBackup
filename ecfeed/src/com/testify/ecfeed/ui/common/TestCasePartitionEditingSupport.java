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

package com.testify.ecfeed.ui.common;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class TestCasePartitionEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private ArrayList<PartitionNode> fTestData;
	private ComboBoxViewerCellEditor fCellEditor;
	private IInputChangedListener fSetValueListener;

	public TestCasePartitionEditingSupport(TableViewer viewer, ArrayList<PartitionNode> testData, IInputChangedListener setValueListener) {
		super(viewer);
		fViewer = viewer;
		fTestData = testData;
		fSetValueListener = setValueListener;
		fCellEditor = new ComboBoxViewerCellEditor(fViewer.getTable(), SWT.TRAIL);
		fCellEditor.setLabelProvider(new LabelProvider());
		fCellEditor.setContentProvider(new ArrayContentProvider());
		fCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION | 
				ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		PartitionNode partition = (PartitionNode)element;
		CategoryNode parent = (CategoryNode)partition.getParent();
		fCellEditor.setInput(parent.getPartitions());
		fCellEditor.setValue(partition.getName());
		return fCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		if(element instanceof PartitionNode){
			PartitionNode partition = (PartitionNode) element;
			CategoryNode parent = partition.getCategory();
			boolean canEdit = !(parent instanceof ExpectedValueCategoryNode); 
			return canEdit;
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return ((PartitionNode)element).toString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		CategoryNode parent = (CategoryNode)((PartitionNode)element).getParent();
		MethodNode method = (MethodNode)parent.getParent();
		int parentIndex = method.getCategories().indexOf(parent);
		if(parentIndex >= 0 && parentIndex <= fTestData.size()){
			fTestData.set(parentIndex, (PartitionNode)value);
		}
		fSetValueListener.inputChanged();
	}
}
