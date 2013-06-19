package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.Vector;

import org.junit.runners.model.FrameworkMethod;

public class ParameterizedFrameworkMethod extends FrameworkMethod {

	protected Vector<Object> fParameters;

	public ParameterizedFrameworkMethod(Method method, Vector<Object> parameters) {
		super(method);
		fParameters = parameters;
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{
		return super.invokeExplosively(target, fParameters.toArray());
	}
	
	@Override
	public String toString(){
		String result = getMethod().getName() + "(";
		for(int i = 0; i < fParameters.size(); i++){
			result += fParameters.elementAt(i).toString();
			if(i < fParameters.size() - 1){
				result += ", ";
			}
		}
		result += ")";
		return result;
	}
}
