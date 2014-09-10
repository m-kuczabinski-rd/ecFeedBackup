package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public class MenuOperationDelete extends ModelModyfingOperation {

	private SelectionInterface fSelectionIf;
	
	public MenuOperationDelete(List<GenericNode> selected,
			ModelOperationManager operationManager, AbstractFormPart source,
			IModelUpdateListener updateListener) {
		super("&Delete", selected, operationManager, source, updateListener);
		fSelectionIf = new SelectionInterface(operationManager);
	}

	@Override
	public Object execute() {
		fSelectionIf.setTarget(getSelectedNodes());
		return fSelectionIf.delete(getSource(), getUpdateListener());
	}

	@Override
	public boolean isEnabled() {
		return getSelectedNodes().size() > 0;
	}

}
