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

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
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
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.editor.actions.ModelModyfyingAction;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionLabelsViewer extends TableViewerSection {
	
	private static final int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private PartitionInterface fPartitionIf;

	private class AddLabelAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			String newLabel = fPartitionIf.addNewLabel(PartitionLabelsViewer.this);
			if(newLabel != null){
				getTableViewer().editElement(newLabel, 0);
			}
		}
	}
	
	private class RemoveLabelsAction extends ModelModyfyingAction{
		public RemoveLabelsAction() {
			super(getViewer(), PartitionLabelsViewer.this);
		}
		
		@Override
		public boolean isEnabled(){
			for(String label : getSelectedLabels()){
				if(fPartitionIf.isLabelInherited(label) == false){
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void run(){
			fPartitionIf.removeLabels(getSelectedLabels(), PartitionLabelsViewer.this);
		}

		@SuppressWarnings("unchecked")
		private List<String> getSelectedLabels(){
			return getSelection().toList();
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
			fPartitionIf.renameLabel((String)element, (String)value, PartitionLabelsViewer.this);
		}
	}

	private class LabelColumnLabelProvider extends ColumnLabelProvider{
		@Override
		public String getText(Object element){
			return (String)element;
		}
		
		@Override
		public Color getForeground(Object element){
			if(element instanceof String){
				String label = (String)element;
				if(fPartitionIf.isLabelInherited(label)){
					return ColorManager.getColor(ColorConstants.INHERITED_LABEL_FOREGROUND);
				}
			}
			return null;
		}

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

	public PartitionLabelsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent, parent.getOperationManager());

		fPartitionIf = new PartitionInterface();
		getSection().setText("Labels");
		
		addButton("Add label", new AddLabelAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new RemoveLabelsAction()));

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		addKeyListener(SWT.DEL, new RemoveLabelsAction());
	}

	@Override
	protected void createTableColumns() {
		TableViewerColumn labelColumn = addColumn("Label", 150, new LabelColumnLabelProvider());
		labelColumn.setEditingSupport(new LabelEditingSupport(getTableViewer()));
	}
	
	public void setInput(PartitionNode	partition){
		fPartitionIf.setTarget(partition);
		super.setInput(partition.getAllLabels());
	}
}
