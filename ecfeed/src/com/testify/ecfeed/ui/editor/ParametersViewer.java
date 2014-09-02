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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.common.DefaultValueEditingSupport;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class ParametersViewer extends CheckboxTableViewerSection{

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";
	private TableViewerColumn fDefaultValueColumn;
	private MethodNode fSelectedMethod;
	private TableViewerColumn fNameColumn;
	private CategoryInterface fCategoryIf;
	private MethodInterface fMethodIf;
	private TableViewerColumn fTypeColumn;
	private TableViewerColumn fExpectedColumn;
	private Button fMoveUpButton;
	
	private class MoveUpDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			boolean up = (e.getSource() == fMoveUpButton);
			fCategoryIf.setTarget((CategoryNode)getSelectedElement());
			fCategoryIf.moveUpDown(up, ParametersViewer.this, getUpdateListener());
		}
	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			CategoryNode addedParameter = fMethodIf.addNewParameter(ParametersViewer.this, getUpdateListener());
			if(addedParameter != null){
				selectElement(addedParameter);
				fNameColumn.getViewer().editElement(addedParameter, 0);			
			}
		}
	}

	private class RemoveParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getCheckedElements().length > 0){
				fMethodIf.removeParameters(getCheckedParameters(), ParametersViewer.this, getUpdateListener());
			}
		}
	}

	public ParametersViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		ModelOperationManager operationManager = parent.getOperationManager();
		fCategoryIf = new CategoryInterface(operationManager);
		fMethodIf = new MethodInterface(operationManager);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		getSection().setText("Parameters");
		addButton("New parameter", new AddNewParameterAdapter());
		addButton("Remove selected", new RemoveParameterAdapter());
		MoveUpDownAdapter adapter = new MoveUpDownAdapter();
		fMoveUpButton = addButton("Move Up", adapter);
		addButton("Move Down", adapter);

		fNameColumn.setEditingSupport(new CategoryNameEditingSupport(this, operationManager));
		fTypeColumn.setEditingSupport(new CategoryTypeEditingSupport(this, operationManager));
		fExpectedColumn.setEditingSupport(new ExpectedValueEditingSupport(this, operationManager));
		fDefaultValueColumn.setEditingSupport(new DefaultValueEditingSupport(this, operationManager));

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getName();
			}
		});
		
		fTypeColumn = addColumn("Type", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
			}
		});
		
		fExpectedColumn = addColumn("Expected", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				CategoryNode node = (CategoryNode)element;
				return (node.isExpected() ? "Yes" : "No");
			}
		});

		fDefaultValueColumn = addColumn("Default value", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof CategoryNode && ((CategoryNode)element).isExpected()){
					CategoryNode category = (CategoryNode)element;
					return category.getDefaultValue();
				}
				return EMPTY_STRING ;
			}
		});
	}
		
	public void setInput(MethodNode method){
		fMethodIf.setTarget(method);
		fSelectedMethod = method;
		showDefaultValueColumn(fSelectedMethod.getCategoriesNames(true).size() == 0);
		super.setInput(method.getCategories());
	}

	private Collection<CategoryNode> getCheckedParameters(){
		Collection<CategoryNode> categories = new ArrayList<>();
		for (Object element : getCheckedElements()) {
			categories.add((CategoryNode)element);
		}
		return categories;
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
}
