package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.PartitionNode;

public class RuntimeMethod extends FrameworkMethod{

	IGenerator<PartitionNode> fGenerator;
	
	public RuntimeMethod(Method method, IGenerator<PartitionNode> initializedGenerator) throws RunnerException{
		super(method);
		fGenerator = initializedGenerator;
	}
	
	@Override
	public Object invokeExplosively(Object target, Object... p) throws Throwable{
		List<PartitionNode> next;
		List<Object> parameters = new ArrayList<Object>();
		try {
			while((next = fGenerator.next()) !=null){
				parameters = new ArrayList<Object>();
				for (PartitionNode partitionNode : next) {
					parameters.add(partitionNode.getValue());
				}
				super.invokeExplosively(target, parameters.toArray());
			}
		} catch (GeneratorException e) {
			throw new RunnerException("Generator execution fault: " + e.getMessage());
		} catch (Throwable e){
			String message = getName() + "(" + parameters + "): " + e.getMessage();
			throw new Exception(message, e);
		}
		return null;
	}
}
