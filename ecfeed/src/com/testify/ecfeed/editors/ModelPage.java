package com.testify.ecfeed.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.testify.ecfeed.model.RootNode;

public class ModelPage extends FormPage {
	
	private final static String ID = "model";
	private final static String TITLE = "model";
	
	private ModelMasterDetailsBlock fBlock;
	private RootNode fModel;
	private EcMultiPageEditor fEditor;
	
	/**
	 * Create the form page.
	 * @param rootNode 
	 * @param id
	 * @param title
	 */
	public ModelPage(EcMultiPageEditor editor, RootNode model) {
		super(editor, ID, TITLE);
		fModel = model;
		fEditor = editor;
		fBlock = new ModelMasterDetailsBlock(fEditor, fModel);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Equivalence class model");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);

		fBlock.createContent(managedForm);

	}
}
