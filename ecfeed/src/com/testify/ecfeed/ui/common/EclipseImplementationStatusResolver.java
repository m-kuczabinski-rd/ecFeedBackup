package com.testify.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class EclipseImplementationStatusResolver extends CachedImplementationStatusResolver{
	
	private JavaModelAnalyser fJavaModelAnalyser;

	public EclipseImplementationStatusResolver(){
		fJavaModelAnalyser = new JavaModelAnalyser();
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
		if(classDefinitionImplemented(classNode) == false){
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
			IType type = fJavaModelAnalyser.getIType(category.getType());
			try{
				if(type == null || type.isEnum() == false){
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
			catch(JavaModelException e){
				status = EImplementationStatus.NOT_IMPLEMENTED;
			}
		}
		return status;
	}

	@Override
	protected EImplementationStatus implementationStatus(TestCaseNode testCase){
		EImplementationStatus status = childrenStatus(testCase.getTestData());
		return status;
	}
	
	@Override
	protected EImplementationStatus implementationStatus(ConstraintNode constraint){
		return EImplementationStatus.IRRELEVANT;
	}
	
	@Override
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
					IType typeDef = fJavaModelAnalyser.getIType(type);
					try{
						if(typeDef == null || typeDef.isEnum() == false){
							status = EImplementationStatus.NOT_IMPLEMENTED;
						}
						else{
							for(IField field : typeDef.getFields()){
								if(field.isEnumConstant() && field.getElementName().equals(partition.getValueString())){
									status = EImplementationStatus.IMPLEMENTED;
									break;
								}
							}
						}
					} catch (JavaModelException e) {
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
	
	protected boolean classDefinitionImplemented(ClassNode classNode) {
		return fJavaModelAnalyser.getIType(classNode.getName()) != null;
	}
	
	protected boolean methodDefinitionImplemented(MethodNode method) {
		IType classType = fJavaModelAnalyser.getIType(method.getClassNode().getName());
		if(classType == null){
			return false;
		}
		try {
			for(IMethod methodDef : classType.getMethods()){
				if(methodDef.getElementName().equals(method.getName()) == false){
					continue;
				}
				List<String> parameterTypes = new ArrayList<>();
				for(ILocalVariable parameter : methodDef.getParameters()){
					parameterTypes.add(fJavaModelAnalyser.getTypeName(methodDef, parameter));
				}
				if(parameterTypes.equals(method.getCategoriesTypes())){
					return true;
				}
			}
		} catch (JavaModelException e) {}
		return false;
	}
	
	
}
