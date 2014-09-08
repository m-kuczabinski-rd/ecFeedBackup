package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class MenuOperationDelete extends ModelModyfingOperation {

	private RootInterface fRootIf;
	
	public MenuOperationDelete(List<GenericNode> selected,
			ModelOperationManager operationManager, AbstractFormPart source,
			IModelUpdateListener updateListener) {
		super("Delete", selected, operationManager, source, updateListener);
		fRootIf = new RootInterface(operationManager);
	}

	@Override
	public Object execute() {
		if(getSelectedNodes().size() > 0){
			fRootIf.setTarget(getSelectedNodes().get(0).getRoot());
		}
		return fRootIf.removeNodes(getSelectedNodes(), getSource(), getUpdateListener());
	}

	@Override
	public boolean isEnabled() {
		return getSelectedNodes().size() > 0;
	}

}
