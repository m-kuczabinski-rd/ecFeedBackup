package com.testify.ecfeed.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.dialogs.PartitionSettingsDialog;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.EcModelUtils;

public class CategoryNodeDetailsPage extends GenericNodeDetailsPage implements IModelUpdateListener{

	private CategoryNode fSelectedNode;
	private Section fMainSection;
	private CheckboxTableViewer fPartitionsViewer;
	private Table fPartitionsTable;
	
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
			if(EcModelUtils.validatePartitionName((String)value, fSelectedNode, (PartitionNode)element)){
				MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
						DialogStrings.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
						DialogStrings.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE,
						MessageDialog.ERROR, new String[] {"OK"}, 0);
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
			if(!EcModelUtils.validatePartitionStringValue(valueString, fSelectedNode)){
				MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
						DialogStrings.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
						DialogStrings.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE,
						MessageDialog.ERROR, new String[] {"OK"}, 0);
				dialog.open();
			}
			else{
				Object newValue = EcModelUtils.getPartitionValueFromString(valueString, fSelectedNode.getType());
				((PartitionNode)element).setValue(newValue);
				updateModel((RootNode)fSelectedNode.getRoot());
			}
		}
	}

	/**
	 * Create the details page.
	 */
	public CategoryNodeDetailsPage(EcMultiPageEditor editor, ModelMasterDetailsBlock parentBlock) {
		super(editor, parentBlock);
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
		//
		Composite composite = fToolkit.createComposite(fMainSection, SWT.NONE);
		fToolkit.paintBordersFor(composite);
		fMainSection.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Label lblPartitons = new Label(composite, SWT.NONE);
		fToolkit.adapt(lblPartitons, true, true);
		lblPartitons.setText("Partitons");
		
		fPartitionsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		fPartitionsViewer.setContentProvider(new ArrayContentProvider());
		fPartitionsViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		fPartitionsTable = fPartitionsViewer.getTable();
		fPartitionsTable.setLinesVisible(true);
		fPartitionsTable.setHeaderVisible(true);
		fPartitionsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		fToolkit.paintBordersFor(fPartitionsTable);
		
		TableViewerColumn nameViewerColumn = new TableViewerColumn(fPartitionsViewer, SWT.NONE);
		TableColumn partitionNameColumn = nameViewerColumn.getColumn();
		partitionNameColumn.setWidth(190);
		partitionNameColumn.setText("Partition name");
		nameViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((PartitionNode)element).getName();
			}
		});
		nameViewerColumn.setEditingSupport(new PartitionNameEditingSupport(fPartitionsViewer));
		
		TableViewerColumn valueViewerColumn = new TableViewerColumn(fPartitionsViewer, SWT.NONE);
		TableColumn valueColumn = valueViewerColumn.getColumn();
		valueColumn.setWidth(100);
		valueColumn.setText("Value");
		valueViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				Object partitionValue = ((PartitionNode)element).getValueString();
				if(partitionValue != null){
					return partitionValue.toString();
				}
				return Constants.NULL_VALUE_STRING_REPRESENTATION;
			}
		});
		valueViewerColumn.setEditingSupport(new PartitionValueEditingSupport(fPartitionsViewer));
		
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		fToolkit.adapt(buttonsComposite);
		fToolkit.paintBordersFor(buttonsComposite);
		buttonsComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button addPartitionButton = new Button(buttonsComposite, SWT.NONE);
		fToolkit.adapt(addPartitionButton, true, true);
		addPartitionButton.setText("Add Partition...");
		addPartitionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				PartitionSettingsDialog dialog = new PartitionSettingsDialog(Display.getDefault().getActiveShell(), 
						fSelectedNode, null);
				if(dialog.open() == Window.OK){
					String partitionName = dialog.getPartitionName();
					Object partitionValue = dialog.getPartitionValue();
					fSelectedNode.addPartition(new PartitionNode(partitionName, partitionValue));
					updateModel((RootNode)fSelectedNode.getRoot());
				}
			}
		});

		Button removeSelectedButton = new Button(buttonsComposite, SWT.NONE);
		fToolkit.adapt(removeSelectedButton, true, true);
		removeSelectedButton.setText("Remove Selected");
		removeSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
						DialogStrings.DIALOG_REMOVE_PARTITIONS_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
						DialogStrings.DIALOG_REMOVE_PARTITIONS_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, new String[] {"OK", "Cancel"}, 0);
				if (dialog.open() == Window.OK) {
					for(Object partition : fPartitionsViewer.getCheckedElements()){
						EcModelUtils.removeReferences((PartitionNode)partition);
						fSelectedNode.removeChild((PartitionNode)partition);
						updateModel((RootNode)fSelectedNode.getRoot());
					}
				}
			}
		});

	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.getFirstElement() instanceof CategoryNode){
			fSelectedNode = (CategoryNode)structuredSelection.getFirstElement();
			refresh();
		}
	}
	
	public void refresh() {
		if(fSelectedNode == null){
			return;
		}
		fMainSection.setText(fSelectedNode.toString());
		fPartitionsViewer.setInput(fSelectedNode.getPartitions());
	}
}
