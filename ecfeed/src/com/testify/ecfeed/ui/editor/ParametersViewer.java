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

import static com.testify.ecfeed.ui.common.CategoryNodeAbstractLayer.addCategory;
import static com.testify.ecfeed.ui.common.CategoryNodeAbstractLayer.removeCategories;

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
			moveSelectedItem(true, 1);
		}
	}

	private class MoveDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(false, 1);
		}
	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
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
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_INT,
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_LONG,
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_SHORT,
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BYTE,
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BOOLEAN,
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_DOUBLE,
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_FLOAT,
						com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING,
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
					String typeCandidate = Constants.DEFAULT_USER_TYPE_NAME;
					while(true){
						List<String> methodTypes = fSelectedMethod.getCategoriesTypes();
						methodTypes.add(typeCandidate);
						if (fSelectedMethod.getClassNode().getMethod(fSelectedMethod.getName(), methodTypes) == null) {
							type = typeCandidate;
							break;
						}
						else{
							typeCandidate = Constants.DEFAULT_USER_TYPE_NAME + i++;
						}
					}
				}
		
				CategoryNode categoryNode = new CategoryNode(name, type, false);
				categoryNode.setDefaultValueString(ModelUtils.getDefaultExpectedValueString(type));
				ArrayList<PartitionNode> defaultPartitions = ModelUtils.generateDefaultPartitions(type);
				for(PartitionNode partition : defaultPartitions){
					categoryNode.addPartition(partition);
				}
				
				addCategory(categoryNode, fSelectedMethod);
				
				modelUpdated();
				selectElement(categoryNode);
				nameColumn.getViewer().editElement(categoryNode, 0);			
			
		}
	}

	private class RemoveParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getCheckedElements().length > 0){
				ArrayList<CategoryNode> categories = new ArrayList<>();
				for (Object element : getCheckedElements()) {
					categories.add((CategoryNode)element);
				}
				if(removeCategories(categories, fSelectedMethod)){
					modelUpdated();
				}
			}
		}
	}

	private boolean moveSelectedItem(boolean moveUp, int shift){
		if(getSelectedElement() != null && shift > 0){
			CategoryNode categoryNode = (CategoryNode)getSelectedElement();
			int index = fSelectedMethod.getCategories().indexOf(categoryNode);
			if(moveUp){
				if(index - shift < 0)
					return false;
			} else{
				if(index + shift >= fSelectedMethod.getCategories().size())
					return false;
			}

			ArrayList<String> tmpTypes = fSelectedMethod.getCategoriesTypes();	
			int currentindex = index;
			for(int i = 0; i < shift; i++){
				Collections.swap(tmpTypes, currentindex, currentindex = moveUp ? currentindex-1 : currentindex+1);
			}

			MethodNode twinMethod = fSelectedMethod.getClassNode().getMethod(fSelectedMethod.getName(), tmpTypes);
			if(twinMethod == null || twinMethod == fSelectedMethod){
				for(int i = 0; i < shift; i++){
					categoryNode.getParent().moveChild(categoryNode, moveUp);
				}
				int newindex = fSelectedMethod.getCategories().indexOf(categoryNode);
				for(TestCaseNode tcnode : fSelectedMethod.getTestCases()){
					Collections.swap(tcnode.getTestData(), newindex, index);
				}
				modelUpdated();
				return true;
			} else{
				if(!moveSelectedItem(moveUp, shift + 1))
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_METHOD_EXISTS_TITLE,
							Messages.DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE);
				return true;
			}
		}
		return true;
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
