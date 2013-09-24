package com.testify.ecfeed.ui.common;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.dialogs.ISetValueListener;
import com.testify.ecfeed.utils.EcModelUtils;

public class TestCaseValueEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	ArrayList<PartitionNode> fTestData;
	private ISetValueListener fSetValueListener;
	

	public TestCaseValueEditingSupport(TableViewer viewer, ArrayList<PartitionNode> testData, ISetValueListener setValueListener) {
		super(viewer);
		fViewer = viewer;
		fTestData = testData;
		fSetValueListener = setValueListener;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		TextCellEditor editor = new TextCellEditor(fViewer.getTable(), SWT.LEFT);
		String valueString = ((PartitionNode)element).getValueString();
		editor.setValue(valueString);
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		if(element instanceof PartitionNode){
			PartitionNode partiton = (PartitionNode)element;
			return partiton.getCategory() instanceof ExpectedValueCategoryNode;
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return ((PartitionNode)element).getValueString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		PartitionNode partition = (PartitionNode) element;
		partition.setValue(EcModelUtils.getPartitionValueFromString((String)value, partition.getCategory().getType()));
//		CategoryNode parent = ((PartitionNode)element).getCategory();
//		Object newValue = EcModelUtils.getPartitionValueFromString((String)value, parent.getType());
//		MethodNode method = parent.getMethod();
//		int parentIndex = method.getCategories().indexOf(parent);
//		if(parentIndex >= 0 && parentIndex <= fTestData.size()){
//			fTestData.get(parentIndex).setValue(value);;
//		}
		fSetValueListener.setValue(fTestData);
	}

}
