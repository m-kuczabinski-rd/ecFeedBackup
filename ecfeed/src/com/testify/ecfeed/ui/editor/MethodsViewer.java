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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.ModelUtils;

public class MethodsViewer extends CheckboxTableViewerSection {

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private ColorManager fColorManager;
	private ClassNode fSelectedClass;
	private TableViewerColumn methods;
	
	private class RemoveSelectedMethodsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_METHODS_TITLE, 
					Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
				removeMethods(getCheckboxViewer().getCheckedElements());
			}
		}

		private void removeMethods(Object[] checkedElements) {
			for(Object object : checkedElements){
				if(object instanceof MethodNode){
					fSelectedClass.removeMethod((MethodNode)object);
				}
			}
			modelUpdated();
		}
	}
	
	private class AddNewMethodAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			String startName = "NewMethod";
			String name = startName;
			int i = 1;

			while (true) {
				if (fSelectedClass.getMethod(name, new ArrayList<String>()) == null) {
					break;
				}
				name = startName + i;
				++i;
			}

			MethodNode methodNode = new MethodNode(name);
			fSelectedClass.addMethod(methodNode);
			modelUpdated();
			selectElement(methodNode);
			methods.getViewer().editElement(methodNode, 0);
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
			MethodNode method = (MethodNode)element;
			if (ModelUtils.isMethodImplemented(method)) {
				return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
			return null;
		}
	}
	
	private class MethodsArgsLabelProvider extends MethodsNameLabelProvider{
		public MethodsArgsLabelProvider() {
			super();
		}
		
		@Override
		public String getText(Object element){
			MethodNode method = (MethodNode)element;
			String name = method.toString();
			String result = name.substring(name.indexOf('('), name.length());
			return result;
		}
	}
	
	public MethodsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);

		setText("Methods");
		addButton("Add new method", new AddNewMethodAdapter());
		addButton("Remove selected", new RemoveSelectedMethodsAdapter());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		methods = addColumn("Methods", 150, new MethodsNameLabelProvider());
		methods.setEditingSupport(new MethodNameEditingSupport(this));
		addColumn("Arguments", 450, new MethodsArgsLabelProvider());
	}
	
	public void setInput(ClassNode classNode){
		fSelectedClass = classNode;
		super.setInput(classNode.getMethods());
	}

	@Override
	protected boolean tableLinesVisible() {
		return true;
	}

	@Override
	protected boolean tableHeaderVisible() {
		return true;
	}
	
}
