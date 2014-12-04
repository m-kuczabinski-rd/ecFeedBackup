package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ParametersParentNode;
import com.testify.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.editor.actions.DeleteAction;
import com.testify.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.ModelNodesTransfer;
import com.testify.ecfeed.ui.modelif.ParameterInterface;
import com.testify.ecfeed.ui.modelif.ParametersParentInterface;

public abstract class AbstractParametersViewer extends TableViewerSection {

	private TableViewerColumn fNameColumn;
	private TableViewerColumn fTypeColumn;

	private AbstractParameterInterface fParameterIf;
	private ParametersParentInterface fParentIf;

	private class ParameterTypeEditingSupport extends EditingSupport {

		private ComboBoxCellEditor fCellEditor;

		public ParameterTypeEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if(fCellEditor == null){
				fCellEditor = new ComboBoxCellEditor(getTable(), ParameterInterface.supportedPrimitiveTypes());
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_KEY_ACTIVATION);
			}
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			AbstractParameterNode node = (AbstractParameterNode)element;
			String [] items = fCellEditor.getItems();
			ArrayList<String> newItems = new ArrayList<String>();

			for (int i = 0; i < items.length; ++i) {
				newItems.add(items[i]);
				if (items[i].equals(node.getType())) {
					return i;
				}
			}

			newItems.add(node.getType());
			fCellEditor.setItems(newItems.toArray(items));
			return (newItems.size() - 1);
		}

		@Override
		protected void setValue(Object element, Object value) {
			AbstractParameterNode node = (AbstractParameterNode)element;
			String newType = null;
			int index = (int)value;

			if (index >= 0) {
				newType = fCellEditor.getItems()[index];
			} else {
				newType = ((CCombo)fCellEditor.getControl()).getText();
			}
			fParameterIf.setTarget(node);
			fParameterIf.setType(newType);

			fCellEditor.setFocus();
		}
	}


	private class ParameterNameEditingSupport extends EditingSupport {

		private TextCellEditor fNameCellEditor;

		public ParameterNameEditingSupport() {
			super(getTableViewer());
			fNameCellEditor = new TextCellEditor(getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fNameCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((AbstractParameterNode)element).getName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			fParameterIf.setTarget((AbstractParameterNode)element);
			fParameterIf.setName((String)value);
		}
	}

	private class AddNewParameterAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			AbstractParameterNode addedParameter = fParentIf.addNewParameter();
			if(addedParameter != null){
				selectElement(addedParameter);
				fNameColumn.getViewer().editElement(addedParameter, 0);
			}
		}
	}

	public AbstractParametersViewer(ISectionContext sectionContext, IModelUpdateContext updateContext, int STYLE) {
		super(sectionContext, updateContext, STYLE);
		fParameterIf = new ParameterInterface(this);
		fParentIf = getParametersParentInterface();

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		addButton("New parameter", new AddNewParameterAdapter());
		addButton("Remove selected", new ActionSelectionAdapter(new DeleteAction(getViewer(), this)));

		fNameColumn.setEditingSupport(new ParameterNameEditingSupport());
		fTypeColumn.setEditingSupport(new ParameterTypeEditingSupport());

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));
		setActionProvider(new ModelViewerActionProvider(getTableViewer(), this));
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
		getViewer().addDropSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDropListener(getViewer(), this));
	}

	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 100, new NodeNameColumnLabelProvider());
		fTypeColumn = addColumn("Type", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((AbstractParameterNode)element).getType();
			}
		});
	}

	public void setInput(ParametersParentNode parent){
		fParentIf.setTarget(parent);
		super.setInput(parent.getParameters());
	}

	protected abstract ParametersParentInterface getParametersParentInterface();
}
