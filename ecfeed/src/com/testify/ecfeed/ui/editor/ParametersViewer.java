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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.DefaultValueEditingSupport;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TestDataEditorListener;
import com.testify.ecfeed.utils.Constants;
import com.testify.ecfeed.utils.ModelUtils;

public class ParametersViewer extends CheckboxTableViewerSection implements TestDataEditorListener{

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";
	private ColorManager fColorManager;
	private TableViewerColumn fDefaultValueColumn;
	private MethodNode fSelectedMethod;
	private TableViewerColumn nameColumn;
	
	public ParametersViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		getSection().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fColorManager = new ColorManager();
		getSection().setText("Parameters");
		addButton("New parameter", new AddNewParameterAdapter());
		addButton("Remove selected", new RemoveParameterAdapter());
		addButton("Move Up", new MoveUpAdapter());
		addButton("Move Down", new MoveDownAdapter());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	private class MoveUpAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(true);
		}
	}

	private class MoveDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(false);
		}
	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(fSelectedMethod.getTestCases().isEmpty() || MessageDialog.openConfirm(getActiveShell(),
					Messages.DIALOG_DATA_MIGHT_BE_LOST_TITLE, Messages.DIALOG_DATA_MIGHT_BE_LOST_MESSAGE)){
				String type = null;
				String name = Constants.DEFAULT_NEW_CATEGORY_NAME;
				int i = 1;
				while(true){
					if(fSelectedMethod.getCategory(name) == null){
						break;
					}
					name = Constants.DEFAULT_NEW_CATEGORY_NAME + i;
					++i;
				}
					
				List<String> types = Arrays.asList(new String[]{
						com.testify.ecfeed.model.Constants.TYPE_NAME_INT,
						com.testify.ecfeed.model.Constants.TYPE_NAME_LONG,
						com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT,
						com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE,
						com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN,
						com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE,
						com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT,
						com.testify.ecfeed.model.Constants.TYPE_NAME_STRING,
						"user.type"
				});
				for(String typeCandidate : types){
					List<String> methodTypes = fSelectedMethod.getCategoriesTypes();
					methodTypes.add(typeCandidate);
					if (fSelectedMethod.getClassNode().getMethod(fSelectedMethod.getName(), methodTypes) == null) {
						type = typeCandidate;
						break;
					}
				}
	
				if(type == null){
					i = 1;
					while(true){
						List<String> methodTypes = fSelectedMethod.getCategoriesTypes();
						String typeCandidate = "user.type" + i;
						methodTypes.add(typeCandidate);
						if (fSelectedMethod.getClassNode().getMethod(fSelectedMethod.getName(), methodTypes) == null) {
							type = typeCandidate;
							break;
						}
						else{
							++i;
						}
					}

				}
		
				CategoryNode categoryNode = new CategoryNode(name, type, false);
				categoryNode.setDefaultValueString(ModelUtils.getDefaultExpectedValueString(type));
				ArrayList<PartitionNode> defaultPartitions = ModelUtils.generateDefaultPartitions(type);
				for(PartitionNode partition : defaultPartitions){
					categoryNode.addPartition(partition);
				}

				fSelectedMethod.addCategory(categoryNode);
				fSelectedMethod.clearTestCases();
				modelUpdated();
				selectElement(categoryNode);
				nameColumn.getViewer().editElement(categoryNode, 0);			
			}
		}
	}

	private class RemoveParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getCheckedElements().length > 0){
				ArrayList<String> tmpTypes = fSelectedMethod.getCategoriesTypes();
				for (Object element : getCheckedElements()) {
					CategoryNode node = (CategoryNode)element;
					for (int i = 0; i < fSelectedMethod.getCategories().size(); ++i) {
						CategoryNode type = fSelectedMethod.getCategories().get(i);
						if (type.getName().equals(node.getName()) && type.getType().equals(node.getType())) {
							tmpTypes.remove(node.getType());
						}
					}
				}
				if (fSelectedMethod.getClassNode().getMethod(fSelectedMethod.getName(), tmpTypes) == null) {
					// checking if there is any reason to display warning  - test cases and constraints
					boolean warn  = false;
					if(fSelectedMethod.getTestCases().isEmpty()){
						for(ConstraintNode constraint: fSelectedMethod.getConstraintNodes()){
							for(Object element: getCheckedElements()){
								if(constraint.mentions((CategoryNode)element)){
									warn  = true;
									break;
								}
							}
							if(warn == true)
								break;
						}
					} else{
						warn =  true;
					}
					if(warn){
						if (MessageDialog.openConfirm(getActiveShell(),
								Messages.DIALOG_REMOVE_PARAMETERS_TITLE,
								Messages.DIALOG_REMOVE_PARAMETERS_MESSAGE)) {
							removeParameters(getCheckedElements());
						}
					} else {
						removeParameters(getCheckedElements());
					}
				} else {
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_METHOD_EXISTS_TITLE,
							Messages.DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE);
				}
			}
		}

		private void removeParameters(Object[] checkedElements) {
			for(Object element : checkedElements){
				if (element instanceof CategoryNode){
					if(fSelectedMethod.removeCategory((CategoryNode)element)){
						fSelectedMethod.clearTestCases();
					};
					
				}
			}
			modelUpdated();
		}
	}

	private void moveSelectedItem(boolean moveUp) {
		if (getSelectedElement() != null) {
			CategoryNode categoryNode = (CategoryNode)getSelectedElement();
			ArrayList<String> tmpTypes = fSelectedMethod.getCategoriesTypes();
			boolean move = false;
			for (int i = 0; i < fSelectedMethod.getCategories().size(); ++i) {
				CategoryNode type = fSelectedMethod.getCategories().get(i);
				if (type.getName().equals(categoryNode.getName()) && type.getType().equals(categoryNode.getType())) {
					if (moveUp && (i > 0)) {
						String prevValue = tmpTypes.get(i - 1);
						tmpTypes.set(i - 1, categoryNode.getType());
						tmpTypes.set(i, prevValue);
						move = true;
					} else if (!moveUp && (i < tmpTypes.size() - 1)){
						String nextValue = tmpTypes.get(i + 1);
						tmpTypes.set(i + 1, categoryNode.getType());
						tmpTypes.set(i, nextValue);
						move = true;
					}
				}
			}

			if (move) {
				if ((fSelectedMethod.getClassNode().getMethod(fSelectedMethod.getName(), tmpTypes) == null)) {
			if(categoryNode.getParent().moveChild(categoryNode, moveUp)){
				int index = fSelectedMethod.getCategories().indexOf(categoryNode);
				int oldindex = moveUp ? (index + 1) : (index - 1);
				for(TestCaseNode tcnode: fSelectedMethod.getTestCases()){
					Collections.swap(tcnode.getTestData(), index, oldindex);
				}
				modelUpdated();
			}
				} else {
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_METHOD_EXISTS_TITLE,
							Messages.DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE);
				}
			}
		}
	}

	@Override
	protected void createTableColumns() {
		nameColumn = addColumn("Name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				String result = new String();
				if(element instanceof CategoryNode && ((CategoryNode)element).isExpected()){
					result += "[e]";
				}
				result += ((CategoryNode)element).getName();
				return result;
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		nameColumn.setEditingSupport(new CategoryNameEditingSupport(this));
		
		TableViewerColumn typeColumn = addColumn("Type", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		typeColumn.setEditingSupport(new CategoryTypeEditingSupport(this));
		
		TableViewerColumn expectedColumn = addColumn("Expected", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				CategoryNode node = (CategoryNode)element;
				return (node.isExpected() ? "Yes" : "No");
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		expectedColumn.setEditingSupport(new ExpectedValueEditingSupport(this));

		fDefaultValueColumn = addColumn("Default value", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof CategoryNode && ((CategoryNode)element).isExpected()){
					CategoryNode category = (CategoryNode)element;
					return category.getDefaultValuePartition().getValueString();
				}
				return EMPTY_STRING ;
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		fDefaultValueColumn.setEditingSupport(new DefaultValueEditingSupport(getTableViewer(), this));
	}
		
	public void setInput(MethodNode method){
		fSelectedMethod = method;
		showDefaultValueColumn(fSelectedMethod.getCategoriesNames(true).size() == 0);
		super.setInput(method.getCategories());
	}

	private Color getColor(Object element){
		if(element instanceof CategoryNode && ((CategoryNode)element).isExpected()){
			return fColorManager.getColor(ColorConstants.EXPECTED_VALUE_CATEGORY);
		}
		return null;
	}

	private void showDefaultValueColumn(boolean show) {
		if(show){
			fDefaultValueColumn.getColumn().setWidth(0);
			fDefaultValueColumn.getColumn().setResizable(false);
		}
		else{
			fDefaultValueColumn.getColumn().setWidth(150);
			fDefaultValueColumn.getColumn().setResizable(true);
		}
	}

	@Override
	public void testDataChanged() {
		modelUpdated();
	}

}
