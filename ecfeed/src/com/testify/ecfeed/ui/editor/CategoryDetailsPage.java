package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;

import com.testify.ecfeed.model.CategoryNode;

public class CategoryDetailsPage extends BasicDetailsPage {

	private CategoryNode fSelectedCategory;
	private CategoryChildrenViewer fPartitionsViewer;

	public CategoryDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		addForm(fPartitionsViewer = new CategoryChildrenViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof CategoryNode){
			fSelectedCategory = (CategoryNode)getSelectedElement();
		}
		refresh();
	}
	
	@Override
	public void refresh(){
		if(fSelectedCategory != null){
			getMainSection().setText(fSelectedCategory.toString());
			fPartitionsViewer.setInput(fSelectedCategory);
		}
		super.refresh();
	}

}
