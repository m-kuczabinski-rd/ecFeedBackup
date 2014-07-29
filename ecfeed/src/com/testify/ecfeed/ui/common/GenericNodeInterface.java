package com.testify.ecfeed.ui.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.gal.GalException;
import com.testify.ecfeed.gal.IModelOperation;
import com.testify.ecfeed.gal.ModelOperationManager;
import com.testify.ecfeed.gal.NodeAbstractionLayer;
import com.testify.ecfeed.ui.editor.BasicSection;

public class GenericNodeInterface extends NodeAbstractionLayer {

	public GenericNodeInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	protected void execute(IModelOperation operation, BasicSection source, String errorMessageTitle){
		try{
			super.execute(operation);
			if(source != null){
				source.getUpdateListener().modelUpdated(source);
			}
		}catch(GalException e){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					errorMessageTitle, 
					e.getMessage());

		}
	}
}
