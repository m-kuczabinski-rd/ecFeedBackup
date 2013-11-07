package com.testify.online_runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.internal.matchers.Each;
import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IAlgorithm;
import com.testify.ecfeed.api.IAlgorithmInput;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.api.IGeneratorParameter;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.generators.CartesianProductGenerator;
import com.testify.generators.algorithms.Tuples;
import com.testify.generators.monitors.SilentProgressMonitor;
import com.testify.generators.test.TestUtils;;



public class RuntimeFrameworkMethod extends FrameworkMethod{

	protected IGenerator<PartitionNode> fGenerator;
	
	public RuntimeFrameworkMethod(Method method, IGenerator<PartitionNode> generator,
			Collection<? extends IConstraint<PartitionNode>> constraints,List<List<PartitionNode>> inputDomain,
			IProgressMonitor progressMonitor,Map<String, Object> parameters) throws Exception{
				super(method);
				this.fGenerator= (IGenerator<PartitionNode>) generator;
				this.fGenerator.initialize(inputDomain, constraints, parameters, progressMonitor);
				
	}


	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{
		 List<Object> fResult=new ArrayList<Object>();
		try {
			
			List<PartitionNode> next;
			while((next =  (List<PartitionNode>) fGenerator.next()) !=null){
				for (PartitionNode partitionNode : next) {
					super.invokeExplosively(target,partitionNode.getValue() );
				}

			}
		} catch (GeneratorException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString(){
		String result = getMethod().getName() ;
		
		return result;
	}
	
	

	


	
}
