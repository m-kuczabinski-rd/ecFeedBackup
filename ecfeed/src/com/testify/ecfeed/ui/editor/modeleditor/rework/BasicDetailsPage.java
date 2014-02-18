package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.RootNode;

public abstract class BasicDetailsPage implements IDetailsPage {

	ModelMasterSection fMasterSection;
	BasicSection fMainSection;
	FormToolkit fToolkit;
	
	public BasicDetailsPage(ModelMasterSection masterSection){
		fMasterSection = masterSection;
	}
	
	@Override
	public void initialize(IManagedForm form) {
		fToolkit = form.getToolkit();
	}

	@Override
	public void dispose() {
		fMainSection.dispose();
	}

	@Override
	public boolean isDirty() {
		return fMainSection.isDirty();
	}

	@Override
	public void commit(boolean onSave) {
		fMainSection.commit(onSave);
	}

	@Override
	public boolean setFormInput(Object input) {
		return fMainSection.setFormInput(input);
	}

	@Override
	public void setFocus() {
		fMainSection.setFocus();
	}

	@Override
	public boolean isStale() {
		return fMainSection.isStale();
	}

	@Override
	public void refresh() {
		fMainSection.refresh();
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		refresh();
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = createMainSection(parent);
	}
	
	public FormToolkit getToolkit(){
		return fToolkit;
	}

	public BasicSection getMainSection(){
		return fMainSection;
	}
	
	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}
	
	public RootNode getModel(){
		return getMasterSection().getModel();
	}
	
	protected Object getSelectedElement(){
		return fMasterSection.getSelectedElement();
	}
	
	abstract protected BasicSection createMainSection(Composite parent);

}
