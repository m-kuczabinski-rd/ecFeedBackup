package com.testify.ecfeed.ui.editor.modeleditor.rework;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.RootNode;

public abstract class BasicDetailsPage implements IDetailsPage, IModelUpdateListener{

	private ModelMasterSection fMasterSection;
	private Section fMainSection;
	private Composite fMainComposite;
	private IManagedForm fManagedForm;
	private List<IFormPart> fForms;
	
	private static final int MAIN_SECTION_STYLE = Section.EXPANDED | Section.TITLE_BAR;

	public BasicDetailsPage(ModelMasterSection masterSection){
		fMasterSection = masterSection;
		fForms = new ArrayList<IFormPart>();
	}
	
	@Override
	public void initialize(IManagedForm form) {
		fManagedForm = form;
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = getToolkit().createSection(parent, MAIN_SECTION_STYLE);

		getToolkit().adapt(getMainSection());
		
		fMainComposite = getToolkit().createComposite(getMainSection(), SWT.NONE);
		fMainComposite.setLayout(new GridLayout(1, false));
		getToolkit().adapt(fMainComposite);
		getMainSection().setClient(fMainComposite);
	}
	
	@Override
	public void refresh(){
		for(IFormPart form : fForms){
			form.refresh();
		}
	}

	@Override
	public void dispose() {
		for(IFormPart form : fForms){
			form.dispose();
		}
	}

	@Override
	public boolean isDirty() {
		for(IFormPart form : fForms){
			if(form.isDirty()){
				return true;
			};
		}
		return false;
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		for(IFormPart form : fForms){
			form.commit(onSave);
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isStale() {
		for(IFormPart form : fForms){
			if(form.isStale()){
				return true;
			};
		}
		return false;
	}

	public RootNode getModel(){
		return getMasterSection().getModel();
	}

	public FormToolkit getToolkit(){
		return fManagedForm.getToolkit();
	}

	protected Section getMainSection(){
		return fMainSection;
	}

	protected ModelMasterSection getMasterSection(){
		return fMasterSection;
	}

	protected void addForm(IFormPart form){
		fForms.add(form);
		form.initialize(getManagedForm());
	}
	
	protected Object getSelectedElement(){
		return fMasterSection.getSelectedElement();
	}
	
	protected IManagedForm getManagedForm(){
		return fManagedForm;
	}

	protected Composite getMainComposite(){
		return fMainComposite;
	}
	
	public void modelUpdated(AbstractFormPart source){
		if(source != null){
			source.markDirty();
		}
		else if(getMasterSection() != null){
			getMasterSection().markDirty();
		}
		if(getMasterSection() != null){
			getMasterSection().refresh();
		}
		refresh();
	}
	
	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}
}
