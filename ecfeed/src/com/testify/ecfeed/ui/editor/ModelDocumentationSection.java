package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ModelDocumentationSection extends TabFolderSection {

	public Text fModelDocText;

	public ModelDocumentationSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, "Model documentation");
	}

	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		fModelDocText = addTextTab("Model doc");
		return client;
	}

	public void setInput(RootNode node){
		super.setInput(node);
		fModelDocText.setText(node.getDescription());
	}

}
