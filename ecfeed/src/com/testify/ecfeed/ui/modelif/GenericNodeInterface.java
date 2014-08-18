package com.testify.ecfeed.ui.modelif;

import java.net.URLClassLoader;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IImplementationStatusResolver;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.ILoaderProvider;
import com.testify.ecfeed.modelif.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.modelif.java.category.CategoryOperationMove;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSwap;
import com.testify.ecfeed.modelif.java.classx.ClassOperationMove;
import com.testify.ecfeed.modelif.java.method.MethodOperationMove;
import com.testify.ecfeed.modelif.java.testcase.TestCaseOperationMove;
import com.testify.ecfeed.ui.common.LoaderProvider;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class GenericNodeInterface {

	private ModelOperationManager fOperationManager;
	private ILoaderProvider fLoaderProvider;
	private IImplementationStatusResolver fStatusResolver;
	private GenericNode fTarget;
	
	private class MoveOperationProvider implements IModelVisitor{

		private GenericNode fNewParent;
		private int fNewIndex;

		public MoveOperationProvider(GenericNode newParent, int newIndex){
			fNewParent = newParent;
			fNewIndex = newIndex;
		}
		
		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fNewParent instanceof RootNode){
				return new ClassOperationMove(node, (RootNode)fNewParent, fNewIndex);
			}
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fNewParent instanceof ClassNode){
				return new MethodOperationMove(node, (ClassNode)fNewParent, fNewIndex);
			}
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			if(fNewParent instanceof MethodNode){
				MethodNode newMethod = (MethodNode)fNewParent;
				if(newMethod != node.getMethod()){
					return new CategoryOperationMove(node, (MethodNode)fNewParent, fNewIndex);
				}
				else{
					return new CategoryOperationSwap(node, fNewIndex);
				}
			}
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
			if(fMoveUp){
				if(node.getIndex() > 0){
					return new MethodOperationMove(node, node.getClassNode(), node.getIndex() - 1);
				}
			}
			else{
				if(node.getIndex() < node.getClassNode().getMethods().size() - 1){
					return new MethodOperationMove(node, node.getClassNode(), node.getIndex() + 1);
				}
			}
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			int newIndex = node.getIndex() + (fMoveUp ? -1 : 1);
			while(CategoryOperationSwap.swapAllowed(node, newIndex) == false){
				newIndex += (fMoveUp ? -1 : 1);
				if(newIndex < 0 || newIndex > node.getMethod().getCategories().size()){
					return null;
				}
			}
			return new CategoryOperationSwap(node, newIndex);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			if(fMoveUp){
				if(node.getIndex() > 0){
					return new TestCaseOperationMove(node, node.getMethod(), node.getIndex() - 1);
				}
			}
			else{
				if(node.getIndex() < node.getMethod().getTestCases().size() - 1){
					return new TestCaseOperationMove(node, node.getMethod(), node.getIndex() + 1);
				}
			}
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
		fLoaderProvider = new LoaderProvider();
		fStatusResolver = new JavaImplementationStatusResolver(fLoaderProvider);
	}

	public void setTarget(GenericNode target){
		fTarget = target;
	}
	
	protected ModelOperationManager getOperationManager(){
		return fOperationManager;
	}
	
	protected ModelClassLoader getLoader(boolean create, URLClassLoader parent){
		return fLoaderProvider.getLoader(create, parent);
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
	
	public ImplementationStatus implementationStatus(GenericNode node){
		return fStatusResolver.getImplementationStatus(node);
	}
	
	public ImplementationStatus implementationStatus(){
		return fStatusResolver.getImplementationStatus(fTarget);
	}
	
	static public boolean validateName(String name){
		return true;
	}
	
	public String getName(){
		return fTarget.getName();
	}
	
	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener){
		return false;
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
		return move(newParent, 0, source, updateListener);
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
