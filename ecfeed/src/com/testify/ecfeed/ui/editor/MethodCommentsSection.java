package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class MethodCommentsSection extends JavaDocCommentsSection {

	public MethodCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	public void refresh(){
		super.refresh();
		getJavaDocText().setText(JavaDocAnalyser.importJavadoc(getTarget()));
	}

	public void setInput(MethodNode input){
		super.setInput(input);
		refresh();
	}
}
