package com.testify.ecfeed.ui.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.BasicSection;

public class GenericNodeInterface {

	private ModelOperationManager fOperationManager;
	
	public GenericNodeInterface(ModelOperationManager modelOperationManager) {
		fOperationManager = modelOperationManager;
	}

	protected void execute(IModelOperation operation, BasicSection source, String errorMessageTitle){
		try{
			fOperationManager.execute(operation);
			if(source != null){
				source.getUpdateListener().modelUpdated(source);
			}
		}catch(ModelIfException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					errorMessageTitle, 
					e.getMessage());

		}
	}
}
