package com.testify.ecfeed.ui.editor.menu;

import java.util.ArrayList;
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
	private List<GenericNode> fChildren;

	public MenuOperationPaste(List<GenericNode> selected, ModelOperationManager operationManager, AbstractFormPart source,
			IModelUpdateListener updateListener) {
		super("Paste", selected, operationManager, source, updateListener);
		if(selected.size() == 1){
			GenericNode node = selected.get(0);
			fTargetIf = new NodeInterfaceFactory(operationManager).getNodeInterface(node);
			fChildren = new ArrayList<>(NodeClipboard.getContentCopy());
		}
	}

	@Override
	public Object execute() {
		if(fTargetIf.addChildren(fChildren, getSource(), getUpdateListener(), Messages.DIALOG_ADD_CHILDREN_PROBLEM_TITLE)){
			return(fChildren.get(fChildren.size() - 1)); 
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		if(getSelectedNodes().size() != 1) return false;
		return fTargetIf.pasteEnabled(NodeClipboard.getContent());
	}

}
