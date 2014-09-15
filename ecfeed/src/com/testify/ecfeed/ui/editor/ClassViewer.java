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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.ModelViewerGlobalActionProvider;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class ClassViewer extends TableViewerSection {
	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;

	private TableViewerColumn fNameColumn;
	private RootInterface fRootIf;
	private ClassInterface fClassIf;

	private TableViewerColumn fPackageNameColumn;


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
			fClassIf.setTarget(target);
			fClassIf.setQualifiedName(qualifiedName, ClassViewer.this);
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
		public void widgetSelected(SelectionEvent e) {
			ClassNode addedClass = fRootIf.addImplementedClass(ClassViewer.this);
			if(addedClass != null){
				selectElement(addedClass);
				fNameColumn.getViewer().editElement(addedClass, 0);
			}
		}
	}
	
	private class AddNewClassAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ClassNode addedClass = fRootIf.addNewClass(ClassViewer.this);
			if(addedClass != null){
				selectElement(addedClass);
				fNameColumn.getViewer().editElement(addedClass, 0);
			}
		}
	}

	private class ClassViewerColumnLabelProvider extends ColumnLabelProvider {
		@Override
		public Color getForeground(Object element) {
			if (element instanceof ClassNode) {
				if(fRootIf.implementationStatus((ClassNode)element) == ImplementationStatus.IMPLEMENTED){
					return ColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
				}
			}
			return null;
		}
	}
	
	public ClassViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent, parent.getOperationManager());
		fRootIf = new RootInterface();
		fClassIf = new ClassInterface();

		fNameColumn.setEditingSupport(new LocalNameEditingSupport());
		fPackageNameColumn.setEditingSupport(new PackageNameEditingSupport());

		setText("Classes");
		addButton("Add implemented class", new AddImplementedClassAdapter());
		addButton("New test class", new AddNewClassAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this)));
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		addKeyListener(SWT.DEL, new DeleteAction(getViewer(), this));
		setActionProvider(new ModelViewerGlobalActionProvider(getTableViewer(), this));
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
	
}
