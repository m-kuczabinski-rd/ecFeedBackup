package com.testify.ecfeed.ui.editor.menu;

import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public abstract class ModelModyfingOperation extends MenuOperation {
	
	
	private ModelOperationManager fOperationManager;
	private AbstractFormPart fSource;
	private IModelUpdateListener fUpdateListener;
	private List<GenericNode> fSelectedNodes;

	public ModelModyfingOperation(String name, List<GenericNode> selected, ModelOperationManager operationManager, AbstractFormPart source, IModelUpdateListener updateListener) {
		super(name);
		fOperationManager = operationManager;
		fSource = source;
		fUpdateListener = updateListener;
		fSelectedNodes = selected;
	}

	protected ModelOperationManager getOperationManager(){
		return fOperationManager;
	}
	
	protected AbstractFormPart getSource(){
		return fSource;
	}
	
	protected IModelUpdateListener getUpdateListener(){
		return fUpdateListener;
	}
	
	protected List<GenericNode> getSelectedNodes(){
		return fSelectedNodes;
	}
}
