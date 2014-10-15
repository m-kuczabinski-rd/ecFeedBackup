package com.testify.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.testify.ecfeed.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.model.MethodNode;

public class EclipseImplementationStatusResolver extends CachedImplementationStatusResolver{
	
	private JavaModelAnalyser fJavaModelAnalyser;

	public EclipseImplementationStatusResolver(){
		fJavaModelAnalyser = new JavaModelAnalyser();
	}
	
	protected boolean classDefinitionImplemented(String qualifiedName) {
		IType type = fJavaModelAnalyser.getIType(qualifiedName); 
		try {
			return  type != null && type.isClass();
		} catch (JavaModelException e) {}
		return false;
	}
	
	protected boolean methodDefinitionImplemented(MethodNode method) {
		IType classType = fJavaModelAnalyser.getIType(method.getClassNode().getName());
		if(classType == null){
			return false;
		}
		try {
			for(IMethod methodDef : classType.getMethods()){
				if(methodDef.getElementName().equals(method.getName()) == false || 
						methodDef.getReturnType().equals(Signature.SIG_VOID) == false ||
						Flags.isPublic(methodDef.getFlags()) == false){
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

	@Override
	protected boolean enumDefinitionImplemented(String qualifiedName) {
		IType type = fJavaModelAnalyser.getIType(qualifiedName); 
		try {
			return  type != null && type.isEnum();
		} catch (JavaModelException e) {}
		return false;
	}

	@Override
	protected boolean enumValueImplemented(String qualifiedName, String value) {
		IType type = fJavaModelAnalyser.getIType(qualifiedName); 
		try {
			if(type == null || type.isEnum()){
				return false;
			}
			for(IField field : type.getFields()){
				if(field.isEnumConstant() && field.getElementName().equals(value)){
					return true;
				}
			}
		} catch (JavaModelException e) {}
		return false;
	}
}
