package com.testify.ecfeed.adapter;

import java.util.HashMap;
import java.util.Map;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;


public abstract class CachedImplementationStatusResolver extends
		AbstractImplementationStatusResolver {

	private static Map<GenericNode, EImplementationStatus> fCache = new HashMap<>();
	private CacheCleaner fCacheCleaner;
	
	private class CacheCleaner implements IModelVisitor{

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
		public Object visit(CategoryNode node) throws Exception {
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
		public Object visit(PartitionNode node) throws Exception {
			fCache.remove(node);
			for(TestCaseNode testCase : node.getCategory().getMethod().mentioningTestCases(node)){
				fCache.remove(testCase);
			}
			return null;
		}
		
	}
	
	public CachedImplementationStatusResolver() {
		fCacheCleaner = new CacheCleaner();
	}
	
	public EImplementationStatus getImplementationStatus(GenericNode node){
//		long start = System.nanoTime();
//		System.out.print(node);
		EImplementationStatus status = fCache.get(node);
		if(status == null){
//			System.out.print(" not in cache");
			status = super.getImplementationStatus(node);
			updateCache(node, status);
		}
//		else{
//			System.out.print(" in cache");
//		}
//		long stop = System.nanoTime();
//		long elapsed = stop - start;
//		System.out.print(" checked in " + elapsed/1000 + "." + elapsed%1000 + "us\n");
		return status;
	}
	
	public void clearCache(GenericNode node){
		if(node != null){
			try{
				node.accept(fCacheCleaner);
			}catch(Exception e){}
			clearCache(node.getParent());
		}
	}
	
	public void updateCache(GenericNode node, EImplementationStatus status){
		fCache.put(node, status);
		clearCache(node.getParent());
	}
	
	public static void clearCache(){
		fCache.clear();
	}
}
