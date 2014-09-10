package com.testify.ecfeed.ui.modelif;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.operations.BulkOperation;

public class OperationExecuter {
	
	private ModelOperationManager fOperationManager;

	public OperationExecuter(ModelOperationManager operationManager){
		fOperationManager = operationManager;
	}
	
	protected boolean execute(IModelOperation operation, AbstractFormPart source, IModelUpdateListener updateListener, String errorMessageTitle){
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
	
	protected boolean execute(BulkOperation operation, AbstractFormPart source, IModelUpdateListener updateListener, String errorMessageTitle){
		try{
			fOperationManager.execute(operation);
		}catch(ModelIfException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					errorMessageTitle, 
					e.getMessage());
		}
		if(operation.modelUpdated()){
			updateListener.modelUpdated(source);
			return true;
		}
		return false;
	}
	
	protected ModelOperationManager getOperationManager(){
		return fOperationManager;
	}
}
