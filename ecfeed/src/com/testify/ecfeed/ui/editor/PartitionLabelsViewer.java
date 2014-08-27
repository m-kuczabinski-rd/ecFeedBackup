/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionLabelsViewer extends CheckboxTableViewerSection {
	
	private static final int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private PartitionInterface fPartitionIf;

	private class AddLabelAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			String newLabel = fPartitionIf.addNewLabel(PartitionLabelsViewer.this, getUpdateListener());
			if(newLabel != null){
				getTableViewer().editElement(newLabel, 0);
			}
		}
	}
	
	private class RemoveLabelsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fPartitionIf.removeLabels(getCheckedLabels(), PartitionLabelsViewer.this, getUpdateListener());
		}
	}
	
	public class LabelEditingSupport extends EditingSupport{
		private TextCellEditor fLabelCellEditor;
		
		public LabelEditingSupport(ColumnViewer viewer) {
			super(viewer);
			fLabelCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fLabelCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return fPartitionIf.isLabelInherited((String)element) == false;
		}

		@Override
		protected Object getValue(Object element) {
			return (String)element;
		}

		@Override
		protected void setValue(Object element, Object value) {
			fPartitionIf.renameLabel((String)element, (String)value, PartitionLabelsViewer.this, getUpdateListener());
		}
	}

	private class LabelColumnLabelProvider extends ColumnLabelProvider{
		
		private ColorManager fColorManager;

		LabelColumnLabelProvider(){
			fColorManager = new ColorManager();
		}
		
		@Override
		public String getText(Object element){
			return (String)element;
		}
		
		@Override
		public Color getForeground(Object element){
			if(element instanceof String){
				String label = (String)element;
				if(fPartitionIf.isLabelInherited(label)){
					return fColorManager.getColor(ColorConstants.INHERITED_LABEL_FOREGROUND);
				}
			}
			return null;
		}

//		@Override
//		public Color getBackground(Object element){
//			if(element instanceof String){
//				String label = (String)element;
//				if(fPartitionIf.isLabelInherited(label)){
//					return fColorManager.getColor(ColorConstants.INHERITED_LABEL_BACKGROUND);
//				}
//			}
//			return null;
//		}
//
		@Override
		public Font getFont(Object element){
			if(element instanceof String){
				String label = (String)element;
				if(fPartitionIf.isLabelInherited(label)){
					Font font = getTable().getFont();
					FontData currentFontData = font.getFontData()[0];
					FontData fd = new FontData();
					fd.setHeight(currentFontData.getHeight());
					fd.setStyle(fd.getStyle() | SWT.ITALIC);
					Device device = font.getDevice();
					return new Font(device, fd);
				}
			}
			return null;
		}
	}
	
	private class LabelCheckStateListener implements ICheckStateListener{
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			String label = (String)event.getElement();
			if(fPartitionIf.isLabelInherited(label)){
				getCheckboxViewer().setChecked(label, false);
			}
		}
	}

	public PartitionLabelsViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);

		fPartitionIf = new PartitionInterface(operationManager);
		getSection().setText("Labels");
		
		addButton("Add label", new AddLabelAdapter());
		addButton("Remove selected", new RemoveLabelsAdapter());

		getCheckboxViewer().addCheckStateListener(new LabelCheckStateListener());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		TableViewerColumn labelColumn = addColumn("Label", 150, new LabelColumnLabelProvider());
		labelColumn.setEditingSupport(new LabelEditingSupport(getTableViewer()));
	}
	
	public void setInput(PartitionNode	partition){
//		fSelectedPartition = partition;
		fPartitionIf.setTarget(partition);
		super.setInput(partition.getAllLabels());
	}
	
	protected Collection<String> getCheckedLabels(){
		Collection<String> labels = new ArrayList<String>();
		for(Object o : getCheckedElements()){
			if(o instanceof String){
				labels.add((String)o);
			}
		}
		return labels;
	}
}
