package com.testify.ecfeed.adapter;

import java.util.List;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public abstract class AbstractModelImplementer implements IModelImplementer {

	private ImplementableVisitor fImplementableVisitor;
	private NodeImplementer fNodeImplementerVisitor;
	private IImplementationStatusResolver fStatusResolver;

	private class ImplementableVisitor implements IModelVisitor{
		@Override
		public Object visit(RootNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return implementable(node);
		}
	}

	private class NodeImplementer implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return implement(node);
		}

	}

	public AbstractModelImplementer(IImplementationStatusResolver statusResolver) {
		fImplementableVisitor = new ImplementableVisitor();
		fNodeImplementerVisitor = new NodeImplementer();
		fStatusResolver = statusResolver;
	}

	@Override
	public boolean implementable(Class<? extends AbstractNode> type){
		if(type.equals(RootNode.class) ||
			(type.equals(ClassNode.class))||
			(type.equals(MethodNode.class))||
			(type.equals(MethodParameterNode.class))||
			(type.equals(GlobalParameterNode.class))||
			(type.equals(TestCaseNode.class))||
			(type.equals(ChoiceNode.class))
		){
			return true;
		}
		return false;
	}

	@Override
	public boolean implementable(AbstractNode node) {
		try{
			return (boolean)node.accept(fImplementableVisitor);
		}catch(Exception e){}
		return false;
	}

	@Override
	public boolean implement(AbstractNode node) {
		try{
			if(implementable(node)){
				return (boolean)node.accept(fNodeImplementerVisitor);
			}
		}catch(Exception e){}
		return false;
	}

	@Override
	public EImplementationStatus getImplementationStatus(AbstractNode node) {
		return fStatusResolver.getImplementationStatus(node);
	}

	protected boolean implement(RootNode node) throws Exception{
		for(GlobalParameterNode parameter : node.getGlobalParameters()){
			if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		for(ClassNode classNode : node.getClasses()){
			if(implementable(classNode) && getImplementationStatus(classNode) != EImplementationStatus.IMPLEMENTED){
				implement(classNode);
			}
		}
		return true;
	}

	protected boolean implement(ClassNode node) throws Exception{
		if(classDefinitionImplemented(node) == false){
			implementClassDefinition(node);
		}
		for(GlobalParameterNode parameter : node.getGlobalParameters()){
			if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		for(MethodNode method : node.getMethods()){
			if(implementable(method) && getImplementationStatus(method) != EImplementationStatus.IMPLEMENTED){
				implement(method);
			}
		}
		return true;
	}

	protected boolean implement(MethodNode node) throws Exception{
		if(methodDefinitionImplemented(node) == false){
			implementMethodDefinition(node);
		}
		for(MethodParameterNode parameter : node.getMethodParameters()){
			if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		for(TestCaseNode testCase : node.getTestCases()){
			if(implementable(testCase) && getImplementationStatus(testCase) != EImplementationStatus.IMPLEMENTED){
				implement(testCase);
			}
		}
		return true;
	}

	protected boolean implement(AbstractParameterNode node) throws Exception{
		if(parameterDefinitionImplemented(node) == false){
			implementParameterDefinition(node);
		}
		for(ChoiceNode choice : node.getLeafChoices()){
			if(implementable(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				implement(choice);
				CachedImplementationStatusResolver.clearCache(choice);
			}
		}
		return true;
	}

	protected boolean implement(TestCaseNode node) throws Exception{
		for(ChoiceNode choice : node.getTestData()){
			if(implementable(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				implement(choice);
			}
		}
		return true;
	}

	protected boolean implement(ConstraintNode node) throws Exception{
		return false;
	}

	protected boolean implement(ChoiceNode node) throws Exception{
		if(parameterDefinitionImplemented(node.getParameter()) == false){
			implementParameterDefinition(node.getParameter());
		}
		if(node.isAbstract()){
			for(ChoiceNode leaf : node.getLeafChoices()){
				if(implementable(leaf) && getImplementationStatus(leaf) != EImplementationStatus.IMPLEMENTED){
					implement(leaf);
				}
			}
		}
		else{
			if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
				implementChoiceDefinition(node);
			}
		}
		return true;
	}

	protected boolean implementable(RootNode node){
		return hasImplementableNode(node.getClasses());
	}

	protected boolean implementable(ClassNode node){
		return hasImplementableNode(node.getMethods());
	}

	protected boolean implementable(MethodNode node){
		return hasImplementableNode(node.getParameters()) || hasImplementableNode(node.getTestCases());
	}

	protected boolean implementable(MethodParameterNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean implementable(GlobalParameterNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean implementable(ChoiceNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean implementable(TestCaseNode node){
		return hasImplementableNode(node.getTestData());
	}

	protected boolean hasImplementableNode(List<? extends AbstractNode> nodes){
		for(AbstractNode node : nodes){
			if(implementable(node)){
				return true;
			}
		}
		return false;
	}

	protected abstract boolean classDefinitionImplemented(ClassNode node);
	protected abstract boolean methodDefinitionImplemented(MethodNode node);
	protected abstract boolean parameterDefinitionImplemented(AbstractParameterNode node);

	protected abstract void implementClassDefinition(ClassNode node) throws Exception;
	protected abstract void implementMethodDefinition(MethodNode node) throws Exception;
	protected abstract void implementParameterDefinition(AbstractParameterNode node) throws Exception;
	protected abstract void implementChoiceDefinition(ChoiceNode node) throws Exception;
}
