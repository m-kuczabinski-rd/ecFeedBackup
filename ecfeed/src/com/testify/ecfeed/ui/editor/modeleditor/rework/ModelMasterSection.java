package com.testify.ecfeed.ui.editor.modeleditor.rework;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.editor.EcMultiPageEditor;
import com.testify.ecfeed.ui.editor.modeleditor.ModelContentProvider;
import com.testify.ecfeed.ui.editor.modeleditor.ModelLabelProvider;

public class ModelMasterSection extends TreeViewerSection {
	private static final int AUTO_EXPAND_LEVEL = 3;

	private List<IModelSelectionListener> fModelSelectionListeners;
	private Button fMoveUpButton;
	private Button fMoveDownButton;
	
	private EcMultiPageEditor fEditor;

	private class MoveUpAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(true);
		}
	}

	private class MoveDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(false);
		}
	}
	
	private class ModelSelectionListener implements ISelectionChangedListener{
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			IGenericNode selectedNode = (IGenericNode)selection.getFirstElement();
			enableSortButtons(selectedNode);
			notifyModelSelectionListeners(selection);
		}

		private void enableSortButtons(IGenericNode selectedElement) {
			boolean enabled = true;
			if((selectedElement instanceof RootNode) || (selectedElement instanceof CategoryNode)){
				enabled = false;
			}
			fMoveUpButton.setEnabled(enabled);
			fMoveDownButton.setEnabled(enabled);
		}
	}

	public ModelMasterSection(Composite parent, FormToolkit toolkit, EcMultiPageEditor editor, int style) {
		super(parent, toolkit, style, ViewerSection.BUTTONS_BELOW);
		fModelSelectionListeners = new ArrayList<IModelSelectionListener>();
		fEditor = editor;
	}
	
	public void addModelSelectionChangedListener(IModelSelectionListener listener){
		fModelSelectionListeners.add(listener);
	}
	
	@Override
	protected void createContent(){
		super.createContent();
		getSection().setText("Structure");
		fMoveUpButton = addButton("Move Up", new MoveUpAdapter());
		fMoveDownButton = addButton("Move Down", new MoveDownAdapter());
		getTreeViewer().setLabelProvider(new ModelLabelProvider());
		getTreeViewer().setContentProvider(new ModelContentProvider());
		getTreeViewer().setAutoExpandLevel(AUTO_EXPAND_LEVEL);
		addSelectionChangedListener(new ModelSelectionListener());
	}

	private void notifyModelSelectionListeners(ISelection newSelection) {
		for(IModelSelectionListener listener : fModelSelectionListeners){
			listener.modelSelectionChanged(newSelection);
		}
	}

	private void moveSelectedItem(boolean moveUp) {
		if(selectedNode() == null || selectedNode().getParent() == null) return;

		selectedNode().getParent().moveChild(selectedNode(), moveUp);
	}

	private IGenericNode selectedNode() {
		Object selectedElement = getSelectedElement();
		if(selectedElement instanceof IGenericNode){
			return (IGenericNode)selectedElement;
		}
		return null;
	}
	
	public void updateModel(){
		fEditor.updateModel();
	}
	
	public RootNode getModel(){
		return fEditor.getModel();
	}
}
