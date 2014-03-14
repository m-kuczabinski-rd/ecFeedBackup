package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Composite;

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
	public void refresh(){
		if(getSelectedElement() instanceof CategoryNode){
			fSelectedCategory = (CategoryNode)getSelectedElement();
		}
		if(fSelectedCategory != null){
			getMainSection().setText(fSelectedCategory.toString());
			fPartitionsViewer.setInput(fSelectedCategory);
		}
	}
}
