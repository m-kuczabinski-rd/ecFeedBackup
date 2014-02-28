package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ModelUtils;

public class PartitionsViewer extends CheckboxTableViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final static String EMPTY_STRING = "";
	
	private CategoryNode fSelectedCategory;
	private ColorManager fColorManager;

	private class PartitionNameEditingSupport extends EditingSupport{
		private TextCellEditor fNameCellEditor;

		public PartitionNameEditingSupport(ColumnViewer viewer) {
			super(viewer);
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
			return ((PartitionNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			String newName = (String)value;
			PartitionNode partition = (PartitionNode)element;
			if(partition.getName().equals(newName)) return;
			if(!fSelectedCategory.validatePartitionName(newName) || 
					partition.hasSibling(newName)){
				MessageDialog.openError(getActiveShell(), 
						Messages.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
						Messages.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE);
			}
			else{
				((PartitionNode)element).setName((String)value);
				modelUpdated();
			}
		}
	}

	private class PartitionValueEditingSupport extends EditingSupport{
		private TextCellEditor fValueCellEditor;
		
		public PartitionValueEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fValueCellEditor = new TextCellEditor(getTable());
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
			if(!fSelectedCategory.validatePartitionStringValue(valueString)){
				MessageDialog.openError(getActiveShell(), 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
						Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE);
			}
			else{
				Object newValue = fSelectedCategory.getPartitionValueFromString(valueString);
				PartitionNode partition = (PartitionNode)element;
				if(newValue.equals(partition.getValue()) == false){
					((PartitionNode)element).setValue(newValue);
					modelUpdated();
				}
			}
		}
	}

	private class AddPartitionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			String newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME;
			int i = 1;
			while(fSelectedCategory.getPartition(newPartitionName) != null){
				newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME + "_" + i;
				i++;
			}
			Object value = ModelUtils.getDefaultExpectedValue(fSelectedCategory.getType());
			PartitionNode newPartition = new PartitionNode(newPartitionName, value);
			fSelectedCategory.addPartition(newPartition);
			getTable().setSelection(fSelectedCategory.getPartitions().size() - 1);
			modelUpdated();
		}
	}
	
	private class RemovePartitionsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if (MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_PARTITIONS_TITLE, 
					Messages.DIALOG_REMOVE_PARTITIONS_MESSAGE)) {
				for(Object partition : getCheckedElements()){
					if(fSelectedCategory.getPartitions().size() > 1){
						fSelectedCategory.removePartition((PartitionNode)partition);
					}
					else{
						MessageDialog.openInformation(getActiveShell(), 
								Messages.DIALOG_REMOVE_LAST_PARTITION_TITLE, 
								Messages.DIALOG_REMOVE_LAST_PARTITION_MESSAGE);
					}
				}
				modelUpdated();
			}
		}
	}

	public PartitionsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		fColorManager = new ColorManager();
		
		getSection().setText("Partitions");
		addButton("Add partition", new AddPartitionAdapter());
		addButton("Remove selected", new RemovePartitionsAdapter());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		TableViewerColumn nameColumn = addColumn("Name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof PartitionNode){
					return ((PartitionNode)element).getName();
				}
				return EMPTY_STRING;
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		nameColumn.setEditingSupport(new PartitionNameEditingSupport(getTableViewer()));

		TableViewerColumn valueColumn = addColumn("Value", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof PartitionNode){
					PartitionNode partition = (PartitionNode)element;
					return partition.isAbstract()?"[ABSTRACT]":partition.getValueString();
				}
				return EMPTY_STRING;
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		valueColumn.setEditingSupport(new PartitionValueEditingSupport(getTableViewer()));

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

	public void setInput(CategoryNode category){
		fSelectedCategory = category;
		super.setInput(category.getPartitions());
	}
}
