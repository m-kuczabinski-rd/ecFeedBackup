package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.Messages;

public class PartitionValueEditingSupport extends EditingSupport {
	private TextCellEditor fValueCellEditor;
	private BasicSection fSection;
	
	public PartitionValueEditingSupport(CategoryChildrenViewer viewer) {
		super(viewer.getTableViewer());
		fValueCellEditor = new TextCellEditor(viewer.getTable());
		fSection = viewer;
	}

	public PartitionValueEditingSupport(PartitionChildrenViewer viewer) {
		super(viewer.getTableViewer());
		fValueCellEditor = new TextCellEditor(viewer.getTable());
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
		if(!getCategory().validatePartitionStringValue(valueString)){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
					Messages.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE);
		}
		else{
			Object newValue = getCategory().getPartitionValueFromString(valueString);
			PartitionNode partition = (PartitionNode)element;
			if(newValue.equals(partition.getValue()) == false){
				((PartitionNode)element).setValue(newValue);
				fSection.modelUpdated();
			}
		}
	}
	
	private CategoryNode getCategory(){
		if(fSection instanceof CategoryChildrenViewer){
			return ((CategoryChildrenViewer)fSection).getSelectedCategory();
		}
		else if(fSection instanceof PartitionChildrenViewer){
			return ((PartitionChildrenViewer)fSection).getSelectedPartition().getCategory();
		}
		return null;
	}
}
