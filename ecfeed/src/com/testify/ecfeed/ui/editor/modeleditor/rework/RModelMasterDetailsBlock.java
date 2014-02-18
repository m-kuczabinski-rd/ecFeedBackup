package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.editor.EcMultiPageEditor;

public class RModelMasterDetailsBlock extends MasterDetailsBlock implements IModelSelectionListener{

	private static final int MASTER_SECTION_STYLE = Section.EXPANDED | Section.TITLE_BAR;

	private EcMultiPageEditor fEditor;
	private ModelMasterSection fMasterSection;

	public RModelMasterDetailsBlock(EcMultiPageEditor editor) {
		fEditor = editor;
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		fMasterSection = new ModelMasterSection(parent, toolkit, fEditor, MASTER_SECTION_STYLE);
		fMasterSection.addModelSelectionChangedListener(this);
		fMasterSection.setInput(fEditor);
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(RootNode.class, new ModelDetailsPage(fMasterSection));
//		detailsPart.registerPage(RootNode.class, new RootNodeDetailsPage(new ModelMasterDetailsBlock(fEditor, fEditor.getModel())));
		
		selectNode(fEditor.getModel());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}
	
	void selectNode(IGenericNode node){
		fMasterSection.selectElement(node);
	}

	@Override
	public void modelSelectionChanged(ISelection newSelection) {
		detailsPart.selectionChanged(fMasterSection, newSelection);
	}

}
