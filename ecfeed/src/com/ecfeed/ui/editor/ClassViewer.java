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

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.editor.actions.DeleteAction;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.modelif.ClassInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.ModelNodesTransfer;
import com.ecfeed.ui.modelif.RootInterface;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;

public class ClassViewer extends TableViewerSection {
	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;

	private TableViewerColumn fNameColumn;
	private RootInterface fRootIf;
	private ClassInterface fClassIf;

	private TableViewerColumn fPackageNameColumn;

	private IFileInfoProvider fFileInfoProvider;


	private abstract class ClassNameEditingSupport extends EditingSupport{

		private TextCellEditor fNameCellEditor;

		public ClassNameEditingSupport() {
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

		protected void renameClass(ClassNode target, String qualifiedName){
			classIf().setTarget(target);
			classIf().setQualifiedName(qualifiedName);
		}
	}

	private class LocalNameEditingSupport extends ClassNameEditingSupport {

		@Override
		protected Object getValue(Object element) {
			return ClassInterface.getLocalName((ClassNode)element);
		}

		@Override
		protected void setValue(Object element, Object value) {
			ClassNode target = (ClassNode)element;
			String packageName = ClassInterface.getPackageName(target);
			String localName = (String)value;
			renameClass(target, ClassInterface.getQualifiedName(packageName, localName));
		}

	}

	private class PackageNameEditingSupport extends ClassNameEditingSupport{

		@Override
		protected Object getValue(Object element) {
			return ClassInterface.getPackageName((ClassNode)element);
		}

		@Override
		protected void setValue(Object element, Object value) {
			ClassNode target = (ClassNode)element;
			String localName = ClassInterface.getLocalName(target);
			String packageName = (String)value;
			renameClass(target, ClassInterface.getQualifiedName(packageName, localName));
		}
	}


	private class AddImplementedClassAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				ClassNode addedClass = fRootIf.addImplementedClass();
				if(addedClass != null){
					selectElement(addedClass);
					fNameColumn.getViewer().editElement(addedClass, 0);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not add implemented class.", e.getMessage());
			}
		}
	}

	private class AddNewClassAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent ev) {
			try {
				ClassNode addedClass = fRootIf.addNewClass();
				if(addedClass != null){
					selectElement(addedClass);
					fNameColumn.getViewer().editElement(addedClass, 0);
				}
			} catch (Exception e) {
				ExceptionCatchDialog.open("Can not create new test class.", e.getMessage());
			}
		}
	}

	private class ClassViewerColumnLabelProvider extends ColumnLabelProvider {
		@Override
		public Color getForeground(Object element) {
			if (!(element instanceof ClassNode)) {
				return null;
			}
			if (!fFileInfoProvider.isProjectAvailable()) {
				return null;
			}
			if(fRootIf.getImplementationStatus((ClassNode)element) == EImplementationStatus.IMPLEMENTED){
				return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
			return null;
		}
	}

	public ClassViewer(
			ISectionContext parent, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(parent, updateContext, fileInfoProvider, STYLE);

		fFileInfoProvider = fileInfoProvider; 
		fNameColumn.setEditingSupport(new LocalNameEditingSupport());
		fPackageNameColumn.setEditingSupport(new PackageNameEditingSupport());

		fRootIf = new RootInterface(this, fileInfoProvider);
		fClassIf = new ClassInterface(this, fileInfoProvider);

		setText("Classes");
		
		if (fFileInfoProvider.isProjectAvailable()) {
			addButton("Add implemented class", new AddImplementedClassAdapter());
		}
		
		addButton("New test class", new AddNewClassAdapter());
		addButton("Remove selected", 
				new ActionSelectionAdapter(
						new DeleteAction(getViewer(), this), Messages.EXCEPTION_CAN_NOT_REMOVE_SELECTED_ITEMS));

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		setActionProvider(new ModelViewerActionProvider(getTableViewer(), this, fileInfoProvider));
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
	}

	@Override
	protected void createTableColumns(){
		fNameColumn = addColumn("Class", 150, new ClassViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ClassInterface.getLocalName((ClassNode)element);
			}
		});

		fPackageNameColumn = addColumn("Package", 150, new ClassViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return JavaUtils.getPackageName((ClassNode)element);
			}
		});
	}

	public void setInput(RootNode model){
		super.setInput(model.getClasses());
		fRootIf.setTarget(model);
	}

	private ClassInterface classIf(){
		if(fClassIf == null){
			fClassIf = new ClassInterface(this, fFileInfoProvider);
		}
		return fClassIf;
	}
}
