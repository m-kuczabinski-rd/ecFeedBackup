package com.testify.ecfeed.ui.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class GenericNodeInterface {

	private ModelOperationManager fOperationManager;
	
	public GenericNodeInterface(ModelOperationManager modelOperationManager) {
		fOperationManager = modelOperationManager;
	}

	protected boolean execute(IModelOperation operation, BasicSection source, IModelUpdateListener updateListener, String errorMessageTitle){
		try{
			fOperationManager.execute(operation);
			if(updateListener != null){
				updateListener.modelUpdated(source);
			}
			return true;
		}catch(ModelIfException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					errorMessageTitle, 
					e.getMessage());
		}
		return false;
	}
}
