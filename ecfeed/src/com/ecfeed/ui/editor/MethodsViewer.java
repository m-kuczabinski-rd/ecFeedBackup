/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.ModelNodesTransfer;

public class MethodsViewer extends TableViewerSection {

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private TableViewerColumn fMethodsColumn;
	private ClassInterface fClassIf;
	private MethodInterface fMethodIf;

	public class MethodNameEditingSupport extends EditingSupport{

		private TextCellEditor fNameCellEditor;

		public MethodNameEditingSupport() {
			super(getTableViewer());
			fNameCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fNameCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			fMethodIf.setTarget((MethodNode)element);
			return fMethodIf.getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String newName = (String)value;
			MethodNode method = (MethodNode)element;
			fMethodIf.setTarget(method);
			fMethodIf.setName(newName);
		}
	}

	private class AddNewMethodAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				MethodNode newMethod = fClassIf.addNewMethod();
				if(newMethod != null){
					selectElement(newMethod);
					fMethodsColumn.getViewer().editElement(newMethod, 0);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add new method", e.getMessage());
			}
		}
	}

	private class MethodsArgsLabelProvider extends NodeViewerColumnLabelProvider{
		@Override
		public String getText(Object element){
			List<String> argTypes = fMethodIf.getArgTypes((MethodNode)element);
			List<String> argNames = fMethodIf.getArgNames((MethodNode)element);
			List<MethodParameterNode> parameters = ((MethodNode)element).getMethodParameters();
			String result = "";
			for(int i = 0; i < argTypes.size(); i++){
				result += (parameters.get(i).isExpected()?"[e]":"") + JavaUtils.getLocalName(argTypes.get(i)) + " " + argNames.get(i);
				if(i < argTypes.size() - 1){
					result += ", ";
				}
			}
			return result;
		}
	}

	public MethodsViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);

		fClassIf = new ClassInterface(this, fileInfoProvider);
		fMethodIf = new MethodInterface(this, fileInfoProvider);

		fMethodsColumn.setEditingSupport(new MethodNameEditingSupport());

		setText("Methods");
		addButton("Add new method", new AddNewMethodAdapter());
		addButton("Remove selected", 
				new ActionSelectionAdapter(
						new DeleteAction(getViewer(), this), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
		setActionProvider(new ModelViewerActionProvider(getTableViewer(), this, fileInfoProvider));
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
	}

	public void setInput(ClassNode classNode){
		fClassIf.setTarget(classNode);
		super.setInput(classNode.getMethods());
	}

	@Override
	protected void createTableColumns() {
		fMethodsColumn = addColumn("Name", 150, new NodeNameColumnLabelProvider());
		addColumn("Parameters", 450, new MethodsArgsLabelProvider());
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
