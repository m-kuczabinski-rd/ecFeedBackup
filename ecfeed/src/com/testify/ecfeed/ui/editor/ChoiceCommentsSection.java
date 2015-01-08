package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceCommentsSection extends JavaDocCommentsSection {

	public ChoiceCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	public void refresh(){
		getJavaDocText().setText(JavaDocAnalyser.importJavadoc(getTarget()));
	}
}
