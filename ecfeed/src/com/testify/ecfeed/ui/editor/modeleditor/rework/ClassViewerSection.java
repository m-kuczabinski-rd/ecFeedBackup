package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.ClassNode;

public class ClassViewerSection extends CheckboxTableViewerSection {
	
	private class AddClassAdapter extends SelectionAdapter{
		
	}
	
	private class RemoveSelectedAdapter extends SelectionAdapter{
		
	}
	
	public ClassViewerSection(Composite parent, FormToolkit toolkit, int style) {
		super(parent, toolkit, style, ViewerSection.BUTTONS_BELOW);
		getTable().setHeaderVisible(true);
		getTable().setLinesVisible(true);

		addButton("Add test class..", new AddClassAdapter());
		addButton("Remove selected", new RemoveSelectedAdapter());
	}
	
	@Override
	protected void createTableColumns(){
		addColumn("Class", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getLocalName();
			}
		});
		addColumn("Qualified name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getQualifiedName();
			}
		});
	}
	
	@Override
	protected void createViewerLabel(Composite viewerComposite){
		getToolkit().createLabel(viewerComposite, "Classes");
	}
}
