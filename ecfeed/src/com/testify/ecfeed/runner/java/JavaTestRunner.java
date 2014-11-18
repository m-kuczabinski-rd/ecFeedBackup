package com.testify.ecfeed.runner.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.java.ModelClassLoader;
import com.testify.ecfeed.adapter.java.ChoiceValueParser;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;

public class JavaTestRunner {

	private ModelClassLoader fLoader;
	private MethodNode fTarget;
	private Class<?> fTestClass;
	private Method fTestMethod;

	public JavaTestRunner(ModelClassLoader loader){
		fLoader = loader;
	}

	public void setTarget(MethodNode target) throws RunnerException{
		fTarget = target;
		ClassNode testClassModel = fTarget.getClassNode();
		fTestClass = getTestClass(testClassModel.getQualifiedName());
		fTestMethod = getTestMethod(fTestClass, fTarget);
	}

	public void runTestCase(List<ChoiceNode> testData) throws RunnerException{
		validateTestData(testData);
		try {
			Object instance = fTestClass.newInstance();
			Object[] arguments = getArguments(testData);
			fTestMethod.invoke(instance, arguments);
		}catch(InvocationTargetException e){
//			throw new RunnerException(Messages.TEST_METHOD_INVOCATION_EXCEPTION(fTarget.toString(), testData.toString(), e.getTargetException().toString()));
			throw new RunnerException(fTarget.getName() + testData.toString() + ": " + e.getTargetException().toString());
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
			throw new RunnerException(Messages.CANNOT_INVOKE_TEST_METHOD(fTarget.toString(), testData.toString(), e.getMessage()));
		}
	}

	protected Method getTestMethod(Class<?> testClass, MethodNode methodModel) throws RunnerException {
		for(Method method : testClass.getMethods()){
			if(isModel(method, methodModel)){
				return method;
			}
		}
		throw new RunnerException(Messages.METHOD_NOT_FOUND(methodModel.toString()));
	}

	protected boolean isModel(Method method, MethodNode methodModel) {
		String methodName = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		List<String> types = new ArrayList<String>();
		for(Class<?> type : parameterTypes){
			types.add(JavaUtils.getTypeName(type.getCanonicalName()));
		}
		return methodName.equals(methodModel.getName()) && types.equals(methodModel.getParametersTypes());
	}

	protected Object[] getArguments(List<ChoiceNode> testData) throws RunnerException {
		List<Object> args = new ArrayList<Object>();
		ChoiceValueParser parser = new ChoiceValueParser(fLoader);
		for(ChoiceNode p : testData){
			Object value = parser.parseValue(p);
			if(value == null){
				String type = p.getParameter().getType();
				//check if null value acceptable
				if(JavaUtils.isString(type) || JavaUtils.isUserType(type)){
					if(p.getValueString().equals(Constants.VALUE_REPRESENTATION_NULL) == false){
						throw new RunnerException(Messages.CANNOT_PARSE_PARAMETER(type, p.getValueString()));
					}
				}
			}

			args.add(value);
		}
		return args.toArray();
	}

	private void validateTestData(List<ChoiceNode> testData) throws RunnerException {
		List<String> dataTypes = new ArrayList<String>();
		for(ChoiceNode parameter : testData){
			dataTypes.add(parameter.getParameter().getType());
		}
		if(dataTypes.equals(fTarget.getParametersTypes()) == false){
			throw new RunnerException(Messages.WRONG_TEST_METHOD_SIGNATURE(fTarget.toString()));
		}
	}

	private Class<?> getTestClass(String qualifiedName) throws RunnerException {
		Class<?> testClass = fLoader.loadClass(qualifiedName);
		if(testClass == null){
			throw new RunnerException(Messages.CANNOT_LOAD_CLASS(qualifiedName));
		}
		return testClass;
	}

}
