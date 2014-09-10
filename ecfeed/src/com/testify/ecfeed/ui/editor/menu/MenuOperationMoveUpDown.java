package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public class MenuOperationMoveUpDown extends ModelModyfingOperation {

	private boolean fUp;
	private SelectionInterface fSelectionIf;
	
	public MenuOperationMoveUpDown(List<GenericNode> selected, boolean up,
			ModelOperationManager operationManager, AbstractFormPart source,
			IModelUpdateListener updateListener) {
		super(up ? "Move &up" : "Move &down", selected, operationManager, source, updateListener);
		fUp = up;
		fSelectionIf = new SelectionInterface(getOperationManager());
		fSelectionIf.setTarget(selected);
	}

	@Override
	public Object execute() {
		fSelectionIf.moveUpDown(fUp, getSource(), getUpdateListener());
		return null;
	}

	@Override
	public boolean isEnabled() {
		return fSelectionIf.moveUpDownEnabed(fUp);
	}

}
