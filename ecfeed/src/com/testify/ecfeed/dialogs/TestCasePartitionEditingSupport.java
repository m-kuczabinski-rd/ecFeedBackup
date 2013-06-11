package com.testify.ecfeed.dialogs;

import java.util.Vector;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.PartitionNode;

public class TestCasePartitionEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private Vector<PartitionNode> fTestData;

	public TestCasePartitionEditingSupport(TableViewer viewer, Vector<PartitionNode> testData) {
		super(viewer);
		fViewer = viewer;
		fTestData = testData;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		Vector<String> items = new Vector<String>();
		if(((PartitionNode)element).getParent() != null){
			Vector<GenericNode> partitions = ((PartitionNode)element).getParent().getChildren();
			for(GenericNode node : partitions){
				if(node instanceof PartitionNode){
					items.add(node.getName());
				}
			}
		}
		return new ComboBoxCellEditor(fViewer.getTable(), items.toArray(new String[]{}), SWT.TRAIL);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		CategoryNode category = (CategoryNode)((PartitionNode)element).getParent();
		String elementName = ((PartitionNode)element).getName();
		Vector<PartitionNode> partitions = category.getPartitions();
		for(int i = 0; i < partitions.size(); i++){
			if(elementName.equals(partitions.elementAt(i).getName())){
				return i;
			}
		}
		return 0;
	}

	@Override
	protected void setValue(Object element, Object value) {
		CategoryNode parent = (CategoryNode) ((PartitionNode)element).getParent();
		
		PartitionNode chosenPartition = (PartitionNode) parent.getPartitions().elementAt((int)value);
		for(int i = 0; i < fTestData.size(); i++){
			if(fTestData.elementAt(i).getParent() == chosenPartition.getParent()){
				fTestData.setElementAt(chosenPartition, i);
			}
		}
		fViewer.refresh();
	}
}
