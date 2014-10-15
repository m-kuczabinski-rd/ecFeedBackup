package com.testify.ecfeed.adapter;

import java.util.List;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public abstract class AbstractImplementationStatusResolver implements
		IImplementationStatusResolver {

	private StatusResolver fStatusResolver;
	
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
		public Object visit(CategoryNode node) throws Exception {
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
		public Object visit(PartitionNode node) throws Exception {
			return implementationStatus(node);
		}
	}
	
	public AbstractImplementationStatusResolver() {
		fStatusResolver = new StatusResolver();
	}
	
	@Override
	public EImplementationStatus getImplementationStatus(GenericNode node) {
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
		else if(method.getCategories().size() == 0){
			status = EImplementationStatus.IMPLEMENTED;
		}
		else{
			EImplementationStatus childrenStatus = childrenStatus(method.getCategories()); 
			if(childrenStatus != EImplementationStatus.IMPLEMENTED){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return status;
	}
	
	protected EImplementationStatus implementationStatus(CategoryNode category){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(JavaUtils.isPrimitive(category.getType())){
			status = category.getPartitions().size() > 0 ? EImplementationStatus.IMPLEMENTED : EImplementationStatus.PARTIALLY_IMPLEMENTED;
		}
		else{
			if(enumDefinitionImplemented(category.getType()) == false){
				status = EImplementationStatus.NOT_IMPLEMENTED;
			}
			else if(category.getPartitions().size() == 0){
				status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
			else{
				EImplementationStatus childrenStatus = childrenStatus(category.getPartitions());
				if(childrenStatus != EImplementationStatus.IMPLEMENTED){
					status = EImplementationStatus.PARTIALLY_IMPLEMENTED;
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
	
	protected EImplementationStatus implementationStatus(PartitionNode partition){
		EImplementationStatus status = EImplementationStatus.IMPLEMENTED;
		if(partition.isAbstract() == false){
			CategoryNode parameter = partition.getCategory();
			if(parameter == null){
				status = EImplementationStatus.NOT_IMPLEMENTED;
			}
			else{
				String type = parameter.getType();
				if(JavaUtils.isPrimitive(type)){
					status = EImplementationStatus.IMPLEMENTED;
				}
				else{
					if(enumValueImplemented(type, partition.getValueString())){
						status = EImplementationStatus.IMPLEMENTED;
					}
					else{
						status = EImplementationStatus.NOT_IMPLEMENTED;
					}
				}
			}
		}
		else{
			status = childrenStatus(partition.getPartitions());
		}
		return status;
	}

	protected EImplementationStatus childrenStatus(List<? extends GenericNode> children){
		int size = children.size();
		int implementedChildren = 0;
		int notImplementedChildren = 0;
		for(GenericNode child : children){
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
