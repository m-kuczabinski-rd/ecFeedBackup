/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.adapter;

import java.util.List;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.IPrimitiveTypePredicate;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public abstract class AbstractImplementationStatusResolver implements
		IImplementationStatusResolver {

	private StatusResolver fStatusResolver;
	private IPrimitiveTypePredicate fPrimitiveTypeTester;

	private class StatusResolver implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return implementationStatus(node);
		}
	}

	public AbstractImplementationStatusResolver(IPrimitiveTypePredicate primitiveTypeTester){
		fStatusResolver = new StatusResolver();
		fPrimitiveTypeTester = primitiveTypeTester;
	}

	@Override
	public EImplementationStatus getImplementationStatus(AbstractNode node) {
		try{
			EImplementationStatus status = (EImplementationStatus)node.accept(fStatusResolver);
			return status;
		}
		catch(Exception e){}
		return EImplementationStatus.NOT_IMPLEMENTED;
	}

	protected EImplementationStatus implementationStatus(RootNode project){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(project.getClasses().size() != 0){
			EImplementationStatus childrenStatus = childrenStatus(project.getClasses());
			if(childrenStatus != EImplementationStatus.IMPLEMENTED){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(ClassNode classNode){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(classDefinitionImplemented(classNode.getName()) == false){
			status = EImplementationStatus.NOT_IMPLEMENTED;
		}
		else if(classNode.getMethods().size() == 0){
			status = EImplementationStatus.IMPLEMENTED;
		}
		else{
			EImplementationStatus childrenStatus = childrenStatus(classNode.getMethods());
			if(childrenStatus != EImplementationStatus.IMPLEMENTED){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(MethodNode method){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(methodDefinitionImplemented(method) == false){
			status = EImplementationStatus.NOT_IMPLEMENTED;
		}
		else if(method.getParameters().size() == 0){
			status = EImplementationStatus.IMPLEMENTED;
		}
		else{
			EImplementationStatus childrenStatus = childrenStatus(method.getParameters());
			if(childrenStatus != EImplementationStatus.IMPLEMENTED){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(MethodParameterNode parameter){
		EImplementationStatus status = implementationStatus((AbstractParameterNode)parameter);
		if(fPrimitiveTypeTester.isPrimitive(parameter.getType()) && parameter.isExpected()){
			status = EImplementationStatus.IMPLEMENTED;
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(GlobalParameterNode parameter){
		return implementationStatus((AbstractParameterNode)parameter);
	}

	protected EImplementationStatus implementationStatus(AbstractParameterNode parameter){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(fPrimitiveTypeTester.isPrimitive(parameter.getType()) == true){
			if(parameter.getChoices().size() == 0){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}else{
			if(enumDefinitionImplemented(parameter.getType()) == false){
				status = EImplementationStatus.NOT_IMPLEMENTED;
			}else{
				if(parameter.getChoices().size() == 0){
					status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
				}else{
					for(ChoiceNode choice : parameter.getChoices()){
						if(implementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
							status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
						}
					}
				}
			}
		}
		return status;
	}

	protected EImplementationStatus implementationStatus(TestCaseNode testCase){
		EImplementationStatus status = childrenStatus(testCase.getTestData());
		return status;
	}

	protected EImplementationStatus implementationStatus(ConstraintNode constraint){
		return EImplementationStatus.IRRELEVANT;
	}

	protected EImplementationStatus implementationStatus(ChoiceNode choice){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(choice.isAbstract() == false){
			AbstractParameterNode parameter = choice.getParameter();
			if(parameter == null){
				status = EImplementationStatus.NOT_IMPLEMENTED;
			}
			else{
				String type = parameter.getType();
				if(fPrimitiveTypeTester.isPrimitive(type)){
					status = EImplementationStatus.IMPLEMENTED;
				}
				else{
					if(enumValueImplemented(type, choice.getValueString())){
						status = EImplementationStatus.IMPLEMENTED;
					}
					else{
						status = EImplementationStatus.NOT_IMPLEMENTED;
					}
				}
			}
		}
		else{
			status = childrenStatus(choice.getChoices());
		}
		return status;
	}

	protected EImplementationStatus childrenStatus(List<? extends AbstractNode> children){
		int size = children.size();
		int implementedChildren = 0;
		int notImplementedChildren = 0;
		for(AbstractNode child : children){
			EImplementationStatus status = getImplementationStatus(child);
			if(status == EImplementationStatus.IMPLEMENTED) ++implementedChildren;
			if(status == EImplementationStatus.NOT_IMPLEMENTED) ++notImplementedChildren;
		}
		if(implementedChildren == size){
			return EImplementationStatus.IMPLEMENTED;
		}
		else if(notImplementedChildren == size){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		return EImplementationStatus.PARTIALLY_IMPLEMENTED;
	}

	protected abstract boolean classDefinitionImplemented(String qualifiedName);
	protected abstract boolean methodDefinitionImplemented(MethodNode method);
	protected abstract boolean enumDefinitionImplemented(String qualifiedName);
	protected abstract boolean enumValueImplemented(String qualifiedName, String value);
}
