package com.testify.ecfeed.ui.modelif;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.ImplementationStatusResolver;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.ui.common.LoaderProvider;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class GenericNodeInterface {

	private ModelOperationManager fOperationManager;
	private ModelClassLoader fLoader;
	private ImplementationStatusResolver fStatusResolver;
	
	public GenericNodeInterface(ModelOperationManager modelOperationManager) {
		fOperationManager = modelOperationManager;
		fLoader = LoaderProvider.getLoader(false, null);
		fStatusResolver = new ImplementationStatusResolver(fLoader);
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
	
	public ImplementationStatus implementationStatus(IGenericNode node){
		return fStatusResolver.getImplementationStatus(node);
	}
}
