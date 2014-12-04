package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Composite;

import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterDetailsPage extends AbstractParameterDetailsPage {

	private GlobalParameterInterface fParameterIf;
	private LinkingMethodsViewer fLinkingMethodsViewer;

	public GlobalParameterDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		getParameterIf();
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		addForm(fLinkingMethodsViewer = new LinkingMethodsViewer(this, this));
	}


	@Override
	protected AbstractParameterInterface getParameterIf() {
		if(fParameterIf == null){
			fParameterIf = new GlobalParameterInterface(this);
		}
		return fParameterIf;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof GlobalParameterNode){
			GlobalParameterNode parameter = (GlobalParameterNode)getSelectedElement();
			fParameterIf.setTarget(parameter);
			getMainSection().setText(parameter.getQualifiedName() + ": " + parameter.getType());
			fLinkingMethodsViewer.setInput(parameter);
		}
	}
}
