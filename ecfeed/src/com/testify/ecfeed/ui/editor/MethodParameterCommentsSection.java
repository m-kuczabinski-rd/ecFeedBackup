package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterCommentsSection extends AbstractParameterCommentsSection {

	private MethodParameterInterface fTargetIf;

	public MethodParameterCommentsSection(ISectionContext sectionContext,
			IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new MethodParameterInterface(getUpdateContext());
		}
		return fTargetIf;
	}
}
