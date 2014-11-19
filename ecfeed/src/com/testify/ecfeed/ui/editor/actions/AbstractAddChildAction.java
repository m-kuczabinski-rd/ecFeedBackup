package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.ParameterInterface;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.RootInterface;

public abstract class AbstractAddChildAction extends ModelModifyingAction{
	
	protected final static String ADD_CLASS_ACTION_NAME = "Add class";
	protected final static String ADD_METHOD_ACTION_NAME = "Add method";
	protected final static String ADD_PARAMETER_ACTION_NAME = "Add parameter";
	protected final static String ADD_TEST_CASE_ACTION_NAME = "Add test case";
	protected final static String ADD_PARTITION_ACTION_NAME = "Add choice";
	protected final static String ADD_CONSTRAINT_ACTION_NAME = "Add constraint";

	protected final static String ADD_CLASS_ACTION_ID = "addClass";
	protected final static String ADD_METHOD_ACTION_ID = "addMethod";
	protected final static String ADD_PARAMETER_ACTION_ID = "addParameter";
	protected final static String ADD_TEST_CASE_ACTION_ID = "addTestCase";
	protected final static String ADD_PARTITION_ACTION_ID = "addChoice";
	protected final static String ADD_CONSTRAINT_ACTION_ID = "addConstraint";

	private StructuredViewer fViewer;

	private class AddChildVisitor implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			RootInterface rootIf = new RootInterface(getUpdateContext());
			rootIf.setTarget(node);
			return rootIf.addNewClass();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			ClassInterface classIf = new ClassInterface(getUpdateContext());
			classIf.setTarget(node);
			return classIf.addNewMethod();
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ParameterNode node) throws Exception {
			ParameterInterface parameterIf = new ParameterInterface(getUpdateContext());
			parameterIf.setTarget(node);
			return parameterIf.addNewChoice();
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
		public Object visit(ChoiceNode node) throws Exception {
			ParameterInterface parameterIf = new ParameterInterface(getUpdateContext());
			parameterIf.setTarget(node);
			return parameterIf.addNewChoice();
		}
	}
	
	private class ActionEnabledResolver implements IModelVisitor{

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
		public Object visit(ParameterNode node) throws Exception {
			if(node.isExpected() == false || JavaUtils.isUserType(node.getType())){
				return true;
			}
			return false;
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
		public Object visit(ChoiceNode node) throws Exception {
			return true;
		}
		
	}
	
	public AbstractAddChildAction(String id, String name, StructuredViewer viewer, IModelUpdateContext updateContext) {
		super(id, name, viewer, updateContext);
		fViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		if (getSelectedNodes().size() != 1) return false;
		try{
			IModelVisitor resolver = new ActionEnabledResolver();
			return (boolean)getSelectedNodes().get(0).accept(resolver);
		}
		catch(Exception e){}
		return false;
	}

	@Override
	public void run(){
		try{
			if (getSelectedNodes().size() == 1){
				select((AbstractNode)getSelectedNodes().get(0).accept(new AddChildVisitor()));
			}
		}
		catch(Exception e){}
	}
	
	protected void select(AbstractNode node){
		if(fViewer != null){
			fViewer.setSelection(new StructuredSelection(node));
		}
	}
}
