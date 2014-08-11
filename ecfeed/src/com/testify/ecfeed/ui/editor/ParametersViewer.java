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

import static com.testify.ecfeed.ui.common.CategoryNodeAbstractLayer.removeCategories;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.DefaultValueEditingSupport;
import com.testify.ecfeed.ui.common.TestDataEditorListener;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class ParametersViewer extends CheckboxTableViewerSection implements TestDataEditorListener{

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";
	private ColorManager fColorManager;
	private TableViewerColumn fDefaultValueColumn;
	private MethodNode fSelectedMethod;
	private TableViewerColumn fNameColumn;
	private CategoryInterface fCategoryIf;
	private MethodInterface fMethodIf;
	private ModelOperationManager fOperationManager;
	private TableViewerColumn fTypeColumn;
	private TableViewerColumn fExpectedColumn;
	
	public ParametersViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		fCategoryIf = new CategoryInterface(operationManager);
		fMethodIf = new MethodInterface(operationManager);
		fOperationManager = operationManager;

		getSection().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fColorManager = new ColorManager();
		getSection().setText("Parameters");
		addButton("New parameter", new AddNewParameterAdapter());
		addButton("Remove selected", new RemoveParameterAdapter());
		addButton("Move Up", new MoveUpAdapter());
		addButton("Move Down", new MoveDownAdapter());

		fNameColumn.setEditingSupport(new CategoryNameEditingSupport(this, fOperationManager));
		fTypeColumn.setEditingSupport(new CategoryTypeEditingSupport(this, fOperationManager));
		fExpectedColumn.setEditingSupport(new ExpectedValueEditingSupport(this, fOperationManager));

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	private class MoveUpAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fCategoryIf.setTarget((CategoryNode)getSelectedElement());
			fCategoryIf.moveUp(ParametersViewer.this, getUpdateListener());
		}
	}

	private class MoveDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fCategoryIf.setTarget((CategoryNode)getSelectedElement());
			fCategoryIf.moveDown(ParametersViewer.this, getUpdateListener());
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

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new ColumnLabelProvider(){
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
		
		fTypeColumn = addColumn("Type", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		
		fExpectedColumn = addColumn("Expected", 150, new ColumnLabelProvider(){
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
		fMethodIf.setTarget(method);
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
