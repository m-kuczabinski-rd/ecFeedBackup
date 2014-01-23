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

package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.RowLayout;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.PartitionSettingsDialog;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class CategoryNodeDetailsPage extends GenericNodeDetailsPage implements IModelUpdateListener{

	private CategoryNode fSelectedCategory;
	private Section fMainSection;
	private CheckboxTableViewer fPartitionsViewer;
	private Table fPartitionsTable;
	private ColorManager fColorManager;
	
	public class PartitionNameEditingSupport extends EditingSupport{
		private TextCellEditor fNameCellEditor;

		public PartitionNameEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fNameCellEditor = new TextCellEditor(fPartitionsTable);
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
			return ((PartitionNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			if(!fSelectedCategory.validatePartitionName((String)value)){
				MessageDialog dialog = new MessageDialog(getActiveShell(), 
						Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
						Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE,
						MessageDialog.ERROR, 
						new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID);
				dialog.open();
			}
			else{
				((PartitionNode)element).setName((String)value);
				updateModel((RootNode)((PartitionNode)element).getRoot());
			}
		}
	}

	public class PartitionValueEditingSupport extends EditingSupport{
		private TextCellEditor fValueCellEditor;
		
		public PartitionValueEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fValueCellEditor = new TextCellEditor(fPartitionsTable);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fValueCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((PartitionNode)element).getValueString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String valueString = (String)value;
			if(!fSelectedCategory.validatePartitionStringValue(valueString)){
				MessageDialog dialog = new MessageDialog(getActiveShell(), 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE,
						MessageDialog.ERROR, 
						new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID);
				dialog.open();
			}
			else{
				Object newValue = fSelectedCategory.getPartitionValueFromString(valueString);
				((PartitionNode)element).setValue(newValue);
				updateModel((RootNode)fSelectedCategory.getRoot());
			}
		}
	}

	/**
	 * Create the details page.
	 */
	public CategoryNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
		fColorManager = new ColorManager();
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = fToolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		fMainSection.setText("Empty Section");
		Composite mainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		fToolkit.paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);
		mainComposite.setLayout(new GridLayout(1, false));

		fToolkit.createLabel(mainComposite, "Partitions");
		
		createPartitionViewer(mainComposite);
		
		createBottomButtons(mainComposite);

	}

	private void createPartitionViewer(Composite composite) {
		fPartitionsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fPartitionsViewer.setContentProvider(new ArrayContentProvider());
		fPartitionsViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		fPartitionsTable = fPartitionsViewer.getTable();
		fPartitionsTable.setLinesVisible(true);
		fPartitionsTable.setHeaderVisible(true);
		fPartitionsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		fToolkit.paintBordersFor(fPartitionsTable);
		
		TableViewerColumn nameViewerColumn = createTableViewerColumn(fPartitionsViewer, "Partition name", 
				190, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((PartitionNode)element).getName();
			}
			
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}

		});
		nameViewerColumn.setEditingSupport(new PartitionNameEditingSupport(fPartitionsViewer));
		
		TableViewerColumn valueViewerColumn = createTableViewerColumn(fPartitionsViewer, "Value", 
				100, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode partition = (PartitionNode)element;
				if(partition.isAbstract()){
					return "ABSTRACT";
				}
				Object partitionValue = partition.getValueString();
				if(partitionValue != null){
					return partitionValue.toString();
				}
				return Constants.NULL_VALUE_STRING_REPRESENTATION;
			}
			
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		valueViewerColumn.setEditingSupport(new PartitionValueEditingSupport(fPartitionsViewer));
	}

	private Color getColor(Object element){
		if(element instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)element;
			if(partition.isAbstract()){
				return fColorManager.getColor(ColorConstants.ABSTRACT_PARTITION);
			}
		}
		return null;
	}

	private void createBottomButtons(Composite composite) {
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		fToolkit.adapt(buttonsComposite);
		fToolkit.paintBordersFor(buttonsComposite);
		buttonsComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
	
		createButton(buttonsComposite, "Add Partition...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				PartitionSettingsDialog dialog = new PartitionSettingsDialog(getActiveShell(), 
						fSelectedCategory, null);
				if(dialog.open() == Window.OK){
					String partitionName = dialog.getPartitionName();
					Object partitionValue = dialog.getPartitionValue();
					fSelectedCategory.addPartition(new PartitionNode(partitionName, partitionValue));
					updateModel(fSelectedCategory);
				}
			}
		});
	
		createButton(buttonsComposite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				MessageDialog dialog = new MessageDialog(getActiveShell(), 
						DialogStrings.DIALOG_REMOVE_PARTITIONS_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
						DialogStrings.DIALOG_REMOVE_PARTITIONS_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, 
						new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
						IDialogConstants.OK_ID);
				if (dialog.open() == Window.OK) {
					for(Object partition : fPartitionsViewer.getCheckedElements()){
						if(fSelectedCategory.getPartitions().size() > 1){
							fSelectedCategory.removePartition((PartitionNode)partition);
						}
						else{
							MessageDialog dlg = new MessageDialog(getActiveShell(), 
									DialogStrings.DIALOG_REMOVE_LAST_PARTITION_TITLE, 
									Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 
									DialogStrings.DIALOG_REMOVE_LAST_PARTITION_MESSAGE,
									MessageDialog.INFORMATION, 
									new String[] {IDialogConstants.OK_LABEL},
									IDialogConstants.OK_ID);
							dlg.open();
						}
						updateModel((RootNode)fSelectedCategory.getRoot());
					}
				}
			}
		});
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedCategory = (CategoryNode)fSelectedNode;
		refresh();
	}
	
	public void refresh() {
		if(fSelectedCategory == null){
			return;
		}
		fMainSection.setText(fSelectedCategory.toString());
		fPartitionsViewer.setInput(fSelectedCategory.getPartitions());
	}
}
