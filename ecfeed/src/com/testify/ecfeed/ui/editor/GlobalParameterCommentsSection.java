package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionListener;

import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterCommentsSection extends AbstractParameterCommentsSection {

	private GlobalParameterInterface fTargetIf;

	public GlobalParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
		getExportButton().setText("Export type comments");
		getImportButton().setText("Import type comments");
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new GlobalParameterInterface(getUpdateContext());
		}
		return fTargetIf;
	}

	@Override
	protected SelectionListener createExportButtonSelectionListener(){
		return new ExportFullTypeSelectionAdapter();
	}

	@Override
	protected SelectionListener createImportButtonSelectionListener(){
		return new ImportFullTypeSelectionAdapter();
	}

}
