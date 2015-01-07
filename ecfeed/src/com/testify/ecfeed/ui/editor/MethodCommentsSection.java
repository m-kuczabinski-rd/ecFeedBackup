package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionAdapter;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class MethodCommentsSection extends JavaDocCommentsSection {

	public MethodCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected SelectionAdapter getExportAdapter() {
		return null;
	}

	@Override
	protected SelectionAdapter getImportAdapter() {
		return null;
	}

	@Override
	public void refresh(){
		super.refresh();
		getJavaDocText().setText(JavaDocAnalyser.getJavaDoc((MethodNode)getTarget()));
	}

	public void setInput(MethodNode input){
		super.setInput(input);
		refresh();
	}
}
