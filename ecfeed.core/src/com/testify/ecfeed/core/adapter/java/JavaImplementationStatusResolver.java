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

package com.testify.ecfeed.core.adapter.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.core.adapter.AbstractImplementationStatusResolver;
import com.testify.ecfeed.core.adapter.EImplementationStatus;
import com.testify.ecfeed.core.model.AbstractNode;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.IModelVisitor;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.utils.SystemLogger;

public class JavaImplementationStatusResolver extends AbstractImplementationStatusResolver{
	private ModelClassLoader fLoader;
	private Map<String, Class<?>> fLoadedClasses;
	private InternalStatusResolver fStatusVisitor;

	private class InternalStatusResolver implements IModelVisitor{

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

	public JavaImplementationStatusResolver(ILoaderProvider loaderProvider){
		super(new JavaPrimitiveTypePredicate());
		fLoader = loaderProvider.getLoader(true, null);
		fStatusVisitor = new InternalStatusResolver();
		fLoadedClasses = new HashMap<>();
	}

	@Override
	public EImplementationStatus getImplementationStatus(AbstractNode node){
		fLoadedClasses.clear();
		return super.getImplementationStatus(node);
	}

	@Override
	protected EImplementationStatus childrenStatus(List<? extends AbstractNode> children){
		int size = children.size();
		int implementedChildren = 0;
		int notImplementedChildren = 0;
		for(AbstractNode child : children){
			// do not use the public getImplementationStatus() function to avoid flushing cache
			EImplementationStatus status = implementationStatus(child);
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

	@Override
	protected boolean classDefinitionImplemented(String qualifiedName) {
		Class<?> classDefinition = loadClass(qualifiedName);
		return classDefinition != null && classDefinition.isInterface() == false && classDefinition.isEnum() == false && classDefinition.isAnnotation() == false;
	}

	@Override
	protected boolean methodDefinitionImplemented(MethodNode methodModel){
		Class<?> parentClass = loadClass(JavaUtils.getQualifiedName(methodModel.getClassNode()));
		if(parentClass == null){
			return false;
		}
		for(Method m : parentClass.getMethods()){
			if(m.getReturnType().equals(Void.TYPE) == false){
				continue;
			}
			if(Modifier.isPublic(m.getModifiers()) == false){
				continue;
			}
			if(m.getName().equals(methodModel.getName()) == false){
				continue;
			}
			List<String> typeNames = getArgTypes(m);
			List<MethodParameterNode> modelParameters = methodModel.getMethodParameters();
			if(typeNames.size() != methodModel.getParameters().size()){
				continue;
			}
			List<String> modelTypeNames = new ArrayList<>();
			for(MethodParameterNode parameter : modelParameters){
				modelTypeNames.add(parameter.getType());
			}
			if(modelTypeNames.equals(typeNames) == false){
				continue;
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean enumDefinitionImplemented(String qualifiedName) {
		Class<?> classDefinition = loadClass(qualifiedName);
		return classDefinition != null && classDefinition.isEnum();
	}

	@Override
	protected boolean enumValueImplemented(String qualifiedName, String value) {
		Class<?> classDefinition = loadClass(qualifiedName);
		if(classDefinition != null && classDefinition.isEnum()){
			for(Field field : classDefinition.getFields()){
				if(field.isEnumConstant() && field.getName().equals(value)){
					return true;
				}
			}
		}
		return false;
	}

	private EImplementationStatus implementationStatus(AbstractNode node) {
		try {
			return (EImplementationStatus)node.accept(fStatusVisitor);
		} catch (Exception e) {SystemLogger.logCatch(e.getMessage());}
		return EImplementationStatus.NOT_IMPLEMENTED;
	}

	private List<String> getArgTypes(Method method) {
		List<String> argTypes = new ArrayList<String>();
		for(Class<?> parameter : method.getParameterTypes()){
			argTypes.add(JavaUtils.getTypeName(parameter.getCanonicalName()));
		}
		return argTypes;
	}

	private Class<?> loadClass(String name){
		if(fLoadedClasses.containsKey(name) == false){
			Class<?> loaded = fLoader.loadClass(name);
			fLoadedClasses.put(name, loaded);
		}
		return fLoadedClasses.get(name);
	}
}
