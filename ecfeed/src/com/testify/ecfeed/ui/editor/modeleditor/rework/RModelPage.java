package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.editor.EcMultiPageEditor;

public class RModelPage extends FormPage {
	private static final String ID = "com.testify.ecfeed.pages.refactored";
	private static final String TITLE = "refactored";
	private RModelMasterDetailsBlock fBlock;

	public RModelPage(EcMultiPageEditor editor, RootNode model) {
		super(editor, ID, TITLE);
		fBlock = new RModelMasterDetailsBlock(editor);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		fBlock.createContent(managedForm);
	}
}
