package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.GlobalParametersParentNode;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParametersParentInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.ModelNodesTransfer;
import com.testify.ecfeed.ui.modelif.ParametersParentInterface;

public class GlobalParametersViewer extends AbstractParametersViewer {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;

	private GlobalParametersParentInterface fParentIf;
	private GlobalParameterInterface fParameterIf;

	public GlobalParametersViewer(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, STYLE);
		getSection().setText("Global parameters");
		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
	}

	@Override
	protected ParametersParentInterface getParametersParentInterface() {
		return getGlobalParametersParentIf();
	}

	protected ParametersParentInterface getGlobalParametersParentIf() {
		if(fParentIf == null){
			fParentIf = new GlobalParametersParentInterface(this);
		}
		return fParentIf;
	}

	public void setInput(GlobalParametersParentNode input){
		fParentIf.setTarget(input);
		super.setInput(input);
	}

	@Override
	protected AbstractParameterInterface getParameterInterface() {
		if(fParameterIf == null){
			fParameterIf = new GlobalParameterInterface(this);
		}
		return fParameterIf;
	}

}
