package com.testify.ecfeed.ui.modelif;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.operations.BulkOperation;

public class OperationExecuter {
	
	protected boolean execute(IModelOperation operation, IModelUpdateContext updateContext, String errorMessageTitle){
		try{
			updateContext.getOperationManager().execute(operation);
			if(updateContext.getUpdateListener() != null){
				updateContext.getUpdateListener().modelUpdated(updateContext.getSourceForm());
			}
			return true;
		}catch(ModelIfException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					errorMessageTitle, 
					e.getMessage());
		}
		return false;
	}
	
	protected boolean execute(BulkOperation operation, IModelUpdateContext updateContext, String errorMessageTitle){
		try{
			updateContext.getOperationManager().execute(operation);
		}catch(ModelIfException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					errorMessageTitle, 
					e.getMessage());
		}
		if(operation.modelUpdated()){
			updateContext.getUpdateListener().modelUpdated(updateContext.getSourceForm());
			return true;
		}
		return false;
	}
//	protected ModelOperationManager getOperationManager(){
//		return fOperationManager;
//	}
}
