package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class AbstractAddChildAction extends ModelModyfyingAction implements INamedAction {
	
	private String fName;
	private TreeViewer fViewer;

	private class AddChildVisitor implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			RootInterface rootIf = new RootInterface();
			rootIf.setTarget(node);
			return rootIf.addNewClass(getUpdateContext());
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			ClassInterface classIf = new ClassInterface();
			classIf.setTarget(node);
			return classIf.addNewMethod(getUpdateContext());
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			CategoryInterface parameterIf = new CategoryInterface();
			parameterIf.setTarget(node);
			return parameterIf.addNewPartition(getUpdateContext());
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			CategoryInterface parameterIf = new CategoryInterface();
			parameterIf.setTarget(node);
			return parameterIf.addNewPartition(getUpdateContext());
		}
	}
	
	private class ClassEnabledResolver implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			return true;
		}
		
	}
	
	public AbstractAddChildAction(String name, TreeViewer viewer, IModelUpdateContext updateContext) {
		super(viewer, updateContext);
		fName = name;
		fViewer = viewer;
	}

	@Override
	public String getName() {
		return fName;
	}
	
	@Override
	public boolean isEnabled(){
		if (getSelectedNodes().size() != 1) return false;
		try{
			return (boolean)getSelectedNodes().get(0).accept(new ClassEnabledResolver());
		}
		catch(Exception e){}
		return false;
	}

	@Override
	public void run(){
		try{
			select((GenericNode)getSelectedNodes().get(0).accept(new AddChildVisitor()));
			
		}
		catch(Exception e){}
	}
	
	protected void select(GenericNode node){
		fViewer.setSelection(new StructuredSelection(node));
	}

}
