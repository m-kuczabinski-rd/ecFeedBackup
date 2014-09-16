package com.testify.ecfeed.ui.editor.actions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class AddChildActionFactory {

	private StructuredViewer fViewer;
	private IModelUpdateContext fContext;

	private class AddClassAction extends AbstractAddChildAction{
		public AddClassAction(StructuredViewer viewer, IModelUpdateContext context){
			super(ADD_CLASS_ACTION_ID, ADD_CLASS_ACTION_NAME, viewer, context);
		}
	}
	
	private class AddMethodAction extends AbstractAddChildAction{
		public AddMethodAction(StructuredViewer viewer, IModelUpdateContext context){
			super(ADD_METHOD_ACTION_ID, ADD_METHOD_ACTION_NAME, viewer, context);
		}
	}
	
	private abstract class AddMethodChildAction extends AbstractAddChildAction{
		private MethodInterface fParentIf;

		public AddMethodChildAction(String id, String name, StructuredViewer viewer, IModelUpdateContext updateContext) {
			super(id, name, viewer, updateContext);
			fParentIf = new MethodInterface();
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
		public AddParameterAction(StructuredViewer viewer, IModelUpdateContext updateContext) {
			super(ADD_PARAMETER_ACTION_ID, ADD_PARAMETER_ACTION_NAME, viewer, updateContext);
		}

		@Override
		public void run() {
			select(getParentInterface().addNewParameter(getUpdateContext()));
		}
	}
	
	private class AddConstraintAction extends AddMethodChildAction{
		public AddConstraintAction(StructuredViewer viewer, IModelUpdateContext updateContext) {
			super(ADD_CONSTRAINT_ACTION_ID, ADD_CONSTRAINT_ACTION_NAME, viewer, updateContext);
		}

		@Override
		public void run() {
			select(getParentInterface().addNewConstraint(getUpdateContext()));
		}
	}
	
	private class AddTestCaseAction extends AddMethodChildAction{
		public AddTestCaseAction(StructuredViewer viewer, IModelUpdateContext updateContext) {
			super(ADD_TEST_CASE_ACTION_ID, ADD_TEST_CASE_ACTION_NAME, viewer, updateContext);
		}

		@Override
		public void run() {
			select(getParentInterface().addTestCase(getUpdateContext()));
		}
	}
	
	private class AddPartitionAction extends AbstractAddChildAction{
		public AddPartitionAction(StructuredViewer viewer, IModelUpdateContext context){
			super(ADD_PARTITION_ACTION_ID, ADD_PARTITION_ACTION_NAME, viewer, context);
		}
	}
	
	private class AddNewChilActionProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
				new AddClassAction(fViewer, fContext)
			});
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodAction(fViewer, fContext)
			});
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddParameterAction(fViewer, fContext),
					new AddConstraintAction(fViewer, fContext),
					new AddTestCaseAction(fViewer, fContext)
			});
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddPartitionAction(fViewer, fContext)
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
		public Object visit(PartitionNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddPartitionAction(fViewer, fContext)
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
