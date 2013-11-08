package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.model.PartitionNode;

public class RuntimeMethod extends FrameworkMethod{

	IGenerator<PartitionNode> fGenerator;
	
	public RuntimeMethod(Method method, IGenerator<PartitionNode> initializedGenerator) throws RunnerException{
		super(method);
		fGenerator = initializedGenerator;
	}
	
	@Override
	public Object invokeExplosively(Object target, Object... p) throws Throwable{
		try {
			List<PartitionNode> next;
			while((next = fGenerator.next()) !=null){
				List<Object> parameters = new ArrayList<Object>();
				for (PartitionNode partitionNode : next) {
					parameters.add(partitionNode.getValue());
				}
				super.invokeExplosively(target, parameters.toArray());
			}
		} catch (GeneratorException e) {
			throw new RunnerException("Generator execution fault: " + e.getMessage());
		}
		return null;
	}
}
