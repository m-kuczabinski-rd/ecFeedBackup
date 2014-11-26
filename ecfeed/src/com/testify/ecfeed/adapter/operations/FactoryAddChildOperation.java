package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class FactoryAddChildOperation implements IModelVisitor{

	private AbstractNode fChild;
	private int fIndex;
	private boolean fValidate;
	private ITypeAdapterProvider fAdapterProvider;

	public FactoryAddChildOperation(AbstractNode child, int index, ITypeAdapterProvider adapterProvider, boolean validate) {
		fChild = child;
		fIndex = index;
		fValidate = validate;
		fAdapterProvider = adapterProvider;
	}

	public FactoryAddChildOperation(AbstractNode child, ITypeAdapterProvider adapterProvider, boolean validate) {
		this(child, -1, adapterProvider, validate);
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fChild instanceof ClassNode){
			if(fIndex == -1){
				return new RootOperationAddNewClass(node, (ClassNode)fChild);
			}
			return new RootOperationAddNewClass(node, (ClassNode)fChild, fIndex);
		}
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fChild instanceof MethodNode){
			if(fIndex == -1){
				return new ClassOperationAddMethod(node, (MethodNode)fChild);
			}
			return new ClassOperationAddMethod(node, (MethodNode)fChild, fIndex);
		}
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof MethodParameterNode){
			if(fIndex == -1){
				return new MethodOperationAddParameter(node, (MethodParameterNode)fChild);
			}
			return new MethodOperationAddParameter(node, (MethodParameterNode)fChild, fIndex);
		}
		if(fChild instanceof ConstraintNode){
			if(fIndex == -1){
				return new MethodOperationAddConstraint(node, (ConstraintNode)fChild);
			}
			return new MethodOperationAddConstraint(node, (ConstraintNode)fChild, fIndex);
		}
		if(fChild instanceof TestCaseNode){
			if(fIndex == -1){
				return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider);
			}
			return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider, fIndex);
		}
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(MethodParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate);
		}
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate);
		}
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate);
		}
		throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

}
