package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.modelif.Messages;
import com.testify.ecfeed.ui.modelif.NodeClipboard;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;

public class MenuOperationPaste extends ModelModyfingOperation {
	
	private GenericNodeInterface fTargetIf;

	public MenuOperationPaste(List<GenericNode> selected, ModelOperationManager operationManager, AbstractFormPart source,
			IModelUpdateListener updateListener) {
		super("Paste", selected, operationManager, source, updateListener);
		if(selected.size() == 1){
			GenericNode node = selected.get(0);
			fTargetIf = new NodeInterfaceFactory(operationManager).getNodeInterface(node);
		}
	}

	@Override
	public Object execute() {
		return fTargetIf.addChildren(NodeClipboard.getContentCopy(), getSource(), getUpdateListener(), Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE);
	}

	@Override
	public boolean isEnabled() {
		if(getSelectedNodes().size() != 1) return false;
		return fTargetIf.canAddChildren(NodeClipboard.getContent());
	}

}
