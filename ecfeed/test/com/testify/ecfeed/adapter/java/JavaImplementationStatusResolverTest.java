package com.testify.ecfeed.adapter.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.java.ILoaderProvider;
import com.ecfeed.core.adapter.java.JavaImplementationStatusResolver;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.junit.annotations.expected;
import com.testify.ecfeed.testutils.ETypeName;
import com.testify.ecfeed.testutils.RandomModelGenerator;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;


@RunWith(OnlineRunner.class)
@EcModel("test/com.testify.ecfeed.adapter.java.ect")
@Constraints(Constraints.ALL)
@Generator(CartesianProductGenerator.class)
public class JavaImplementationStatusResolverTest {
	private JavaImplementationStatusResolver fResolver;

	public enum ImplementedUserType{
		IMPLEMENTED_VALUE
	}

	public enum OtherImplementedUserType{
		IMPLEMENTED_VALUE
	}

	public class ImplementedClass{
		public void implementedTestMethod0(){}
		public void implementedTestMethod1(){}
		public void implementedTestMethod2(){}
		public void implementedTestMethod3(){}
		public void implementedTestMethod4(){}

		public void implementedTestMethod(){}
		public void implementedOtherTestMethod(boolean arg1){}
		public void implementedTestMethod(ImplementedUserType arg1, ImplementedUserType arg2, ImplementedUserType arg3){}
	}

	public enum EParameterType{
		PRIMITIVE, IMPLEMENTED_USER_TYPE, UNIMPLEMENTED_USER_TYPE
	}

	public enum EImplementedChildren{
		NONE, SOME, SOME_PARTLY, ALL
	}

	public enum EChildrenStatus{
		NO_CHILDREN,
		ALL_NOT_IMPLEMENTED,
		SOME_PARTLY_IMPLEMENTED_REST_NOT_IMPLEMENTED,
		ALL_PARTLY_IMPLEMENTED,
		SOME_IMPLEMENTED_REST_NOT_IMPLEMENTED,
		SOME_IMPLEMENTED_REST_PARTLY_IMPLEMENTED,
		ALL_IMPLEMENTED,
		FULL_MIX
	}

	public enum EImplementedSignatureElements{
		PARAMETERS_NOTHING_IMPLEMENTED,
		PARAMETERS_NAME_IMPLEMENTED,
		PARAMETERS_TYPES_IMPLEMENTED,
		PARAMETERS_NAME_AND_PARAMETERS_IMPLEMENTED,
		NO_PARAMETERS_NAME_IMPLEMENTED,
		NO_PARAMETERS_NAME_NOT_IMPLEMENTED
	}

	private class LoaderProvider implements ILoaderProvider{
		@Override
		public ModelClassLoader getLoader(boolean create, ClassLoader parent) {
			try{
				if(parent == null){
					parent = this.getClass().getClassLoader();
				}
				URL[] urls = ((URLClassLoader)parent).getURLs();
				return new ModelClassLoader(urls, parent);
			}catch(Exception e){
				fail("Unexpected exception: " + e.getMessage());
			}
			return null;
		}

	}

	public JavaImplementationStatusResolverTest(){
		fResolver = new JavaImplementationStatusResolver(new LoaderProvider());
	}

	@Test
	public void primitiveChoiceStatusTest(ETypeName type){
		MethodParameterNode parameter = new MethodParameterNode("parameter", type.getTypeName(), "0", false);
		EclipseModelBuilder builder = new EclipseModelBuilder();
		for(ChoiceNode choice : builder.defaultChoices(type.getTypeName())){
			parameter.addChoice(choice);
			assertEquals(EImplementationStatus.IMPLEMENTED, fResolver.getImplementationStatus(choice));
		}
	}

	@Test
	public void userTypeChoiceStatusTest(boolean abstractChoice, boolean parameterImplemented, EImplementationStatus status){
		//		System.out.println("userTypeChoiceStatusTest(" + abstractChoice + ", " + parameterImplemented + ", " + status + ")");
		if(abstractChoice == false && status == EImplementationStatus.PARTIALLY_IMPLEMENTED ||
				parameterImplemented == false && status != EImplementationStatus.NOT_IMPLEMENTED ||
				status == EImplementationStatus.IRRELEVANT){
			//invalid combination
			return;
		}
		ChoiceNode choice = prepareChoice(abstractChoice, parameterImplemented, status);

		assertEquals(status, fResolver.getImplementationStatus(choice));
	}

	@Test
	public void parameterStatusTest(EParameterType type, boolean expected, int noOfChildren, EImplementedChildren implementedChoices, EImplementationStatus status){
		//		System.out.println("parameterStatusTest("  + type + ", " + expected + ", " + noOfChildren + ", " + status + ")");
		MethodParameterNode parameter = createParameter(type, expected, noOfChildren, implementedChoices, status);
		EImplementationStatus resolvedStatus = fResolver.getImplementationStatus(parameter);
		assertEquals(status, resolvedStatus);

		//		System.out.println("OK");
	}

	@Test
	public void testCaseStatusTest(int noOfChoices, EImplementedChildren implementedChoices, EImplementationStatus status){
		TestCaseNode testCase = prepareTestCase(noOfChoices, implementedChoices);
		EImplementationStatus resolvedStatus = fResolver.getImplementationStatus(testCase);
		//		System.out.println("testCaseStatusTest(" + noOfChoices + ", " + implementedChoices + ", " + status + ") resolved to " + resolvedStatus);
		assertEquals(status, resolvedStatus);
	}

	@Test
	public void constraintStatusTest(){
		RandomModelGenerator generator = new RandomModelGenerator();
		MethodNode method = generator.generateMethod(3, 10, 0);
		for(ConstraintNode constraint : method.getConstraintNodes()){
			assertEquals(fResolver.getImplementationStatus(constraint), EImplementationStatus.IRRELEVANT);
		}
	}

	@Test
	public void methodStatusTest(boolean parentDefinitionInplemented, EImplementedSignatureElements signature, EChildrenStatus childrenStatus, EImplementationStatus status){
		//		System.out.println("methodStatusTest(" + parentDefinitionInplemented + ", " + signature + ", " + childrenStatus + ", " + status + ")");
		//		MethodNode method = prepareMethod(true, EImplementedSignatureElements.PARAMETERS_NOTHING_IMPLEMENTED, EChildrenStatus.ALL_NOT_IMPLEMENTED);
		MethodNode method = prepareMethod(parentDefinitionInplemented, signature, childrenStatus);
		EImplementationStatus resolvedStatus = fResolver.getImplementationStatus(method);
		//		System.out.print("Method " + method + " resolved to " + resolvedStatus + "\n");
		assertEquals(status, resolvedStatus);
	}

	@Test
	public void classStatusTest(boolean classDefinitionImplemented, int noOfMethods, EImplementedChildren implementedMethods, @expected EImplementationStatus status){
		//		System.out.print("classStatusTest(" + classDefinitionImplemented + ", " + noOfMethods + ", " + implementedMethods + ", " + status + ")");
		ClassNode _class = prepareClass(classDefinitionImplemented, noOfMethods, implementedMethods);
		EImplementationStatus resolvedStatus = fResolver.getImplementationStatus(_class);
		//		System.out.print(" resolved to " + resolvedStatus + "\n");
		assertEquals(status, resolvedStatus);
	}

	@Test 
	public void projectStatusTest(EChildrenStatus classes, @expected EImplementationStatus status){
		//		System.out.println("projectStatusTest(" + classes + ", " +  status + ")");
		RootNode project = prepareProject(classes);
		EImplementationStatus resolvedStatus = fResolver.getImplementationStatus(project);
		assertEquals(status, resolvedStatus);
	}

	private RootNode prepareProject(EChildrenStatus classes) {
		RootNode project = new RootNode("project", ModelVersionDistributor.getCurrentVersion());
		if(classes == EChildrenStatus.NO_CHILDREN){
			return project;
		}
		ClassNode class1 = new ClassNode("UnimplementedClass");
		ClassNode class2 = new ClassNode("UnimplementedClass");;
		ClassNode class3 = new ClassNode("UnimplementedClass");;
		switch(classes){
		case ALL_IMPLEMENTED:
			class1.setName(ImplementedClass.class.getCanonicalName());
			class2.setName(ImplementedClass.class.getCanonicalName());
			class3.setName(ImplementedClass.class.getCanonicalName());
			break;
		case ALL_NOT_IMPLEMENTED:
			break;
		case ALL_PARTLY_IMPLEMENTED:
			class1.setName(ImplementedClass.class.getCanonicalName());
			class1.addMethod(new MethodNode("unimplementedMethod"));
			class2.setName(ImplementedClass.class.getCanonicalName());
			class2.addMethod(new MethodNode("unimplementedMethod"));
			class3.setName(ImplementedClass.class.getCanonicalName());
			class3.addMethod(new MethodNode("unimplementedMethod"));
			break;
		case FULL_MIX:
			class1.setName(ImplementedClass.class.getCanonicalName());
			class2.setName(ImplementedClass.class.getCanonicalName());
			class2.addMethod(new MethodNode("unimplementedMethod"));
			break;
		case NO_CHILDREN:
			break;
		case SOME_IMPLEMENTED_REST_NOT_IMPLEMENTED:
			class1.setName(ImplementedClass.class.getCanonicalName());
			break;
		case SOME_IMPLEMENTED_REST_PARTLY_IMPLEMENTED:
			class1.setName(ImplementedClass.class.getCanonicalName());
			class2.setName(ImplementedClass.class.getCanonicalName());
			class2.addMethod(new MethodNode("unimplementedMethod"));
			class3.setName(ImplementedClass.class.getCanonicalName());
			class3.addMethod(new MethodNode("unimplementedMethod"));
			break;
		case SOME_PARTLY_IMPLEMENTED_REST_NOT_IMPLEMENTED:
			class2.setName(ImplementedClass.class.getCanonicalName());
			class2.addMethod(new MethodNode("unimplementedMethod"));
			class3.setName(ImplementedClass.class.getCanonicalName());
			class3.addMethod(new MethodNode("unimplementedMethod"));
			break;
		}

		project.addClass(class1);
		project.addClass(class2);
		project.addClass(class3);
		return project;
	}

	private MethodNode prepareMethod(boolean parentDefinitionInplemented, EImplementedSignatureElements signature, EChildrenStatus childrenStatus) {
		ClassNode _class = new ClassNode("dummy.name");
		if(parentDefinitionInplemented){
			_class.setName(ImplementedClass.class.getCanonicalName());
		}
		MethodNode method = null;
		MethodParameterNode arg1 = null;
		MethodParameterNode arg2 = null;
		MethodParameterNode arg3 = null;
		switch(signature){
		case NO_PARAMETERS_NAME_IMPLEMENTED:
			method = new MethodNode("implementedTestMethod");
			break;
		case NO_PARAMETERS_NAME_NOT_IMPLEMENTED:
			method = new MethodNode("notImplementedTestMethod");
			break;
		case PARAMETERS_NAME_AND_PARAMETERS_IMPLEMENTED:
			method = new MethodNode("implementedTestMethod");
			arg1 = new MethodParameterNode("arg1", ImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new MethodParameterNode("arg2", ImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new MethodParameterNode("arg3", ImplementedUserType.class.getCanonicalName(), "", false);
			break;
		case PARAMETERS_NAME_IMPLEMENTED:
			method = new MethodNode("implementedTestMethod");
			arg1 = new MethodParameterNode("arg1", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new MethodParameterNode("arg2", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new MethodParameterNode("arg3", OtherImplementedUserType.class.getCanonicalName(), "", false);
			break;
		case PARAMETERS_NOTHING_IMPLEMENTED:
			method = new MethodNode("notImplementedTestMethod");
			arg1 = new MethodParameterNode("arg1", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new MethodParameterNode("arg2", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new MethodParameterNode("arg3", OtherImplementedUserType.class.getCanonicalName(), "", false);
			break;
		case PARAMETERS_TYPES_IMPLEMENTED:
			method = new MethodNode("notImplementedTestMethod");
			arg1 = new MethodParameterNode("arg1", ImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new MethodParameterNode("arg2", ImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new MethodParameterNode("arg3", ImplementedUserType.class.getCanonicalName(), "", false);
			break;
		}

		switch(childrenStatus){
		case ALL_IMPLEMENTED:
			arg1.addChoice(new ChoiceNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg2.addChoice(new ChoiceNode("choice 2", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg3.addChoice(new ChoiceNode("choice 3", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			break;
		case ALL_NOT_IMPLEMENTED:
			arg1.setType("UnimplementedType");
			arg2.setType("UnimplementedType");
			arg3.setType("UnimplementedType");
			break;
		case ALL_PARTLY_IMPLEMENTED:
			//by default parameter with no choices is partly implemented
			break;
		case FULL_MIX:
			arg1.addChoice(new ChoiceNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg3.setType("UnimplementedType");
			break;
		case NO_CHILDREN:
			break;
		case SOME_IMPLEMENTED_REST_NOT_IMPLEMENTED:
			arg1.addChoice(new ChoiceNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg2.setType("UnimplementedType");
			arg3.setType("UnimplementedType");
			break;
		case SOME_IMPLEMENTED_REST_PARTLY_IMPLEMENTED:
			arg1.addChoice(new ChoiceNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			break;
		case SOME_PARTLY_IMPLEMENTED_REST_NOT_IMPLEMENTED:
			arg2.setType("UnimplementedType");
			arg3.setType("UnimplementedType");
			break;
		}
		if(childrenStatus != EChildrenStatus.NO_CHILDREN){
			method.addParameter(arg1);
			method.addParameter(arg2);
			method.addParameter(arg3);
		}

		_class.addMethod(method);
		return method;
	}

	private ClassNode prepareClass(boolean classDefinitionImplemented, int noOfMethods, EImplementedChildren implementedMethods) {
		if((classDefinitionImplemented == false && implementedMethods != EImplementedChildren.NONE)||
				(noOfMethods == 0 && implementedMethods != EImplementedChildren.NONE)){
			//invalid combination
			return null;
		}
		ClassNode _class = new ClassNode("dummy.name");
		if(classDefinitionImplemented){
			_class.setName(ImplementedClass.class.getCanonicalName());
		}
		for(int i = 0; i < noOfMethods; i++){
			MethodNode method = new MethodNode("name");
			if(implementedMethods == EImplementedChildren.ALL || (implementedMethods == EImplementedChildren.SOME && i == 1)){
				method.setName("implementedTestMethod" + String.valueOf(i));
			}
			_class.addMethod(method);
		}
		return _class;
	}

	private TestCaseNode prepareTestCase(int noOfChoices, EImplementedChildren implementedChoices) {
		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
		for(int i = 0; i < noOfChoices; ++i){
			if(implementedChoices == EImplementedChildren.ALL || (implementedChoices == EImplementedChildren.SOME && i == 1)){
				MethodParameterNode parameter = new MethodParameterNode(String.valueOf(i), "int", "0", false);
				ChoiceNode choice = new ChoiceNode(String.valueOf(i), String.valueOf(i));
				parameter.addChoice(choice);
				testData.add(choice);
			}
			else{
				MethodParameterNode parameter = new MethodParameterNode(String.valueOf(i), "dummy", "0", false);
				ChoiceNode choice = new ChoiceNode(String.valueOf(i), String.valueOf(i));
				parameter.addChoice(choice);
				testData.add(choice);
			}

		}
		return new TestCaseNode("dummy", testData);
	}

	@SuppressWarnings("incomplete-switch")
	protected ChoiceNode prepareChoice(boolean abstractChoice, boolean parameterImplemented, EImplementationStatus status){
		MethodParameterNode parameter = createEmptyParameter(parameterImplemented); 
		ChoiceNode choice = new ChoiceNode("choice", "unimplemented value");
		parameter.addChoice(choice);
		if(abstractChoice == false){
			if(status == EImplementationStatus.IMPLEMENTED){
				choice.setValueString(ImplementedUserType.IMPLEMENTED_VALUE.name());
			}
		}
		else{
			ChoiceNode child1 = new ChoiceNode("child1", "unimplemented value");
			ChoiceNode child2 = new ChoiceNode("child2", "unimplemented value");
			choice.addChoice(child1);
			choice.addChoice(child2);
			switch(status){
			case IMPLEMENTED:
				child1.setValueString(ImplementedUserType.IMPLEMENTED_VALUE.name());
				child2.setValueString(ImplementedUserType.IMPLEMENTED_VALUE.name());
				break;
			case PARTIALLY_IMPLEMENTED:
				child1.setValueString(ImplementedUserType.IMPLEMENTED_VALUE.name());
				break;
			}
		}

		return choice;
	}

	protected MethodParameterNode createEmptyParameter(boolean implemented){
		String type = implemented ? ImplementedUserType.class.getCanonicalName() : "UnimplementedType";
		return new MethodParameterNode("parameter", type, "IRRELEVANT", false);
	}

	protected MethodParameterNode createParameter(EParameterType type, boolean expected, int noOfChildren, EImplementedChildren implementedChoices, EImplementationStatus status){
		MethodParameterNode parameter = new MethodParameterNode("name", "dummy", "dummy", expected);
		switch(type){
		case IMPLEMENTED_USER_TYPE:
			parameter.setType(ImplementedUserType.class.getCanonicalName());
			break;
		case PRIMITIVE:
			parameter.setType("int");
			break;
		case UNIMPLEMENTED_USER_TYPE:
			break;
		}

		for(int i = 0; i < noOfChildren; i++){
			if(type == EParameterType.PRIMITIVE){
				parameter.addChoice(new ChoiceNode(String.valueOf(i), String.valueOf(i)));
			}
			else if(implementedChoices == EImplementedChildren.NONE || (implementedChoices == EImplementedChildren.SOME && i == 0)){
				parameter.addChoice(new ChoiceNode(String.valueOf(i), "dummy"));
			}
			else{
				parameter.addChoice(new ChoiceNode(String.valueOf(i), ImplementedUserType.IMPLEMENTED_VALUE.name()));
			}
		}

		return parameter;
	}

}
