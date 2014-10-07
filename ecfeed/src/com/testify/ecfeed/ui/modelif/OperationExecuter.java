package com.testify.ecfeed.ui.modelif;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;

public class OperationExecuter {
	
	private IModelUpdateContext fUpdateContext;
	private IOperationHistory fOperationHistory;
	
	private class UndoableOperation extends AbstractOperation{
		
		private IModelOperation fOperation;
		private String fErrorMessageTitle;
		
		
		public UndoableOperation(IModelOperation operation, IUndoContext context, String errorMessageTitle){
			super(operation.getName());
			fOperation = operation;
			fErrorMessageTitle = errorMessageTitle;
			addContext(context);
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return executeOperation(fOperation, monitor, info);
		}


		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return executeOperation(fOperation, monitor, info);
		}


		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return executeOperation(fOperation.reverseOperation(), monitor, info);
		}

		private IStatus executeOperation(IModelOperation operation, IProgressMonitor monitor, IAdaptable info){
			try {
				operation.execute();
				if(fUpdateContext.getUpdateListener() != null){
					fUpdateContext.getUpdateListener().modelUpdated(getSourceForm());
				}
				return Status.OK_STATUS;
			} catch (ModelOperationException e) {
				if(operation.modelUpdated()){
					fUpdateContext.getUpdateListener().modelUpdated(getSourceForm());
				}
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						fErrorMessageTitle, 
						e.getMessage());
				return Status.CANCEL_STATUS;
			}
		}
	}

	public OperationExecuter(IModelUpdateContext updateContext){
		fOperationHistory = OperationHistoryFactory.getOperationHistory();
		fUpdateContext = updateContext;
	}
	
	protected boolean execute(IModelOperation operation, String errorMessageTitle){
		try{
			UndoableOperation action = new UndoableOperation(operation, getUpdateContext().getUndoContext(), errorMessageTitle);
			fOperationHistory.execute(action, null, null);
			return true;
		} catch (ExecutionException e) {}
		return false;
	}
	
//	protected boolean execute(BulkOperation operation, String errorMessageTitle){
//		try{
//			getUpdateContext().getOperationManager().execute(operation);
//		}catch(ModelIfException e){
//			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
//					errorMessageTitle, 
//					e.getMessage());
//		}
//		if(operation.modelUpdated()){
//			getUpdateContext().getUpdateListener().modelUpdated(getSourceForm());
//			return true;
//		}
//		return false;
//	}
	
	protected IModelUpdateContext getUpdateContext(){
		return fUpdateContext;
	}
	
	private AbstractFormPart getSourceForm(){
		return getUpdateContext().getSourceForm();
	}
}

