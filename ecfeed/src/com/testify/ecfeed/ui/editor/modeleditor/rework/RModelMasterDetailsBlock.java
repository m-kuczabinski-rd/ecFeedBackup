package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.RootNode;

public class RModelMasterDetailsBlock extends MasterDetailsBlock implements IModelSelectionListener{

	private ModelMasterSection fMasterSection;
	private RModelPage fPage;

	public RModelMasterDetailsBlock(RModelPage modelPage) {
		fPage = modelPage;
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		fMasterSection = new ModelMasterSection(parent, toolkit);
		fMasterSection.initialize(managedForm);
		fMasterSection.addModelSelectionChangedListener(this);
		fMasterSection.setModel(getModel());
	}

	private RootNode getModel() {
		return fPage.getModel();
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(RootNode.class, new ModelDetailsPage(fMasterSection));
		detailsPart.registerPage(ClassNode.class, new ClassDetailsPage(fMasterSection));

		selectNode(getModel());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}
	
	public void selectNode(IGenericNode node){
		fMasterSection.selectElement(node);
	}

	@Override
	public void modelSelectionChanged(ISelection newSelection) {
		detailsPart.selectionChanged(fMasterSection, newSelection);
	}

	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}
}
