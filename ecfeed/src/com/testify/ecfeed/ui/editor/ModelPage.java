package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.editor.EcMultiPageEditor;

public class ModelPage extends FormPage {
	private static final String ID = "com.testify.ecfeed.pages.refactored";
	private static final String TITLE = "refactored";

	private ModelMasterDetailsBlock fBlock;
	private EcMultiPageEditor fEditor;

	public ModelPage(EcMultiPageEditor editor) {
		super(editor, ID, TITLE);
		fEditor = editor;
		fBlock = new ModelMasterDetailsBlock(this);
	}

	public void commitMasterPart(boolean onSave){
		if(fBlock.getMasterSection() != null && fBlock.getMasterSection().isDirty()){
			fBlock.getMasterSection().commit(onSave);
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		fBlock.createContent(managedForm);
	}
	
	@Override
	public boolean isDirty(){
		boolean masterSectionDirty = fBlock.getMasterSection() == null ? false : fBlock.getMasterSection().isDirty();
		return super.isDirty() || masterSectionDirty;
	}
	
	public RootNode getModel(){
		return fEditor.getModel();
	}
}
