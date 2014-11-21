package com.testify.ecfeed.adapter;

import java.util.HashMap;
import java.util.Map;

import com.testify.ecfeed.adapter.java.JavaPrimitiveTypePredicate;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;


public abstract class CachedImplementationStatusResolver extends
		AbstractImplementationStatusResolver {

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
			for(TestCaseNode testCase : node.getParameter().getMethod().mentioningTestCases(node)){
				fCache.remove(testCase);
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
			}catch(Exception e){}
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
