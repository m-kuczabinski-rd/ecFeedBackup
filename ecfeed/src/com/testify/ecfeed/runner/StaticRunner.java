package com.testify.ecfeed.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.parsers.EcParser;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.TestSuites;

public class StaticRunner extends BlockJUnit4ClassRunner {

	private List<FrameworkMethod> fTestMethods;
	private RootNode fModel;

	public StaticRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public List<FrameworkMethod> computeTestMethods(){
		if(fTestMethods == null){
			try {
				fTestMethods = generateTestMethods();
			} catch (RunnerException e) {
				System.out.println("Runner exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return fTestMethods;
	}

	@Override
	protected void validateTestMethods(List<Throwable> errors){
		validatePublicVoidMethods(Test.class, false, errors);
	}

	protected List<FrameworkMethod> generateTestMethods() throws RunnerException {
		
		List<FrameworkMethod> methods = new ArrayList<FrameworkMethod>();
		for(FrameworkMethod method : getTestClass().getAnnotatedMethods(Test.class)){
			if(method.getMethod().getParameterTypes().length == 0){
				//standard jUnit test
				methods.add(method);
			} else{
				MethodNode methodModel = getMethodModel(getModel(), method);
				if(methodModel == null){
					continue;
				}
				methods.add(new ParameterizedMethod(method.getMethod(), getTestCases(methodModel, getTestSuites(method))));
			}
		}
		return methods;
	}

	protected RootNode getModel() throws RunnerException{
		if(fModel == null){
			fModel = createModel();
		}
		return fModel;
	}

	protected MethodNode getMethodModel(RootNode rootNode, FrameworkMethod method) throws RunnerException {
		String methodName = method.getName();
		String parentClassName = method.getMethod().getDeclaringClass().getName();
		ClassNode classModel = rootNode.getClassModel(parentClassName);
		if(classModel == null){
			throw new RunnerException(Messages.CLASS_NOT_FOUND_IN_THE_MODEL(parentClassName));
		}
		return classModel.getMethod(methodName, getParameterTypes(method.getMethod().getParameterTypes()));
	}

	protected Set<String> getTestSuites(FrameworkMethod method) throws RunnerException{
		Set<String> result;
		Annotation annotation = method.getAnnotation(TestSuites.class);
		if(annotation != null){
			result = new HashSet<String>(Arrays.asList(((TestSuites)annotation).value()));
		}
		else{
			result = getMethodModel(getModel(), method).getTestSuites();
		}
		return result;
	}

	private Collection<TestCaseNode> getTestCases(MethodNode methodModel,
			Set<String> testSuites) {
		Collection<TestCaseNode> result = new LinkedList<TestCaseNode>();
		for(String testSuite : testSuites){
			result.addAll(methodModel.getTestCases(testSuite));
		}
		return result;
	}

	private ArrayList<String> getParameterTypes(Class<?>[] parameterTypes) {
		ArrayList<String> result = new ArrayList<String>();
		for(Class<?> parameter : parameterTypes){
			result.add(getParameterType(parameter));
		}
		return result;
	}

	private String getParameterType(Class<?> parameter) {
		return parameter.getSimpleName();
	}


	private String getEctFilePath() throws RunnerException {
		TestClass testClass = getTestClass();
		for(Annotation annotation : testClass.getAnnotations()){
			if(annotation.annotationType().equals(EcModel.class)){
				return ((EcModel)annotation).value();
			}
		}
		throw new RunnerException(Messages.CANNOT_FIND_MODEL);
	}

	private void validatePublicVoidMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
		List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);
		for(FrameworkMethod method : methods){
			method.validatePublicVoid(isStatic, errors);
		}
	}

	private RootNode createModel() throws RunnerException {
		EcParser parser = new EcParser();
		String ectFilePath = getEctFilePath();
		InputStream istream;
		try {
			istream = new FileInputStream(new File(ectFilePath));
		} catch (FileNotFoundException e) {
			throw new RunnerException(Messages.CANNOT_FIND_MODEL);
		}
		return parser.parseEctFile(istream);
	}
}
