package com.testify.ecfeed.ui.modelif;

import java.util.List;

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

import com.testify.ecfeed.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;

public class OperationExecuter {
	
	private IModelUpdateContext fUpdateContext;
	private List<IModelUpdateListener> fUpdateListeners;
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
				CachedImplementationStatusResolver.clearCache();
				updateListeners();
				return Status.OK_STATUS;
			} catch (ModelOperationException e) {
				updateListeners();
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						fErrorMessageTitle, 
						e.getMessage());
				return Status.CANCEL_STATUS;
			}
		}

		private void updateListeners() {
			if(fUpdateListeners != null){
				for(IModelUpdateListener listener : fUpdateListeners){
					listener.modelUpdated(getSourceForm());
				}
			}
		}
	}

	public OperationExecuter(IModelUpdateContext updateContext){
		fOperationHistory = OperationHistoryFactory.getOperationHistory();
		fUpdateContext = updateContext;
		fUpdateListeners = updateContext.getUpdateListeners();
	}
	
	protected boolean execute(IModelOperation operation, String errorMessageTitle){
		try{
			UndoableOperation action = new UndoableOperation(operation, getUpdateContext().getUndoContext(), errorMessageTitle);
			fOperationHistory.execute(action, null, null);
			return true;
		} catch (ExecutionException e) {}
		return false;
	}
	
	protected IModelUpdateContext getUpdateContext(){
		return fUpdateContext;
	}
	
	private AbstractFormPart getSourceForm(){
		return getUpdateContext().getSourceForm();
	}
}

