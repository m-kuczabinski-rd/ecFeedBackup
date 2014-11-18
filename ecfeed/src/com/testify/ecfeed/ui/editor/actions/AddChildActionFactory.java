package com.testify.ecfeed.ui.editor.actions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;

import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class AddChildActionFactory {

	private StructuredViewer fViewer;
	private IModelUpdateContext fContext;

	private class AddClassAction extends AbstractAddChildAction{
		public AddClassAction(){
			super(ADD_CLASS_ACTION_ID, ADD_CLASS_ACTION_NAME, fViewer, fContext);
		}
	}
	
	private class AddMethodAction extends AbstractAddChildAction{
		public AddMethodAction(){
			super(ADD_METHOD_ACTION_ID, ADD_METHOD_ACTION_NAME, fViewer, fContext);
		}
	}
	
	private abstract class AddMethodChildAction extends AbstractAddChildAction{
		private MethodInterface fParentIf;

		public AddMethodChildAction(String id, String name) {
			super(id, name, fViewer, fContext);
			fParentIf = new MethodInterface(fContext);
		}

		@Override
		public boolean isEnabled(){
			List<GenericNode> nodes = getSelectedNodes();
			if(nodes.size() != 1) return false;
			if(nodes.get(0) instanceof MethodNode == false) return false;
			return true;
		}
		
		protected MethodInterface getParentInterface(){
			List<GenericNode> nodes = getSelectedNodes();
			if(nodes.size() != 1) return null;
			if(nodes.get(0) instanceof MethodNode == false) return null;
			fParentIf.setTarget((MethodNode)nodes.get(0));
			return fParentIf;
		}
	}
	
	private class AddParameterAction extends AddMethodChildAction{
		public AddParameterAction() {
			super(ADD_PARAMETER_ACTION_ID, ADD_PARAMETER_ACTION_NAME);
		}

		@Override
		public void run() {
			select(getParentInterface().addNewParameter());
		}
	}
	
	private class AddConstraintAction extends AddMethodChildAction{
		public AddConstraintAction() {
			super(ADD_CONSTRAINT_ACTION_ID, ADD_CONSTRAINT_ACTION_NAME);
		}

		@Override
		public void run() {
			select(getParentInterface().addNewConstraint());
		}
	}
	
	private class AddTestCaseAction extends AddMethodChildAction{
		public AddTestCaseAction() {
			super(ADD_TEST_CASE_ACTION_ID, ADD_TEST_CASE_ACTION_NAME);
		}

		@Override
		public void run() {
			select(getParentInterface().addTestCase());
		}
	}
	
	private class AddPartitionAction extends AbstractAddChildAction{
		public AddPartitionAction(){
			super(ADD_PARTITION_ACTION_ID, ADD_PARTITION_ACTION_NAME, fViewer, fContext);
		}
	}
	
	private class AddNewChilActionProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
				new AddClassAction()
			});
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodAction()
			});
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddParameterAction(),
					new AddConstraintAction(),
					new AddTestCaseAction()
			});
		}

		@Override
		public Object visit(ParameterNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddPartitionAction()
			});
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{});
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{});
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddPartitionAction()
			});
		}
	}

	public AddChildActionFactory(StructuredViewer viewer, IModelUpdateContext context){
		fContext = context;
		fViewer = viewer;
	}
			
	
	@SuppressWarnings("unchecked")
	public List<AbstractAddChildAction> getPossibleActions(GenericNode parent){
		try{
			return (List<AbstractAddChildAction>)parent.accept(new AddNewChilActionProvider());
		}catch(Exception e){}
		return null;
	}
}
