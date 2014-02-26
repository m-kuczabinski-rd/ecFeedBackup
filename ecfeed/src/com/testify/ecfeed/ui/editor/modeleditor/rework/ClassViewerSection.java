package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ModelUtils;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;

public class ClassViewerSection extends CheckboxTableViewerSection {
	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;

	private RootNode fModel;
	private BasicDetailsPage fPage;

	private class AddClassAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			IType selectedClass = selectClass();
			
			if(fModel != null){
				addClass(selectedClass, fModel);
			}
		}
		
		private void addClass(IType selectedClass, RootNode model){
			ClassNode classNode = ModelUtils.generateClassModel(selectedClass);
			if(model.getClassModel(classNode.getQualifiedName()) == null){
				model.addClass(classNode);
				fPage.modelUpdated(ClassViewerSection.this);
			}
			else{
				MessageDialog.openError(getActiveShell(), 
						Messages.DIALOG_CLASS_EXISTS_TITLE,
						Messages.DIALOG_CLASS_EXISTS_MESSAGE);
			}
		}

		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(getActiveShell());
			
			if (dialog.open() == IDialogConstants.OK_ID) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}

	private class RemoveSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_CLASSES_TITLE, 
					Messages.DIALOG_REMOVE_CLASSES_MESSAGE)){
				removeClasses(getCheckboxViewer().getCheckedElements());
			}
		}

		private void removeClasses(Object[] checkedElements) {
			if(fModel != null){
				for(Object element : checkedElements){
					if(element instanceof ClassNode){
						fModel.removeClass((ClassNode)element);
					}
				}
				fPage.modelUpdated(ClassViewerSection.this);
			}
		}
	}

	public ClassViewerSection(Composite parent, FormToolkit toolkit, BasicDetailsPage page) {
		super(parent, toolkit, STYLE, ViewerSection.BUTTONS_BELOW);
		fPage = page;
		
		setText("Classes");

		getTable().setHeaderVisible(true);
		getTable().setLinesVisible(true);

		addButton("Add test class..", new AddClassAdapter());
		addButton("Remove selected", new RemoveSelectedAdapter());
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(page.getMasterSection()));
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
	
	public void setInput(RootNode model){
		fModel = model;
		super.setInput(model.getClasses());
		
		refresh();
	}
	
	@Override
	public boolean isStale(){
		if(fModel != null){
			return fModel.getClasses().equals(getInput());
		}
		return false;
	}
}
