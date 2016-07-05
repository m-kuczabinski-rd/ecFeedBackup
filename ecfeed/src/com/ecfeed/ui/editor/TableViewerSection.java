/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class TableViewerSection extends ViewerSection {

	public TableViewerSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider,
			int style) {
		super(sectionContext, updateContext, fileInfoProvider, style);
	}

	@Override
	protected IContentProvider viewerContentProvider(){
		return new ArrayContentProvider();
	}

	@Override
	protected IBaseLabelProvider viewerLabelProvider(){
		return new LabelProvider();
	}

	@Override
	protected StructuredViewer createViewer(Composite parent, int style) {
		return createTableViewer(parent, style);
	}

	@Override
	protected void createViewerColumns(){
		createTableColumns();

		getTable().setHeaderVisible(tableHeaderVisible());
		getTable().setLinesVisible(tableLinesVisible());
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

	public Table getTable(){
		return getTableViewer().getTable();
	}

	public TableViewer getTableViewer(){
		return (TableViewer)getViewer();
	}

	protected TableViewerColumn addColumn(String name, int width, ColumnLabelProvider labelProvider){
		TableViewerColumn viewerColumn = new TableViewerColumn(getTableViewer(), SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setWidth(width);
		column.setText(name);
		column.setResizable(true);
		column.setMoveable(true);
		viewerColumn.setLabelProvider(labelProvider);
		return viewerColumn;
	}

	protected boolean tableLinesVisible(){
		return true;
	}

	protected boolean tableHeaderVisible(){
		if(getTable().getColumns().length > 1){
			return true;
		}
		return false;
	}

	protected abstract void createTableColumns();
}
