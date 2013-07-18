package com.testify.ecfeed.dialogs;

import java.util.Vector;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.CategoryNode;
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
		PartitionNode partition = (PartitionNode)element;
		CategoryNode parent = (CategoryNode)partition.getParent();
		fCellEditor.setInput(parent.getPartitions());
		fCellEditor.setValue(partition.getName());
		return fCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((PartitionNode)element).toString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		int index = fTestData.indexOf(element);
		fTestData.setElementAt((PartitionNode)value, index);
	}
}
