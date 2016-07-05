/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter;

import java.util.HashMap;
import java.util.Map;

import com.ecfeed.core.adapter.java.JavaPrimitiveTypePredicate;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IPrimitiveTypePredicate;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;


public abstract class CachedImplementationStatusResolver extends AbstractImplementationStatusResolver {

	private static Map<AbstractNode, EImplementationStatus> fCache = new HashMap<>();
	private static CacheCleaner fCacheCleaner = new CacheCleaner();

	private static class CacheCleaner implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			fCache.remove(node);
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			fCache.remove(node);
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			fCache.remove(node);
			return null;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			fCache.remove(node);
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			fCache.remove(node);
			for(MethodParameterNode parameter : node.getLinkers()){
				fCache.remove(parameter);
			}
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			fCache.remove(node);
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			fCache.remove(node);
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			fCache.remove(node);
			for(MethodNode method : node.getParameter().getMethods()){
				for(TestCaseNode testCase : method.mentioningTestCases(node)){
					fCache.remove(testCase);
				}
			}
			return null;
		}

	}

	public CachedImplementationStatusResolver(IPrimitiveTypePredicate primitiveTypePredicate) {
		super(new JavaPrimitiveTypePredicate());
		fCacheCleaner = new CacheCleaner();
	}

	@Override
	public EImplementationStatus getImplementationStatus(AbstractNode node){
		EImplementationStatus status = fCache.get(node);

		if(status == null){
			status = super.getImplementationStatus(node);
			updateCache(node, status);
		}

		return status;
	}

	public static void clearCache(AbstractNode node){
		if(node != null){
			try{
				node.accept(fCacheCleaner);
			}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
			clearCache(node.getParent());
		}
	}

	public void updateCache(AbstractNode node, EImplementationStatus status){
		fCache.put(node, status);
		if(node != null && node.getParent() != null){
			clearCache(node.getParent());
		}
	}

	public static void clearCache(){
		fCache.clear();
	}
}
