package com.testify.ecfeed.junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.adapter.java.PartitionValueParser;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;

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
		String type = partition.getParameter().getType();
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
