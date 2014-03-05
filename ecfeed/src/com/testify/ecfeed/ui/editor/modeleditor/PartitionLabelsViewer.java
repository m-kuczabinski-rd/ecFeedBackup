package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.dialogs.MessageDialog;
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
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;

public class PartitionLabelsViewer extends CheckboxTableViewerSection {
	
	private static final int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private PartitionNode fSelectedPartition;

	private class AddLabelAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			String newLabel = Constants.DEFAULT_LABEL;
			int i = 1;
			while(fSelectedPartition.getAllLabels().contains(newLabel)){
				newLabel = Constants.DEFAULT_LABEL + "(" + i + ")";
				i++;
			}
			if(fSelectedPartition.addLabel(newLabel) == false){
				MessageDialog.openError(getActiveShell(),  
						Messages.DIALOG_CANNOT_ADD_LABEL_TITLE, 
						Messages.DIALOG_CANNOT_ADD_LABEL_MESSAGE);
			};
			modelUpdated();
		}
	}
	
	private class RemoveLabelsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			for(Object element : getCheckedElements()){
				String label = (String)element;
				if(fSelectedPartition.removeLabel(label) == false){
					MessageDialog.openError(getActiveShell(), 
							Messages.DIALOG_CANNOT_REMOVE_LABEL_TITLE, 
							Messages.DIALOG_CANNOT_REMOVE_LABEL_MESSAGE(label));
				}
				modelUpdated();
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
			return !fSelectedPartition.getInheritedLabels().contains((String)element);
		}

		@Override
		protected Object getValue(Object element) {
			return (String)element;
		}

		@Override
		protected void setValue(Object element, Object value) {
			String oldLabel = (String)element;
			String newLabel = (String)value;
			if(fSelectedPartition.getAllLabels().contains(newLabel) == false){
				fSelectedPartition.removeLabel(oldLabel);
				fSelectedPartition.addLabel(newLabel);
				modelUpdated();
			}
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
				if(fSelectedPartition.getInheritedLabels().contains(label)){
					return fColorManager.getColor(ColorConstants.INHERITED_LABEL_FOREGROUND);
				}
			}
			return null;
		}

		@Override
		public Font getFont(Object element){
			if(element instanceof String){
				String label = (String)element;
				if(fSelectedPartition.getInheritedLabels().contains(label)){
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
			if(fSelectedPartition.getInheritedLabels().contains(label)){
				getCheckboxViewer().setChecked(label, false);
			}
		}
	}

	public PartitionLabelsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);

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
		fSelectedPartition = partition;
		super.setInput(partition.getAllLabels());
	}
	
	
}
