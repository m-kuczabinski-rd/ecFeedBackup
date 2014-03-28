/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.runner.annotations.Constraints;
import com.testify.ecfeed.runner.annotations.Generator;
import com.testify.ecfeed.runner.annotations.GeneratorParameter;
import com.testify.ecfeed.runner.annotations.GeneratorParameterNames;
import com.testify.ecfeed.runner.annotations.GeneratorParameterValues;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.generators.api.IGeneratorParameter;

public class OnlineRunner extends StaticRunner {

	public OnlineRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}
	
	@Override
	protected List<FrameworkMethod> generateTestMethods() throws RunnerException {
		List<FrameworkMethod> methods = new ArrayList<FrameworkMethod>();
		for(FrameworkMethod method : getTestClass().getAnnotatedMethods(Test.class)){
			if(method.getMethod().getParameterTypes().length == 0){
				//standard jUnit test
				methods.add(method);
			} else{
				MethodNode methodModel = getMethodModel(getModel(), method);
				if(methodModel == null){
					continue;
				}
				IGenerator<PartitionNode> generator = getGenerator(method);
				List<List<PartitionNode>> input = getInput(methodModel);
				Collection<IConstraint<PartitionNode>> constraints = getConstraints(method, methodModel);
				Map<String, Object> parameters = getGeneratorParameters(generator, method);
				try {
					generator.initialize(input, constraints, parameters);
				} catch (GeneratorException e) {
					throw new RunnerException(Messages.GENERATOR_INITIALIZATION_PROBLEM(e.getMessage()));
				}
				methods.add(new RuntimeMethod(method.getMethod(), generator));
			}
		}
		return methods;
	}

	private Map<String, Object> getGeneratorParameters(
			IGenerator<PartitionNode> generator, FrameworkMethod method) throws RunnerException {
		List<IGeneratorParameter> parameters = generator.parameters();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String>	parsedParameters = parseParameters(method.getAnnotations());{
			if(parsedParameters == null){
				parsedParameters = parseParameters(getTestClass().getAnnotations());
			}
		}
		for(IGeneratorParameter parameter : parameters){
			Object value = getParameterValue(parameter, parsedParameters);
			if(value == null && parameter.isRequired()){
				throw new RunnerException(Messages.MISSING_REQUIRED_PARAMETER(parameter.getName()));
			}
			else if(value != null){
				result.put(parameter.getName(), value);
			}
		}
		return result;
	}

	private Map<String, String> parseParameters(Annotation[] annotations) throws RunnerException {
		Map<String, String> result = null;
		String[] parameterNames = null;
		String[] parameterValues = null;
		for(Annotation annotation : annotations){
			if(annotation instanceof GeneratorParameter){
				result = new HashMap<String, String>();
				GeneratorParameter parameter = (GeneratorParameter)annotation;
				result.put(parameter.name(), parameter.value());
			}
			else if(annotation instanceof GeneratorParameterNames){
				parameterNames = ((GeneratorParameterNames)annotation).value();
			}
			else if(annotation instanceof GeneratorParameterValues){
				parameterValues = ((GeneratorParameterValues)annotation).value();
			}
		}
		if(parameterNames != null && parameterValues != null){
			if(parameterNames.length != parameterValues.length){
				throw new RunnerException(Messages.PARAMETERS_ANNOTATION_LENGTH_ERROR);
			}
			if(result == null){
				result = new HashMap<String, String>();
			}
			for(int i = 0; i < parameterNames.length; i++){
				result.put(parameterNames[i], parameterValues[i]);
			}
		} 
		else if(parameterNames != null || parameterValues != null){
			throw new RunnerException(Messages.MISSING_PARAMETERS_ANNOTATION);
		}
		return result;
	}

	private Object getParameterValue(IGeneratorParameter parameter,
			Map<String, String> parsedParameters) throws RunnerException {
		String valueString = parsedParameters.get(parameter.getName());
		if(valueString != null){
			try{
				switch (parameter.getType()) {
				case BOOLEAN:
					return Boolean.parseBoolean(valueString);
				case DOUBLE:
					return Double.parseDouble(valueString);
				case INTEGER:
					return Integer.parseInt(valueString);
				case STRING:
					return (String)valueString;
				}
			}
			catch(Throwable e){
				throw new RunnerException(Messages.WRONG_PARAMETER_TYPE(parameter.getName(), e.getMessage()));
			}
		}
		return null;
	}

	protected Collection<IConstraint<PartitionNode>> getConstraints(
			FrameworkMethod method, MethodNode methodModel) {
		Collection<String> constraintsNames = constraintsNames(method);
		if(constraintsNames == null){
			return methodModel.getAllConstraints();
		}
		if(constraintsNames.contains(Constraints.ALL)){
			constraintsNames = methodModel.getConstraintsNames();
		}
		else if(constraintsNames.contains(Constraints.NONE)){
			constraintsNames.clear();
		}
		
		Collection<IConstraint<PartitionNode>> constraints = new HashSet<IConstraint<PartitionNode>>();
		for(String name : constraintsNames){
			constraints.addAll(methodModel.getConstraints(name));
		}
		return constraints;
	}

	protected List<List<PartitionNode>> getInput(MethodNode methodModel) {
		List<List<PartitionNode>> result = new ArrayList<List<PartitionNode>>();
		for(AbstractCategoryNode category : methodModel.getCategories()){
			result.add(category.getPartitions());
		}
		return result;
	}

	protected IGenerator<PartitionNode> getGenerator(FrameworkMethod method) throws RunnerException {
		IGenerator<PartitionNode> generator = getGenerator(method.getAnnotations());
		if(generator == null){
			generator = getGenerator(getTestClass().getAnnotations());
		}
		if(generator == null){
			throw new RunnerException(Messages.NO_VALID_GENERATOR(method.getName()));
		}
		return generator;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IGenerator<PartitionNode> getGenerator(Annotation[] annotations) throws RunnerException{
		IGenerator<PartitionNode> generator = null;
		for(Annotation annotation : annotations){
			if(annotation instanceof Generator){
				try {
					Class<? extends IGenerator> generatorClass = ((Generator)annotation).value();  
					generatorClass.getTypeParameters();
					Constructor<? extends IGenerator> constructor = generatorClass.getConstructor(new Class<?>[]{});
					generator = (IGenerator<PartitionNode>)(constructor.newInstance(new Object[]{}));
				} catch (Exception e) {
					throw new RunnerException(Messages.CANNOT_INSTANTIATE_GENERATOR(e.getMessage()));
				}
			}
		}
		return generator;
	}

	protected Set<String> constraintsNames(FrameworkMethod method) {
		Set<String> names = constraintsNames(method.getAnnotations());
		if(names == null){
			names = constraintsNames(getTestClass().getAnnotations());
		}
		return names;
	}

	private Set<String> constraintsNames(Annotation[] annotations) {
		for(Annotation annotation : annotations){
			if(annotation instanceof Constraints){
				String[] constraints = ((Constraints)annotation).value();
				return new HashSet<String>(Arrays.asList(constraints));
			}
		}
		return null;
	}
}
