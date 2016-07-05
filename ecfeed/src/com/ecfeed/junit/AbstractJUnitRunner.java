/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.java.ILoaderProvider;
import com.ecfeed.core.adapter.java.JavaImplementationStatusResolver;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.core.serialization.IModelParser;
import com.ecfeed.core.serialization.ParserException;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.junit.annotations.EcModel;

public abstract class AbstractJUnitRunner extends BlockJUnit4ClassRunner {

	private List<FrameworkMethod> fTestMethods;
	private RootNode fModel;
	private ILoaderProvider fLoaderProvider;
	private JavaImplementationStatusResolver fImplementationStatusResolver;
	
	private class JUnitLoaderProvider implements ILoaderProvider{
		@Override
		public ModelClassLoader getLoader(boolean create, ClassLoader parent) {
			return new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader());
		}
	}
	
	public AbstractJUnitRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public List<FrameworkMethod> computeTestMethods(){
		if(fTestMethods == null){
			try {
				fTestMethods = generateTestMethods();
			} catch (RunnerException e) {
				System.out.println(Messages.RUNNER_EXCEPTION(e.getMessage()));
				e.printStackTrace();
			}
		}
		return fTestMethods;
	}

	protected abstract List<FrameworkMethod> generateTestMethods() throws RunnerException;

	@Override
	protected void validateTestMethods(List<Throwable> errors){
		validatePublicVoidMethods(Test.class, false, errors);
	}

	protected RootNode getModel() throws RunnerException{
		if(fModel == null){
			fModel = createModel();
		}
		return fModel;
	}

	protected MethodNode getMethodModel(RootNode rootNode, FrameworkMethod method) throws RunnerException {
		String methodName = method.getName();
		String parentClassName = method.getMethod().getDeclaringClass().getName();
		ClassNode classModel = rootNode.getClassModel(parentClassName);
		if(classModel == null){
			RunnerException.report(Messages.CLASS_NOT_FOUND_IN_THE_MODEL(parentClassName));
		}
		return classModel.getMethod(methodName, getParameterTypes(method.getMethod().getParameterTypes()));
	}
	
	protected EImplementationStatus implementationStatus(AbstractNode node){
		return getImplementationStatusResolver().getImplementationStatus(node);
	}
	
	protected ILoaderProvider getLoaderProvider(){
		if(fLoaderProvider == null){
			fLoaderProvider = new JUnitLoaderProvider();
		}
		return fLoaderProvider;
	}
	
	protected ModelClassLoader getLoader(){
		return getLoaderProvider().getLoader(true, this.getClass().getClassLoader());
	}
	
	protected JavaImplementationStatusResolver getImplementationStatusResolver() {
		if(fImplementationStatusResolver == null){
			fImplementationStatusResolver = new JavaImplementationStatusResolver(getLoaderProvider());
		}
		return fImplementationStatusResolver;
	}

	private RootNode createModel() throws RunnerException {
		IModelParser parser = new EctParser();
		String ectFilePath = getEctFilePath();
		InputStream istream;
		try {
			istream = new FileInputStream(new File(ectFilePath));
			return parser.parseModel(istream);
		} catch (FileNotFoundException e) {
			RunnerException.report(Messages.CANNOT_FIND_MODEL);
			return null;
		} catch (ParserException e) {
			RunnerException.report(Messages.CANNOT_PARSE_MODEL(e.getMessage()));
			return null;
		}
	}

	private void validatePublicVoidMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
		List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);
		for(FrameworkMethod method : methods){
			method.validatePublicVoid(isStatic, errors);
		}
	}

	private String getEctFilePath() throws RunnerException {
		TestClass testClass = getTestClass();
		for(Annotation annotation : testClass.getAnnotations()){
			if(annotation.annotationType().equals(EcModel.class)){
				return ((EcModel)annotation).value();
			}
		}
		RunnerException.report(Messages.CANNOT_FIND_MODEL);
		return null;
	}
	
	private ArrayList<String> getParameterTypes(Class<?>[] parameterTypes) {
		ArrayList<String> result = new ArrayList<String>();
		for(Class<?> parameter : parameterTypes){
			if (parameter.isEnum()) {
				result.add(parameter.getCanonicalName());	
			} else {
				result.add(parameter.getSimpleName());
			}
		}
		return result;
	}
}
