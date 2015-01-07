package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ClassCommentsSection extends JavaDocCommentsSection {

	private class ExportAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
		}
	}

	private class ImportAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
		}
	}

	public ClassCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected SelectionAdapter getExportAdapter() {
		return new ExportAdapter();
	}

	@Override
	protected SelectionAdapter getImportAdapter() {
		return new ImportAdapter();
	}

	@Override
	public void refresh(){
		super.refresh();
		getJavaDocText().setText(JavaDocAnalyser.getJavadoc((ClassNode)getTarget()));
	}

	public void setInput(ClassNode input){
		super.setInput(input);
		refresh();
	}

}
