package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionAdapter;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceCommentsSection extends JavaDocCommentsSection {

	public ChoiceCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	public void refresh(){
		getJavaDocText().setText(JavaDocAnalyser.getJavaDoc((ChoiceNode)getTarget()));
	}

	@Override
	protected SelectionAdapter getExportAdapter() {
		return null;
	}

	@Override
	protected SelectionAdapter getImportAdapter() {
		return null;
	}

}
