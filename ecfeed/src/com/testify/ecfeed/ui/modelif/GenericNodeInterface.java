package com.testify.ecfeed.ui.modelif;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.ImplementationStatusResolver;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.modelif.java.classx.ClassOperationMove;
import com.testify.ecfeed.ui.common.LoaderProvider;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class GenericNodeInterface {

	private ModelOperationManager fOperationManager;
	private ModelClassLoader fLoader;
	private ImplementationStatusResolver fStatusResolver;
	private GenericNode fTarget;
	
	private class MoveOperationProvider implements IModelVisitor{

		private GenericNode fNewParent;
		private int fNewIndex;
		private boolean fValidIndex;

		public MoveOperationProvider(GenericNode newParent, int newIndex){
			fNewParent = newParent;
			fNewIndex = newIndex;
			fValidIndex = true;
		}
		
		public MoveOperationProvider(GenericNode newParent){
			fNewParent = newParent;
			fValidIndex = false;
		}
		
		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fNewParent instanceof RootNode){
				if(fValidIndex){
					return new ClassOperationMove(node, (RootNode)fNewParent, fNewIndex);
				}
				return new ClassOperationMove(node, (RootNode)fNewParent);
			}
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class MoveUpDownOperationProvider implements IModelVisitor{

		private boolean fMoveUp;
		
		public MoveUpDownOperationProvider(boolean moveUp){
			fMoveUp = moveUp;
		}
		
		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fMoveUp){
				if(node.getIndex() > 0){
					return new ClassOperationMove(node, node.getRoot(), node.getIndex() - 1);
				}
			}
			else{
				if(node.getIndex() < node.getRoot().getClasses().size() - 1){
					return new ClassOperationMove(node, node.getRoot(), node.getIndex() + 1);
				}
			}
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public GenericNodeInterface(ModelOperationManager modelOperationManager) {
		fOperationManager = modelOperationManager;
		fLoader = LoaderProvider.getLoader(false, null);
		fStatusResolver = new ImplementationStatusResolver(fLoader);
	}

	public void setTarget(GenericNode target){
		fTarget = target;
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
	
	public boolean move(GenericNode newParent, int newIndex, BasicSection source, IModelUpdateListener updateListener){
		try {
			IModelOperation moveOperation = (IModelOperation)fTarget.accept(new MoveOperationProvider(newParent, newIndex));
			return executeMoveOperation(moveOperation, source, updateListener);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean move(GenericNode newParent, BasicSection source, IModelUpdateListener updateListener){
		try{
			IModelOperation moveOperation = (IModelOperation)fTarget.accept(new MoveOperationProvider(newParent));
			return executeMoveOperation(moveOperation, source, updateListener);
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean moveUp(BasicSection source, IModelUpdateListener updateListener){
		try {
			IModelOperation moveOperation = (IModelOperation)fTarget.accept(new MoveUpDownOperationProvider(true));
			return executeMoveOperation(moveOperation, source, updateListener);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean moveDown(BasicSection source, IModelUpdateListener updateListener){
		try {
			IModelOperation moveOperation = (IModelOperation)fTarget.accept(new MoveUpDownOperationProvider(false));
			return executeMoveOperation(moveOperation, source, updateListener);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean moveUpDown(boolean up, BasicSection source, IModelUpdateListener updateListener){
		try {
			IModelOperation moveOperation = (IModelOperation)fTarget.accept(new MoveUpDownOperationProvider(up));
			return executeMoveOperation(moveOperation, source, updateListener);
		} catch (Exception e) {
			return false;
		}
		
	}

	private boolean executeMoveOperation(IModelOperation moveOperation,
			BasicSection source, IModelUpdateListener updateListener) {
		return execute(moveOperation, source, updateListener, Messages.DIALOG_MOVE_NODE_PROBLEM_TITLE);	}
	
}
