package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.ModelClassLoader;
import com.testify.ecfeed.modelif.java.PartitionValueParser;

public class AbstractFrameworkMethod extends FrameworkMethod {

	private PartitionValueParser fValueParser;

	public AbstractFrameworkMethod(Method method, ModelClassLoader loader) {
		super(method);
		fValueParser = new PartitionValueParser(loader);
	}
	
	protected void invoke(Object target, List<PartitionNode> args) throws RunnerException, Throwable{
		List<Object> parameters = new ArrayList<Object>();
		for(PartitionNode p : args){
			parameters.add(parsePartitionValue(p));
		}
		super.invokeExplosively(target, parameters.toArray());
	}

	protected Object parsePartitionValue(PartitionNode partition) throws RunnerException{
		String type = partition.getCategory().getType();
		Object value = fValueParser.parseValue(partition);
		
		if(JavaUtils.isString(type) || JavaUtils.isUserType(type)){
			//null value acceptable
			if(partition.getValueString().equals(Constants.VALUE_REPRESENTATION_NULL)){
				return null;
			}
		}
		if(value == null){
			throw new RunnerException(Messages.CANNOT_PARSE_PARAMETER(type, partition.getValueString()));
		}
		return value;
	}
}
