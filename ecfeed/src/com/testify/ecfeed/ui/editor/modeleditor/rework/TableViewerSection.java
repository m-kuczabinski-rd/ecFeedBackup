package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class TableViewerSection extends ViewerSection {

	public TableViewerSection(Composite parent, FormToolkit toolkit, int style, int buttonsPosition) {
		super(parent, toolkit, style, buttonsPosition);
	}

	protected TableViewer createTableViewer(Composite parent, int style){
		Table table = createTable(parent, style);
		table.setLayoutData(viewerLayoutData());
		TableViewer tableViewer = new TableViewer(table);
		return tableViewer;
	}
	
	protected Table createTable(Composite parent, int style) {
		return new Table(parent, style);
	}

	protected void addSelectionChangedListener(ISelectionChangedListener listener){
		getTableViewer().addSelectionChangedListener(listener);
	}
	
	protected Table getTable(){
		return getTableViewer().getTable();
	}
	
	protected TableViewer getTableViewer(){
		return (TableViewer)getViewer();
	}
	
	public void selectElement(Object element){
		getViewer().setSelection(new StructuredSelection(element), true);
	}

	protected void addColumn(String name, int width, ColumnLabelProvider labelProvider){
			TableViewerColumn viewerColumn = new TableViewerColumn(getTableViewer(), SWT.NONE);
			TableColumn column = viewerColumn.getColumn();
			column.setWidth(width);
			column.setText(name);
			column.setResizable(true);
			column.setMoveable(true);
			viewerColumn.setLabelProvider(labelProvider);
	}
	
	@Override
	protected StructuredViewer createViewer(Composite parent, int style) {
		return createTableViewer(parent, style);
	}

	@Override
	protected void createViewerColumns(){
		createTableColumns();
	}
	
	protected abstract void createTableColumns();
}
