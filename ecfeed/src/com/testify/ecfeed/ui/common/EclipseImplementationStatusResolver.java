package com.testify.ecfeed.ui.common;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.adapter.java.JavaPrimitiveTypePredicate;
import com.testify.ecfeed.model.MethodNode;

public class EclipseImplementationStatusResolver extends CachedImplementationStatusResolver{

	public EclipseImplementationStatusResolver(){
		super(new JavaPrimitiveTypePredicate());
	}

	@Override
	protected boolean classDefinitionImplemented(String qualifiedName) {
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			return  type != null && type.isClass();
		} catch (JavaModelException e) {}
		return false;
	}

	@Override
	protected boolean methodDefinitionImplemented(MethodNode method) {
		return JavaModelAnalyser.getIMethod(method) != null;
	}

	@Override
	protected boolean enumDefinitionImplemented(String qualifiedName) {
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			return  type != null && type.isEnum();
		} catch (JavaModelException e) {}
		return false;
	}

	@Override
	protected boolean enumValueImplemented(String qualifiedName, String value) {
		IType type = JavaModelAnalyser.getIType(qualifiedName);
		try {
			if(type == null || type.isEnum() == false){
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
