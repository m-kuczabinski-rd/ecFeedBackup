package com.testify.ecfeed.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.parsers.EcParser;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.TestSuites;

import org.junit.Test;

public class EcFeeder extends BlockJUnit4ClassRunner {

	private Vector<RootNode> fEcModels;
	private Set<String> fTestSuites;
	List<FrameworkMethod> fTestMethods;
	
	public EcFeeder(Class<?> klass) throws Throwable{
		super(klass);
	}

	@Override
	public List<FrameworkMethod> computeTestMethods(){
		if(fTestMethods == null){
			fTestMethods = generateTestMethods();
		}
		return fTestMethods;
	}
	
	protected List<FrameworkMethod> generateTestMethods(){
		try {
			fEcModels = createEquivalenceClassModels();
			fTestSuites = getTestSuites();
		} catch (Throwable e) {
			System.out.println("Exception: " + e.getMessage());
		}
		
		List<FrameworkMethod> testMethods = new LinkedList<FrameworkMethod>();
		TestClass testClass = getTestClass();
		for(FrameworkMethod method : testClass.getAnnotatedMethods(Test.class)){
			if(method.getMethod().getParameterTypes().length == 0){
				//standard jUnit test
				testMethods.add(method);
				continue;
			} else{
				//parameterized test case: get requested test cases from models
				if(fEcModels == null){
					break;
				}
				for(RootNode rootNode : fEcModels){
					MethodNode methodModel = getMethodModel(rootNode, method);
					if(methodModel == null){
						continue;
					}
					for(String testSuite : fTestSuites){
						Collection<TestCaseNode> testCases = methodModel.getTestCases(testSuite);
						for(TestCaseNode testCase : testCases){
							testMethods.add(createTestMethod(method, testCase));
						}
					}
				}
			}
		}
		return testMethods;
	}

	@Override
	protected void validateTestMethods(List<Throwable> errors){
		validatePublicVoidMethods(Test.class, false, errors);
	}

	protected Set<String> getTestSuites() throws Throwable {
		Set<String> testSuites = new HashSet<String>();
		Set<FrameworkMethod> testSuiteMethods = getTestSuiteMethods(getTestClass());
		if(testSuiteMethods == null){
			fTestSuites.add("*");
		}
		for(FrameworkMethod method : testSuiteMethods){
			String[] suites = (String[]) method.invokeExplosively(null);
			testSuites.addAll(Arrays.asList(suites));
		}
		return testSuites;
	}

	protected Vector<RootNode> createEquivalenceClassModels() throws Throwable {
		Vector<RootNode> models = new Vector<RootNode>();
		EcParser parser = new EcParser();
		for(String ectPath : getEctFilesPaths(getTestClass())){
			models.add(parseEctModel(ectPath, parser));
		}
		return models;
	}

	protected Vector<RootNode> getModelsVector(){
		return fEcModels;
	}

	private ParameterizedFrameworkMethod createTestMethod(FrameworkMethod method, TestCaseNode testCase) {
		Vector<Object> testParameters = new Vector<Object>(); 
		for(PartitionNode partition : testCase.getTestData()){
			testParameters.add(partition.getValue());
		}
		return new ParameterizedFrameworkMethod(method.getMethod(), testParameters);
	}

	private MethodNode getMethodModel(RootNode rootNode, FrameworkMethod method) {
		String methodName = method.getName();
		String parentClassName = method.getMethod().getDeclaringClass().getName();
		ClassNode classModel = rootNode.getClassModel(parentClassName);
		return classModel.getMethod(methodName, getParameterTypes(method.getMethod().getParameterTypes()));
	}

	private Vector<String> getParameterTypes(Class<?>[] parameterTypes) {
		Vector<String> result = new Vector<String>();
		for(Class<?> parameter : parameterTypes){
			result.add(getParameterType(parameter));
		}
		return result;
	}

	private String getParameterType(Class<?> parameter) {
		return parameter.getSimpleName();
	}

	private Set<FrameworkMethod> getTestSuiteMethods(TestClass testClass) throws Exception{
		Set<FrameworkMethod> methods = new HashSet<FrameworkMethod>();
		List<FrameworkMethod> annotatedMethods = getTestClass().getAnnotatedMethods(TestSuites.class);
		for(FrameworkMethod method : annotatedMethods){
			if(isValidTestSuitesMethod(method)){
				methods.add(method);
			}
		}
		if(methods.size() > 0){
			return methods;
		}
		else{
			return null;
		}
	}

	private boolean isValidTestSuitesMethod(FrameworkMethod method) throws Exception {
		boolean result = validateMethod(method.getMethod(), Modifier.STATIC | Modifier.PUBLIC, String[].class, 0);
		if(result){
			return true;
		}
		else{
			throw new Exception("Method " + method.getName() + " is not valid TestSuites method. "
					+ "Valid method should be static and public and return String array with "
					+ "test suite names");
		}
	}

	private String[] getEctFilesPaths(TestClass testClass) throws Throwable {
		List<FrameworkMethod> annotatedMethods = testClass.getAnnotatedMethods(EcModel.class);
		if(annotatedMethods.size() == 0){
			throw new Exception("No EcModel method found");
		}
		FrameworkMethod method = annotatedMethods.get(0);
		if(!isValidEcModelMethod(method)){
			throw new Exception(annotatedMethods.get(0).getName() + "is not valid EcModel method.");
		}
		return (String[])method.invokeExplosively(null);
	}
	
	private boolean isValidEcModelMethod(FrameworkMethod method) throws Throwable {
		boolean result = validateMethod(method.getMethod(), Modifier.STATIC | Modifier.PUBLIC, String[].class, 0);
		if(result){
			return true;
		}
		else{
			throw new Exception("Method " + method.getName() + " is not valid EcModel method. "
					+ "Valid method should be static and public and return String array with "
					+ "paths to .ect files");
		}
	}

	private boolean validateMethod(Method method, int requiredModifiers, Class<?> requiredReturnType, int numOfParameters){
		boolean result = true;
		result &= (method.getModifiers() == requiredModifiers);
		result &= method.getReturnType().equals(requiredReturnType);
		result &= method.getParameterTypes().length == numOfParameters;
		return result;
	}

	private RootNode parseEctModel(String ectPath, EcParser parser) throws Throwable {
		InputStream istream = new FileInputStream(new File(ectPath));
		
		return parser.parseEctFile(istream);
	}

	private void validatePublicVoidMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
		List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);
		for(FrameworkMethod method : methods){
			method.validatePublicVoid(isStatic, errors);
		}
	}
	

}
