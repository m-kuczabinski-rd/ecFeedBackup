package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterDetailsPage extends AbstractParameterDetailsPage {

	private GlobalParameterInterface fParameterIf;

	public GlobalParameterDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		getParameterIf();
	}

	@Override
	protected AbstractParameterInterface getParameterIf() {
		if(fParameterIf == null){
			fParameterIf = new GlobalParameterInterface(this);
		}
		return fParameterIf;
	}

}
