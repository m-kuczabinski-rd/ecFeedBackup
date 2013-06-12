package com.testify.ecfeed.dialogs;

import java.util.Vector;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.PartitionNode;

public class TestCasePartitionEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private Vector<PartitionNode> fTestData;
	private ComboBoxViewerCellEditor fCellEditor;

	public TestCasePartitionEditingSupport(TableViewer viewer, Vector<PartitionNode> testData) {
		super(viewer);
		fViewer = viewer;
		fTestData = testData;
		fCellEditor = new ComboBoxViewerCellEditor(fViewer.getTable(), SWT.TRAIL);
		fCellEditor.setLabelProvider(new LabelProvider());
		fCellEditor.setContentProvider(new ArrayContentProvider());
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
			fCellEditor.setInput(items);
		}
		return fCellEditor;
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
		if(value == null){
			value = ((CCombo)fCellEditor.getControl()).getText();
		}
		CategoryNode parent = (CategoryNode) ((PartitionNode)element).getParent();
		PartitionNode chosenPartition = (PartitionNode) parent.getPartition((String)value);
		if(chosenPartition == null){
			chosenPartition = (PartitionNode)element;
		}
		
//		PartitionNode chosenPartition = (PartitionNode) parent.getPartitions().elementAt((int)value);
		for(int i = 0; i < fTestData.size(); i++){
			if(fTestData.elementAt(i).getParent() == parent){
				fTestData.setElementAt(chosenPartition, i);
				break;
			}
		}
		fViewer.refresh();
	}
}
