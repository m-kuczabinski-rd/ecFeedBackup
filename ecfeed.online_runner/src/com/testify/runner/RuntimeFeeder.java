/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Linesh Raghawan (linesh.vr(at)testify.no) - initial implementation
 ******************************************************************************/

package com.testify.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.widgets.Combo;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.parsers.EcParser;
import com.testify.ecfeed.runner.annotations.Model;
import com.testify.ecfeed.runner.annotations.TestSuites;
import com.testify.generators.monitors.SilentProgressMonitor;

public class RuntimeFeeder extends BlockJUnit4ClassRunner {
	private Combo fGeneratorCombo;
	private RootNode fEcModel;
	private Set<String> fTestSuites;
	List<FrameworkMethod> fTestMethods;
	private Map<String, IGenerator<PartitionNode>> fAvaliableGenerators;
	private List<List<String>> fInput = new ArrayList<List<String>>();
	final IProgressMonitor progressMonitor = new SilentProgressMonitor();
	private CheckboxTreeViewer fConstraintsViewer;
	public RuntimeFeeder(Class<?> klass) throws Throwable {
		super(klass);
	}

	@Override
	public List<FrameworkMethod> computeTestMethods() {
		if (fTestMethods == null) {
			fTestMethods = generateTestMethods();
		}
		return fTestMethods;
	}

	protected List<FrameworkMethod> generateTestMethods() {
		try {
			fEcModel = createEquivalenceClassModel();
			fTestSuites = getTestSuites();
			List<FrameworkMethod> testMethods = new LinkedList<FrameworkMethod>();
			TestClass testClass = getTestClass();
			for (FrameworkMethod method : testClass
					.getAnnotatedMethods(Test.class)) {
				if (method.getMethod().getParameterTypes().length == 0) {
					// standard jUnit test
					testMethods.add(method);
					continue;
				} else {
					// parameterized test case: get requested test cases from
					// models
					if (fEcModel == null) {
						break;
					}

					MethodNode methodModel = getMethodModel(fEcModel, method);
					if (methodModel == null) {
						continue;
					}
					for (String testSuite : fTestSuites) {
						Collection<TestCaseNode> testCases = methodModel
								.getTestCases(testSuite);
						for (TestCaseNode testCase : testCases) {
							testMethods.add(createTestMethodRandom(method,
									testCase));
						}
					}
				}
			}
			return testMethods;
		} catch (Throwable e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void validateTestMethods(List<Throwable> errors) {
		validatePublicVoidMethods(Test.class, false, errors);
	}

	protected Set<String> getTestSuites() throws Throwable {
		Set<String> testSuites = new HashSet<String>();
		FrameworkMethod testSuiteMethod = getTestSuiteMethod(getTestClass());
		if (testSuiteMethod == null) {
			ClassNode classModel = fEcModel.getClassModel(getTestClass()
					.getName());
			if (classModel == null) {
				throw new Throwable(
						"The test class is not contained in the provided model");
			}
			return classModel.getTestSuites();
		} else {
			String[] suites = (String[]) testSuiteMethod
					.invokeExplosively(null);
			testSuites.addAll(Arrays.asList(suites));
		}
		return testSuites;
	}

	protected RootNode createEquivalenceClassModel() throws Throwable {
		EcParser parser = new EcParser();
		String ectFilePath = getEctFilePath(getTestClass());
		return parseEctModel(ectFilePath, parser);
	}

	protected RootNode getModelsVector() {
		return fEcModel;
	}

	@SuppressWarnings("unchecked")
	private RuntimeFrameworkMethod createTestMethodRandom(
			FrameworkMethod method, TestCaseNode testCase) throws Exception {
		ArrayList<Object> testParameters = new ArrayList<Object>();
		for (PartitionNode partition : testCase.getTestData()) {
			testParameters.add(partition.getValue());
		}

		IGenerator<PartitionNode> generator = fAvaliableGenerators
				.get(fGeneratorCombo.getText());

		/*
		 * Class<Constraints> aClass = Constraints.class; Annotation[]
		 * annotations = aClass.getAnnotations();
		 * 
		 * String[] constraints; for(Annotation annotation : annotations){
		 * if(annotation instanceof Constraints){ Constraints myAnnotation =
		 * (Constraints) annotation; constraints= myAnnotation.value();
		 * 
		 * } }
		 */
		Object[] checkedObjects = fConstraintsViewer.getCheckedElements();
		List<IConstraint<PartitionNode>> constraints = new ArrayList<IConstraint<PartitionNode>>();
		for (Object obj : checkedObjects) {
			if (obj instanceof Constraint) {
				constraints.add((Constraint) obj);
			}
		}

		return new RuntimeFrameworkMethod(method.getMethod(), generator,
				constraints, fInput, progressMonitor, (Map<String, Object>) testParameters);
	}

	private MethodNode getMethodModel(RootNode rootNode, FrameworkMethod method)
			throws Throwable {
		String methodName = method.getName();

		String parentClassName = method.getMethod().getDeclaringClass()
				.getName();
		ClassNode classModel = rootNode.getClassModel(parentClassName);
		if (classModel == null) {
			throw new Throwable("Cannot find model of " + parentClassName
					+ " in the model");
		}
		return classModel.getMethod(methodName, getParameterTypes(method
				.getMethod().getParameterTypes()));
	}

	private ArrayList<String> getParameterTypes(Class<?>[] parameterTypes) {
		ArrayList<String> result = new ArrayList<String>();
		for (Class<?> parameter : parameterTypes) {
			result.add(getParameterType(parameter));
		}
		return result;
	}

	private String getParameterType(Class<?> parameter) {
		return parameter.getSimpleName();
	}

	private FrameworkMethod getTestSuiteMethod(TestClass testClass)
			throws Throwable {
		List<FrameworkMethod> annotatedMethods = getTestClass()
				.getAnnotatedMethods(TestSuites.class);
		if (annotatedMethods.size() == 0) {
			return null;
		}
		if (annotatedMethods.size() > 1) {
			throw new Throwable(
					"There may be at most one method annotated with @TestSuites in the class.");
		}
		if (!isValidTestSuitesMethod(annotatedMethods.get(0))) {
			throw new Throwable(
					"@TestSuites method must be static, public and return String array.");
		}

		return annotatedMethods.get(0);
	}

	private boolean isValidTestSuitesMethod(FrameworkMethod method)
			throws Exception {
		boolean result = validateMethod(method.getMethod(), Modifier.STATIC
				| Modifier.PUBLIC, String[].class, 0);
		if (result) {
			return true;
		} else {
			throw new Exception(
					"Method "
							+ method.getName()
							+ " is not valid TestSuites method. "
							+ "Valid method should be static and public and return String array with "
							+ "test suite names");
		}
	}

	private String getEctFilePath(TestClass testClass) throws Throwable {
		for (Annotation annotation : testClass.getAnnotations()) {
			if (annotation.annotationType().equals(Model.class)) {
				return ((Model) annotation).value();
			}
		}
		throw new Throwable(
				"Cannot locate model path. Make sure that the test class is annotated with EcModel annotation with right path");
	}

	private boolean validateMethod(Method method, int requiredModifiers,
			Class<?> requiredReturnType, int numOfParameters) {
		boolean result = true;
		result &= (method.getModifiers() == requiredModifiers);
		result &= method.getReturnType().equals(requiredReturnType);
		result &= method.getParameterTypes().length == numOfParameters;
		return result;
	}

	private RootNode parseEctModel(String ectPath, EcParser parser)
			throws Throwable {
		InputStream istream = new FileInputStream(new File(ectPath));

		return parser.parseEctFile(istream);
	}

	private void validatePublicVoidMethods(
			Class<? extends Annotation> annotation, boolean isStatic,
			List<Throwable> errors) {
		List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(
				annotation);
		for (FrameworkMethod method : methods) {
			method.validatePublicVoid(isStatic, errors);
		}
	}

}
