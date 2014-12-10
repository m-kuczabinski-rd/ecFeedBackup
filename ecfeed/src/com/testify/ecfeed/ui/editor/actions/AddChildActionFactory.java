package com.testify.ecfeed.ui.editor.actions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.GlobalParametersParentNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.GlobalParametersParentInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class AddChildActionFactory {

	private StructuredViewer fViewer;
	private IModelUpdateContext fContext;

	private class AddGlobalParameterAction extends AbstractAddChildAction{
		private GlobalParametersParentInterface fParentIf;

		public AddGlobalParameterAction() {
			super(ADD_METHOD_PARAMETER_ACTION_ID, ADD_METHOD_PARAMETER_ACTION_NAME, fViewer, fContext);
			fParentIf = new GlobalParametersParentInterface(fContext);
		}

		@Override
		public void run() {
			if(getParentInterface() != null){
				select(getParentInterface().addNewParameter());
			}
		}

		protected GlobalParametersParentInterface getParentInterface(){
			List<AbstractNode> nodes = getSelectedNodes();
			if(nodes.size() != 1) return null;
			if(nodes.get(0) instanceof GlobalParametersParentNode == false) return null;
			fParentIf.setTarget((GlobalParametersParentNode)nodes.get(0));
			return fParentIf;
		}

	}

	private class AddClassAction extends AbstractAddChildAction{
		private RootInterface fParentIf;

		public AddClassAction(){
			super(ADD_CLASS_ACTION_ID, ADD_CLASS_ACTION_NAME, fViewer, fContext);
			fParentIf = new RootInterface(fContext);
		}

		protected RootInterface getParentInterface(){
			List<AbstractNode> nodes = getSelectedNodes();
			if(nodes.size() != 1) return null;
			if(nodes.get(0) instanceof RootNode == false) return null;
			fParentIf.setTarget((RootNode)nodes.get(0));
			return fParentIf;
		}

		@Override
		public void run(){
			if(getParentInterface() != null){
				select(getParentInterface().addNewClass());
			}
		}
	}

	private class AddMethodAction extends AbstractAddChildAction{
		private ClassInterface fParentIf;

		public AddMethodAction(){
			super(ADD_METHOD_ACTION_ID, ADD_METHOD_ACTION_NAME, fViewer, fContext);
			fParentIf = new ClassInterface(fContext);
		}

		protected ClassInterface getParentInterface(){
			List<AbstractNode> nodes = getSelectedNodes();
			if(nodes.size() != 1) return null;
			if(nodes.get(0) instanceof ClassNode == false) return null;
			fParentIf.setTarget((ClassNode)nodes.get(0));
			return fParentIf;
		}

		@Override
		public void run(){
			if(getParentInterface() != null){
				select(getParentInterface().addNewMethod());
			}
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
			List<AbstractNode> nodes = getSelectedNodes();
			if(nodes.size() != 1) return false;
			if(nodes.get(0) instanceof MethodNode == false) return false;
			return true;
		}

		protected MethodInterface getParentInterface(){
			List<AbstractNode> nodes = getSelectedNodes();
			if(nodes.size() != 1) return null;
			if(nodes.get(0) instanceof MethodNode == false) return null;
			fParentIf.setTarget((MethodNode)nodes.get(0));
			return fParentIf;
		}
	}

	private class AddMethodParameterAction extends AddMethodChildAction{
		public AddMethodParameterAction() {
			super(ADD_METHOD_PARAMETER_ACTION_ID, ADD_METHOD_PARAMETER_ACTION_NAME);
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

	private class AddChoiceAction extends AbstractAddChildAction{
		public AddChoiceAction(){
			super(ADD_PARTITION_ACTION_ID, ADD_PARTITION_ACTION_NAME, fViewer, fContext);
		}
	}

	private class AddNewChilActionProvider implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
				new AddClassAction(),
				new AddGlobalParameterAction()
			});
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodAction(),
					new AddGlobalParameterAction()
			});
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddMethodParameterAction(),
					new AddConstraintAction(),
					new AddTestCaseAction()
			});
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddChoiceAction()
			});
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return Arrays.asList(new AbstractAddChildAction[]{
					new AddChoiceAction()
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
					new AddChoiceAction()
			});
		}
	}

	public AddChildActionFactory(StructuredViewer viewer, IModelUpdateContext context){
		fContext = context;
		fViewer = viewer;
	}


	@SuppressWarnings("unchecked")
	public List<AbstractAddChildAction> getPossibleActions(AbstractNode parent){
		try{
			return (List<AbstractAddChildAction>)parent.accept(new AddNewChilActionProvider());
		}catch(Exception e){}
		return null;
	}
}
