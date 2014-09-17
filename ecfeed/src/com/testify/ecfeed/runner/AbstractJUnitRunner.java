package com.testify.ecfeed.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.testify.ecfeed.abstraction.ImplementationStatus;
import com.testify.ecfeed.abstraction.java.ILoaderProvider;
import com.testify.ecfeed.abstraction.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.abstraction.java.ModelClassLoader;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.serialization.IModelParser;
import com.testify.ecfeed.serialization.ParserException;
import com.testify.ecfeed.serialization.ect.EctParser;

public abstract class AbstractJUnitRunner extends BlockJUnit4ClassRunner {

	private List<FrameworkMethod> fTestMethods;
	private RootNode fModel;
	private ILoaderProvider fLoaderProvider;
	private JavaImplementationStatusResolver fImplementationStatusResolver;
	
	private class JUnitLoaderProvider implements ILoaderProvider{
		@Override
		public ModelClassLoader getLoader(boolean create, ClassLoader parent) {
			return new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader());
		}
	}
	
	public AbstractJUnitRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public List<FrameworkMethod> computeTestMethods(){
		if(fTestMethods == null){
			try {
				fTestMethods = generateTestMethods();
			} catch (RunnerException e) {
				System.out.println(Messages.RUNNER_EXCEPTION(e.getMessage()));
				e.printStackTrace();
			}
		}
		return fTestMethods;
	}

	protected abstract List<FrameworkMethod> generateTestMethods() throws RunnerException;

	@Override
	protected void validateTestMethods(List<Throwable> errors){
		validatePublicVoidMethods(Test.class, false, errors);
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
	
	protected ImplementationStatus implementationStatus(GenericNode node){
		return getImplementationStatusResolver().getImplementationStatus(node);
	}
	
	protected ILoaderProvider getLoaderProvider(){
		if(fLoaderProvider == null){
			fLoaderProvider = new JUnitLoaderProvider();
		}
		return fLoaderProvider;
	}
	
	protected ModelClassLoader getLoader(){
		return getLoaderProvider().getLoader(true, this.getClass().getClassLoader());
	}
	
	protected JavaImplementationStatusResolver getImplementationStatusResolver() {
		if(fImplementationStatusResolver == null){
			fImplementationStatusResolver = new JavaImplementationStatusResolver(getLoaderProvider());
		}
		return fImplementationStatusResolver;
	}

	private RootNode createModel() throws RunnerException {
		IModelParser parser = new EctParser();
		String ectFilePath = getEctFilePath();
		InputStream istream;
		try {
			istream = new FileInputStream(new File(ectFilePath));
			return parser.parseModel(istream);
		} catch (FileNotFoundException e) {
			throw new RunnerException(Messages.CANNOT_FIND_MODEL);
		} catch (ParserException e) {
			throw new RunnerException(Messages.CANNOT_PARSE_MODEL(e.getMessage()));
		}
	}

	private void validatePublicVoidMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
		List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);
		for(FrameworkMethod method : methods){
			method.validatePublicVoid(isStatic, errors);
		}
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
	
	private ArrayList<String> getParameterTypes(Class<?>[] parameterTypes) {
		ArrayList<String> result = new ArrayList<String>();
		for(Class<?> parameter : parameterTypes){
			if (parameter.isEnum()) {
				result.add(parameter.getCanonicalName());	
			} else {
				result.add(parameter.getSimpleName());
			}
		}
		return result;
	}
}
