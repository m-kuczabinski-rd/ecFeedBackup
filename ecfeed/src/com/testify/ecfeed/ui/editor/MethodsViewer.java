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
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaClassUtils;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class MethodsViewer extends CheckboxTableViewerSection {

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private ColorManager fColorManager;
	private TableViewerColumn fMethodsColumn;
	private ClassInterface fClassIf;
	private MethodInterface fMethodIf;
	
	private class RemoveSelectedMethodsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fClassIf.removeMethods(getSelectedMethods(), MethodsViewer.this, getUpdateListener());
		}
	}
	
	private class AddNewMethodAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			MethodNode newMethod = fClassIf.addNewMethod(MethodsViewer.this, getUpdateListener());
			if(newMethod != null){
				selectElement(newMethod);
				fMethodsColumn.getViewer().editElement(newMethod, 0);
			}
		}
	}

	private class MethodsNameLabelProvider extends ColumnLabelProvider{
		
		public MethodsNameLabelProvider() {
			fColorManager = new ColorManager();
		}
		
		@Override
		public String getText(Object element){
			MethodNode method = (MethodNode)element;
			String result = method.getName();
			return result;
		}

		@Override
		public Color getForeground(Object element){
			fMethodIf.setTarget((MethodNode)element);
			switch(fMethodIf.implementationStatus()){
			case IMPLEMENTED: return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			default: return null;
			}
		}
	}
	
	private class MethodsArgsLabelProvider extends MethodsNameLabelProvider{
		public MethodsArgsLabelProvider() {
			super();
		}
		
		@Override
		public String getText(Object element){
			List<String> argTypes = fMethodIf.getArgTypes((MethodNode)element);
			List<String> argNames = fMethodIf.getArgNames((MethodNode)element);
			String result = "";
			for(int i = 0; i < argTypes.size(); i++){
				result += JavaClassUtils.getLocalName(argTypes.get(i)) + " " + argNames.get(i);
				if(i < argTypes.size() - 1){
					result += ", ";
				}
			}
			return result;
		}
	}
	
	public MethodsViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		
		fClassIf = new ClassInterface(operationManager);
		fMethodIf = new MethodInterface(operationManager);

		fMethodsColumn.setEditingSupport(new MethodNameEditingSupport(this, operationManager));

		setText("Methods");
		addButton("Add new method", new AddNewMethodAdapter());
		addButton("Remove selected", new RemoveSelectedMethodsAdapter());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	public void setInput(ClassNode classNode){
		fClassIf.setTarget(classNode);
		super.setInput(classNode.getMethods());
	}

	@Override
	protected void createTableColumns() {
		fMethodsColumn = addColumn("Methods", 150, new MethodsNameLabelProvider());
		addColumn("Arguments", 450, new MethodsArgsLabelProvider());
	}
	
	@Override
	protected boolean tableLinesVisible() {
		return true;
	}

	@Override
	protected boolean tableHeaderVisible() {
		return true;
	}
	
	protected Collection<MethodNode> getSelectedMethods(){
		List<MethodNode> methods = new ArrayList<MethodNode>();
		for(Object o : getCheckedElements()){
			if(o instanceof MethodNode){
				methods.add((MethodNode)o);
			}
		}
		return methods;
	}
}
