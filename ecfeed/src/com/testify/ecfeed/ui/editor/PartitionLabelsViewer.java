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
import java.util.List;

import org.eclipse.jface.action.Action;
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
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.editor.actions.CutAction;
import com.testify.ecfeed.ui.editor.actions.IActionProvider;
import com.testify.ecfeed.ui.editor.actions.ModelModyfyingAction;
import com.testify.ecfeed.ui.editor.actions.SelectAllAction;
import com.testify.ecfeed.ui.modelif.PartitionInterface;

public class PartitionLabelsViewer extends TableViewerSection {
	
	private static final int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private PartitionInterface fPartitionIf;

	private static class LabelClipboard{
		private static List<String> fLabels = new ArrayList<>();
		
		public static List<String> getContent(){
			return fLabels;
		}
	
		public static List<String> getContentCopy(){
			List<String> copy = new ArrayList<>();
			for(String label : fLabels){
				copy.add(new String(label));
			}
			return copy;
		}
		
		public static void setContent(List<String> labels){
			fLabels.clear();
			for(String label : labels){
				fLabels.add(new String(label));
			}
		}
	}

	private class LabelsViewerActionProvider implements IActionProvider{

		private class LabelCopyAction extends Action{
			@Override
			public boolean isEnabled(){
				return getSelectedLabels().size() > 0;
			}
			
			@Override
			public void run(){
				LabelClipboard.setContent(getSelectedLabels());
			}
		}
		
		private class LabelPasteAction extends ModelModyfyingAction{
			public LabelPasteAction() {
				super(getViewer(), PartitionLabelsViewer.this);
			}

			@Override
			public boolean isEnabled(){
				return LabelClipboard.getContent().size() > 0;
			}
			
			@Override
			public void run(){
				fPartitionIf.addLabels(LabelClipboard.getContentCopy(), PartitionLabelsViewer.this);
			}
		}
		
		@Override
		public Action getAction(String actionId) {
			if(actionId.equals(ActionFactory.COPY.getId())){
				return new LabelCopyAction();
			}
			if(actionId.equals(ActionFactory.CUT.getId())){
				return new CutAction(new LabelCopyAction(), new LabelDeleteAction());
			}
			if(actionId.equals(ActionFactory.DELETE.getId())){
				return new LabelDeleteAction();
			}
			if(actionId.equals(ActionFactory.PASTE.getId())){
				return new LabelPasteAction();
			}
			if(actionId.equals(ActionFactory.SELECT_ALL.getId())){
				return new SelectAllAction(getTableViewer());
			}

			return null;
		}
		
	}
	
	private class LabelDeleteAction extends ModelModyfyingAction{
		public LabelDeleteAction() {
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
	}

	private class AddLabelAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			String newLabel = fPartitionIf.addNewLabel(PartitionLabelsViewer.this);
			if(newLabel != null){
				getTableViewer().editElement(newLabel, 0);
			}
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
		addButton("Remove selected", new ActionSelectionAdapter(new LabelDeleteAction()));

		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
		addKeyListener(SWT.DEL, new LabelDeleteAction());
		
		setActionProvider(new LabelsViewerActionProvider());
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

	@SuppressWarnings("unchecked")
	private List<String> getSelectedLabels(){
		return getSelection().toList();
	}
}
