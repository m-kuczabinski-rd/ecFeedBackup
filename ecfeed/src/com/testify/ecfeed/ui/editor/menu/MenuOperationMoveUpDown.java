package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public class MenuOperationMoveUpDown extends ModelModyfingOperation {

	private boolean fUp;
	
	public MenuOperationMoveUpDown(List<GenericNode> selected, boolean up,
			ModelOperationManager operationManager, AbstractFormPart source,
			IModelUpdateListener updateListener) {
		super(up ? "Move up" : "Move down", selected, operationManager, source, updateListener);
		fUp = up;
	}

	@Override
	public Object execute() {
		SelectionInterface selectionIf = new SelectionInterface(getOperationManager());
		selectionIf.setTarget(getSelectedNodes());
		selectionIf.moveUpDown(fUp, getSource(), getUpdateListener());
		return null;
	}

	@Override
	public boolean isEnabled() {
		return getSelectedNodes().size() == 1;
	}

}
