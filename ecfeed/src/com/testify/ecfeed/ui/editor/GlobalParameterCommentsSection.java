package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterCommentsSection extends
		AbstractParameterCommentsSection {

	private GlobalParameterInterface fTargetIf;

	public GlobalParameterCommentsSection(ISectionContext sectionContext,
			IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new GlobalParameterInterface(getUpdateContext());
		}
		return fTargetIf;
	}
}
