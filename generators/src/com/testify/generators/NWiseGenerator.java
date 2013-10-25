///*******************************************************************************
// * Copyright (c) 2013 Testify AS.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// * 
// * Contributors:
// *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
// ******************************************************************************/
//
//package com.testify.generators;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//
//import com.testify.ecfeed.api.GeneratorException;
//import com.testify.ecfeed.api.IConstraint;
//import com.testify.ecfeed.api.IGenerator;
//import com.testify.ecfeed.api.IGeneratorParameter;
//import com.testify.generators.monitors.ConsoleProgressMonitor;
//
//public class NWiseGenerator<E> implements IGenerator<E>{
//	private class NParameter implements IGeneratorParameter{
//
//		private int fDefaultValue;
//		private int fMaxValue;
//		
//		NParameter(){
//			fDefaultValue = 2;
//			fMaxValue = Integer.MAX_VALUE;
//		}
//		
//		NParameter(int numOfCategories){
//			fDefaultValue = numOfCategories > 1?2:1;
//			fMaxValue = numOfCategories;
//		}
//		
//		@Override
//		public String getName() {
//			return N_PARAMETER_NAME;
//		}
//
//		@Override
//		public TYPE getType() {
//			return TYPE.NUMERIC;
//		}
//
//		@Override
//		public boolean isRequired() {
//			return true;
//		}
//
//		@Override
//		public Object defaultValue() {
//			return fDefaultValue;
//		}
//
//		@Override
//		public Object[] allowedValues() {
//			return null;
//		}
//
//		@Override
//		public long minValue() {
//			return 1;
//		}
//
//		@Override
//		public long maxValue() {
//			return fMaxValue;
//		}
//	}
//	
//	private int N = -1;
//	private final String N_PARAMETER_NAME = "N";
//	private Set<List<E>> fGeneratedSuite = null;
//	private boolean fInitialized = false;
//
//	@Override
//	public List<IGeneratorParameter> requiredParameters() {
//		List<IGeneratorParameter> result = new ArrayList<IGeneratorParameter>();
//		result.add(new NParameter());
//		return result;
//	}
//
//	@Override
//	public List<IGeneratorParameter> requiredParameters(
//			List<List<E>> inputDomain) {
//		List<IGeneratorParameter> result = new ArrayList<IGeneratorParameter>();
//		result.add(new NParameter(inputDomain.size()));
//		return result;
//	}
//
//	@Override
//	public void setParameter(String name, Object value) throws GeneratorException{
//		if(name.equals(N_PARAMETER_NAME)) N = (int) value;
//		else throw new GeneratorException("Unknown parameter: " + name);
//	}
//
//	@Override
//	public void initialize(List<List<E>> inputDomain,
//			Collection<IConstraint<E>> constraints,
//			IProgressMonitor progressMonitor) throws GeneratorException{
//		if(N == -1) throw new GeneratorException("Parameter " + N_PARAMETER_NAME + " has not been initialized");
//		if (progressMonitor == null) progressMonitor = new ConsoleProgressMonitor();
//		fInitialized = true;
//	}
//
//	@Override
//	public List<E> getNext() throws GeneratorException{
//		if(!fInitialized) throw new GeneratorException("Generator not initialized");
//		return null;
//	}
//	
//}
