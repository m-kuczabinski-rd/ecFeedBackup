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

package com.testify.ecfeed.ui.editor.modeleditor.obsolete;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.utils.Constants;
import com.testify.ecfeed.utils.ModelUtils;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

public class ObsoletePartitionNodeDetailsPage extends ObsoleteGenericNodeDetailsPage{

	private PartitionNode fSelectedPartition;
	private Section fMainSection;
	private Section fChildrenSection;
	private Text fPartitionNameText;
	private Text fPartitionValueText;
	private Button fApplyButton;
	private CheckboxTableViewer fPartitionsViewer;
	private Table fPartitionsTable;
	private ColorManager fColorManager;
	private Section fLabelsSection;
	private CheckboxTableViewer fLabelsViewer;
	private Table fLabelsTable;
	
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
			if(!fSelectedPartition.getCategory().validatePartitionName((String)value)){
				MessageDialog.openError(getActiveShell(), 
						Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE,
						Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE);
			}
			else{
				((PartitionNode)element).setName((String)value);
				updateModel((PartitionNode)element);
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
			return !((PartitionNode)element).isAbstract();
		}

		@Override
		protected Object getValue(Object element) {
			return ((PartitionNode)element).getValueString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String valueString = (String)value;
			if(!ModelUtils.validatePartitionStringValue(valueString, fSelectedPartition.getCategory().getType())){
				MessageDialog dialog = new MessageDialog(getActiveShell(), 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE,
						MessageDialog.ERROR, 
						new String[] {IDialogConstants.OK_LABEL}, IDialogConstants.OK_ID);
				dialog.open();
			}
			else{
				Object newValue = ModelUtils.getPartitionValueFromString(valueString, fSelectedPartition.getCategory().getType());
				((PartitionNode)element).setValue(newValue);
				updateModel(fSelectedPartition);
			}
		}
	}

	public class LabelEditingSupport extends EditingSupport{
		private TextCellEditor fLabelCellEditor;
		
		public LabelEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fLabelCellEditor = new TextCellEditor(fLabelsTable);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fLabelCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return !fSelectedPartition.getInheritedLabels().contains((String)element);
		}

		@Override
		protected Object getValue(Object element) {
			return (String)element;
		}

		@Override
		protected void setValue(Object element, Object value) {
			String oldLabel = (String)element;
			String newLabel = (String)value;
			if(fSelectedPartition.getAllLabels().contains(newLabel) == false){
				fSelectedPartition.removeLabel(oldLabel);
				fSelectedPartition.addLabel(newLabel);
				updateModel(fSelectedPartition);
			}
		}
	}

	/**
	 * Create the details page.
	 */
	public ObsoletePartitionNodeDetailsPage(ObsoleteModelMasterDetailsBlock parentBlock) {
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

		Composite mainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		fToolkit.paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);

		createNameValueComposite(mainComposite);
		
		createChildrenSection(mainComposite);
		
		createLabelsSection(mainComposite);
		
	}

	private void createNameValueComposite(Composite parent) {
		Composite nameAndValueComposite = fToolkit.createComposite(parent, SWT.NONE);
		nameAndValueComposite.setLayout(new GridLayout(3, false));
		nameAndValueComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fToolkit.paintBordersFor(nameAndValueComposite);

		createNameEdit(nameAndValueComposite);
		
		createValueEdit(nameAndValueComposite);
	}

	private void createNameEdit(Composite parent) {
		fToolkit.createLabel(parent, "Partition name");
		fPartitionNameText = fToolkit.createText(parent, null);
		fPartitionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionNameText.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					String newName = fPartitionNameText.getText();
					String newValue = fPartitionValueText.getText();
					renamePartition(newName);
					changePartitionValue(newValue);
				}
			}
		});

		Composite buttonComposite = fToolkit.createComposite(parent);
		buttonComposite.setLayout(new GridLayout(1, true));
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
		fApplyButton = createButton(buttonComposite, "Apply", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String newName = fPartitionNameText.getText();
				String newValue = fPartitionValueText.getText();
				renamePartition(newName);
				changePartitionValue(newValue);
			}
		});
	}

	private void createValueEdit(Composite parent) {
		fToolkit.createLabel(parent, "Partition value");
		fPartitionValueText = fToolkit.createText(parent, null);
		fPartitionValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionValueText.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					String newName = fPartitionNameText.getText();
					String newValue = fPartitionValueText.getText();
					renamePartition(newName);
					changePartitionValue(newValue);
				}
			}
		});
	}

	private void createChildrenSection(Composite mainComposite) {
		fChildrenSection = fToolkit.createSection(mainComposite, Section.TITLE_BAR);
		fChildrenSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(fChildrenSection);
		fChildrenSection.setText("Children");
		fChildrenSection.setExpanded(true);
		Composite childrenComposite = createChildrenComposite(fChildrenSection);
		fChildrenSection.setClient(childrenComposite);
	}

	private Composite createChildrenComposite(Composite parent) {
		Composite childrenComposite = fToolkit.createComposite(parent, SWT.NONE);
		childrenComposite.setLayout(new GridLayout(1, false));
		childrenComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createChildrenViewer(childrenComposite);
		createChildrenViewerButtons(childrenComposite);
		return childrenComposite;
	}

	private void createChildrenViewer(Composite parent) {
		fPartitionsViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION);
		fPartitionsViewer.setContentProvider(new ArrayContentProvider());
		fPartitionsViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		fPartitionsTable = fPartitionsViewer.getTable();
		fPartitionsTable.setLinesVisible(true);
		fPartitionsTable.setHeaderVisible(true);
		fPartitionsTable.setLayoutData(VIEWERS_GRID_DATA);
		
		TableViewerColumn nameViewerColumn = createTableViewerColumn(fPartitionsViewer, "Partition name", 
				190, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((PartitionNode)element).getName();
			}
			
			@Override
			public Color getForeground(Object element){
				return getPartitionColor(element);
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
				return com.testify.ecfeed.parsers.Constants.NULL_VALUE_STRING_REPRESENTATION;
			}
			
			@Override
			public Color getForeground(Object element){
				return getPartitionColor(element);
			}
		});
		valueViewerColumn.setEditingSupport(new PartitionValueEditingSupport(fPartitionsViewer));
	}

	private void createChildrenViewerButtons(Composite parent) {
		Composite buttonsComposite = fToolkit.createComposite(parent, SWT.NONE);
		buttonsComposite.setLayout(new RowLayout());
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		createButton(buttonsComposite, "Add Partition...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME;
				int i = 1;
				while(fSelectedPartition.getPartition(newPartitionName) != null){
					newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME + "_" + i;
					i++;
				}
				Object value = ModelUtils.getDefaultExpectedValue(fSelectedPartition.getCategory().getType());
				PartitionNode newPartition = new PartitionNode(newPartitionName, value);
				fSelectedPartition.addPartition(newPartition);
				updateModel(fSelectedPartition);
				fPartitionsTable.setSelection(fSelectedPartition.getPartitions().size() - 1);
				CategoryNode category = fSelectedPartition.getCategory(); 
				MethodNode method = category.getMethod();
				int categoryIndex = method.getCategories().indexOf(category);
				//replace the current partition (that is abstract now) by newly created partition
				for(TestCaseNode testCase : method.getTestCases()){
					if(testCase.getTestData().get(categoryIndex) == fSelectedPartition){
						testCase.getTestData().set(categoryIndex, newPartition);
					}
				}
	
				updateModel(fSelectedPartition);
			}
		});
	
		createButton(buttonsComposite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				MessageDialog dialog = new MessageDialog(getActiveShell(), 
						Messages.DIALOG_REMOVE_PARTITIONS_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
						Messages.DIALOG_REMOVE_PARTITIONS_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, 
						new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
						IDialogConstants.OK_ID);
				if (dialog.open() == Window.OK) {
					for(Object element : fPartitionsViewer.getCheckedElements()){
						PartitionNode partition = (PartitionNode)element;
						fSelectedPartition.removePartition(partition);
						updateModel(fSelectedPartition);
					}
				}
			}
		});
	
	}

	private void createLabelsSection(Composite mainComposite) {
		fLabelsSection = fToolkit.createSection(mainComposite, Section.TITLE_BAR);
		fLabelsSection.setLayout(new GridLayout(1, false));
		fLabelsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		getToolkit().paintBordersFor(fChildrenSection);
		fLabelsSection.setText("Labels");
		fLabelsSection.setExpanded(true);
		Composite labelsComposite = createLabelsComposite(fLabelsSection);
		fLabelsSection.setClient(labelsComposite);
	
	}

	private Composite createLabelsComposite(Composite parent) {
		Composite labelsComposite = fToolkit.createComposite(parent, SWT.NONE);
		labelsComposite.setLayout(new GridLayout(1, false));
		labelsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createLabelsViewer(labelsComposite);
		createLabelsViewerButtons(labelsComposite);
		return labelsComposite;
	}

	private void createLabelsViewer(Composite parent) {
		fLabelsViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION);
		fLabelsViewer.setContentProvider(new ArrayContentProvider());
		fLabelsTable = fLabelsViewer.getTable();
		fLabelsTable.setLayoutData(VIEWERS_GRID_DATA);
		fLabelsTable.setLinesVisible(true);
		fLabelsTable.setHeaderVisible(true);
		fLabelsViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				String label = (String)event.getElement();
				if(fSelectedPartition.getInheritedLabels().contains(label)){
					fLabelsViewer.setChecked(label, false);
				}
			}
		});
		
		TableViewerColumn labelColumn = createTableViewerColumn(fLabelsViewer, "Label", 
				190, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return (String)element;
			}
			
			@Override
			public Color getForeground(Object element){
				if(element instanceof String){
					String label = (String)element;
					if(fSelectedPartition.getInheritedLabels().contains(label)){
						return fColorManager.getColor(ColorConstants.INHERITED_LABEL_FOREGROUND);
					}
				}
				return null;
			}
	
			@Override
			public Font getFont(Object element){
				if(element instanceof String){
					String label = (String)element;
					if(fSelectedPartition.getInheritedLabels().contains(label)){
						Font font = fLabelsTable.getFont();
						FontData currentFontData = font.getFontData()[0];
						FontData fd = new FontData();
						fd.setHeight(currentFontData.getHeight());
						fd.setStyle(fd.getStyle() | SWT.ITALIC);
						Device device = font.getDevice();
						return new Font(device, fd);
					}
				}
				return null;
			}
		});
		labelColumn.setEditingSupport(new LabelEditingSupport(fLabelsViewer));
	}

	private void createLabelsViewerButtons(Composite parent) {
		Composite buttonsComposite = fToolkit.createComposite(parent, SWT.NONE);
		buttonsComposite.setLayout(new RowLayout());
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		createButton(buttonsComposite, "Add Label...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String newLabel = Constants.DEFAULT_LABEL;
				int i = 1;
				while(fSelectedPartition.getAllLabels().contains(newLabel)){
					newLabel = Constants.DEFAULT_LABEL + "(" + i + ")";
					i++;
				}
				if(fSelectedPartition.addLabel(newLabel) == false){
					new MessageDialog(getActiveShell(), 
							Messages.DIALOG_CANNOT_ADD_LABEL_TITLE, 
							Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
							Messages.DIALOG_CANNOT_ADD_LABEL_MESSAGE,
							MessageDialog.ERROR, 
							new String[] {IDialogConstants.OK_LABEL},
							IDialogConstants.OK_ID).open();
				};
				updateModel(fSelectedPartition);
			}
		});
	
		createButton(buttonsComposite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				for(Object element : fLabelsViewer.getCheckedElements()){
					String label = (String)element;
					if(fSelectedPartition.removeLabel(label) == false){
						new MessageDialog(getActiveShell(), 
								Messages.DIALOG_CANNOT_REMOVE_LABEL_TITLE, 
								Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
								Messages.DIALOG_CANNOT_REMOVE_LABEL_MESSAGE(label),
								MessageDialog.ERROR, 
								new String[] {IDialogConstants.OK_LABEL},
								IDialogConstants.OK_ID).open();
					}
					updateModel(fSelectedPartition);
				}
			}
		});
	
	}

	private Color getPartitionColor(Object element){
		if(element instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)element;
			if(partition.isAbstract()){
				return fColorManager.getColor(ColorConstants.ABSTRACT_PARTITION);
			}
		}
		return null;
	}

	private void renamePartition(String name) {
		if(fSelectedPartition.getCategory().validatePartitionName(name) && fSelectedPartition.getSibling(name) == null){
			fSelectedPartition.setName(name);
			updateModel(fSelectedPartition);
		}
		else{
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
			fPartitionNameText.setText(fSelectedPartition.getName());
		}
	}

	private void changePartitionValue(String valueString) {
		CategoryNode parent = fSelectedPartition.getCategory();
		if(ModelUtils.validatePartitionStringValue(valueString, parent.getType())){
			fSelectedPartition.setValue(ModelUtils.getPartitionValueFromString(valueString, parent.getType()));
			updateModel(fSelectedPartition);
		}
		else{
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
			fPartitionValueText.setText(fSelectedPartition.getValueString());
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedPartition = (PartitionNode)fSelectedNode;
		refresh();
	}
	
	public void refresh() {
		if(fSelectedPartition == null){
			return;
		}
		fMainSection.setText(fSelectedPartition.toString());
		fPartitionNameText.setText(fSelectedPartition.getName());
		fPartitionValueText.setText(fSelectedPartition.getValueString());
		if(fSelectedPartition.isAbstract()){
			fPartitionValueText.setEnabled(false);
			fApplyButton.setEnabled(false);
		}
		else{
			fPartitionValueText.setEnabled(true);
			fApplyButton.setEnabled(true);
		}
		fPartitionsViewer.setInput(fSelectedPartition.getPartitions());
		fLabelsViewer.setInput(fSelectedPartition.getAllLabels());
	}
}
