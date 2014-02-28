package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.RootNode;

public class ModelDetailsPage extends BasicDetailsPage {

	RootNode fModel;
	private ClassViewer fClassesSection;
	private ModelNameForm fNameForm;
	
	private class ModelNameForm extends TextForm {

		public ModelNameForm(Composite parent, FormToolkit toolkit) {
			super(parent, toolkit, "Model name:", "Change");
		}

		@Override
		protected void newText(String text) {
			renameModel(text);
		}
		
		@Override
		public void refresh(){
			super.refresh();
			if(fModel != null){
				setText(fModel.getName());
			}
		}

		private void renameModel(String text) {
			if(RootNode.validateModelName(text) && !fModel.getName().equals(text)){
				fModel.setName(text);
				modelUpdated(this);
			}
			else{
				setText(fModel.getName());
			}
		}

	}
	
	public ModelDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		getMainSection().setText("Model details");
		
		addForm(fNameForm = new ModelNameForm(getMainComposite(), getToolkit()));
		addForm(fClassesSection = new ClassViewer(this, getToolkit()));

		getToolkit().paintBordersFor(getMainComposite());
	}


	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fModel = (RootNode)getSelectedElement();
		fNameForm.setText(fModel.getName());
		fClassesSection.setInput(fModel);
	}
}
