package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ModelUtils;

public class CategoryChildrenViewer extends CheckboxTableViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	
	private CategoryNode fSelectedCategory;

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
	public CategoryChildrenViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		
		getSection().setText("Partitions");
		addButton("Add partition", new AddPartitionAdapter());
		addButton("Remove selected", new RemovePartitionsAdapter());
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		TableViewerColumn nameColumn = addColumn("Name", 150, new PartitionNameLabelProvider());
		nameColumn.setEditingSupport(new PartitionNameEditingSupport(this));

		TableViewerColumn valueColumn = addColumn("Value", 150, new PartitionValueLabelProvider());
		valueColumn.setEditingSupport(new PartitionValueEditingSupport(this));

	}

	public CategoryNode getSelectedCategory(){
		return fSelectedCategory;
	}

	public void setInput(CategoryNode category){
		fSelectedCategory = category;
		super.setInput(category.getPartitions());
	}
}
