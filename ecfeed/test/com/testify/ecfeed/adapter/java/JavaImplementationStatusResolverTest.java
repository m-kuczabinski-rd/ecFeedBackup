package com.testify.ecfeed.adapter.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.junit.annotations.expected;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
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
		CategoryNode parameter = new CategoryNode("parameter", type.getTypeName(), "0", false);
		EclipseModelBuilder builder = new EclipseModelBuilder();
		for(PartitionNode choice : builder.defaultPartitions(type.getTypeName())){
			parameter.addPartition(choice);
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
		PartitionNode choice = prepareChoice(abstractChoice, parameterImplemented, status);

		assertEquals(status, fResolver.getImplementationStatus(choice));
	}
	
	@Test
	public void parameterStatusTest(EParameterType type, boolean expected, int noOfChildren, EImplementedChildren implementedChoices, EImplementationStatus status){
//		System.out.println("parameterStatusTest("  + type + ", " + expected + ", " + noOfChildren + ", " + status + ")");
		CategoryNode parameter = createParameter(type, expected, noOfChildren, implementedChoices, status);
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
		RootNode project = new RootNode("project");
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
		CategoryNode arg1 = null;
		CategoryNode arg2 = null;
		CategoryNode arg3 = null;
		switch(signature){
		case NO_PARAMETERS_NAME_IMPLEMENTED:
			method = new MethodNode("implementedTestMethod");
			break;
		case NO_PARAMETERS_NAME_NOT_IMPLEMENTED:
			method = new MethodNode("notImplementedTestMethod");
			break;
		case PARAMETERS_NAME_AND_PARAMETERS_IMPLEMENTED:
			method = new MethodNode("implementedTestMethod");
			arg1 = new CategoryNode("arg1", ImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new CategoryNode("arg2", ImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new CategoryNode("arg3", ImplementedUserType.class.getCanonicalName(), "", false);
			break;
		case PARAMETERS_NAME_IMPLEMENTED:
			method = new MethodNode("implementedTestMethod");
			arg1 = new CategoryNode("arg1", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new CategoryNode("arg2", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new CategoryNode("arg3", OtherImplementedUserType.class.getCanonicalName(), "", false);
			break;
		case PARAMETERS_NOTHING_IMPLEMENTED:
			method = new MethodNode("notImplementedTestMethod");
			arg1 = new CategoryNode("arg1", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new CategoryNode("arg2", OtherImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new CategoryNode("arg3", OtherImplementedUserType.class.getCanonicalName(), "", false);
			break;
		case PARAMETERS_TYPES_IMPLEMENTED:
			method = new MethodNode("notImplementedTestMethod");
			arg1 = new CategoryNode("arg1", ImplementedUserType.class.getCanonicalName(), "", false);
			arg2 = new CategoryNode("arg2", ImplementedUserType.class.getCanonicalName(), "", false);
			arg3 = new CategoryNode("arg3", ImplementedUserType.class.getCanonicalName(), "", false);
			break;
		}
		
		switch(childrenStatus){
		case ALL_IMPLEMENTED:
			arg1.addPartition(new PartitionNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg2.addPartition(new PartitionNode("choice 2", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg3.addPartition(new PartitionNode("choice 3", ImplementedUserType.IMPLEMENTED_VALUE.name()));
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
			arg1.addPartition(new PartitionNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg3.setType("UnimplementedType");
			break;
		case NO_CHILDREN:
			break;
		case SOME_IMPLEMENTED_REST_NOT_IMPLEMENTED:
			arg1.addPartition(new PartitionNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			arg2.setType("UnimplementedType");
			arg3.setType("UnimplementedType");
			break;
		case SOME_IMPLEMENTED_REST_PARTLY_IMPLEMENTED:
			arg1.addPartition(new PartitionNode("choice 1", ImplementedUserType.IMPLEMENTED_VALUE.name()));
			break;
		case SOME_PARTLY_IMPLEMENTED_REST_NOT_IMPLEMENTED:
			arg2.setType("UnimplementedType");
			arg3.setType("UnimplementedType");
			break;
		}
		if(childrenStatus != EChildrenStatus.NO_CHILDREN){
			method.addCategory(arg1);
			method.addCategory(arg2);
			method.addCategory(arg3);
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
		List<PartitionNode> testData = new ArrayList<PartitionNode>();
		for(int i = 0; i < noOfChoices; ++i){
			if(implementedChoices == EImplementedChildren.ALL || (implementedChoices == EImplementedChildren.SOME && i == 1)){
				CategoryNode category = new CategoryNode(String.valueOf(i), "int", "0", false);
				PartitionNode choice = new PartitionNode(String.valueOf(i), String.valueOf(i));
				category.addPartition(choice);
				testData.add(choice);
			}
			else{
				CategoryNode category = new CategoryNode(String.valueOf(i), "dummy", "0", false);
				PartitionNode choice = new PartitionNode(String.valueOf(i), String.valueOf(i));
				category.addPartition(choice);
				testData.add(choice);
			}
			
		}
		return new TestCaseNode("dummy", testData);
	}

	@SuppressWarnings("incomplete-switch")
	protected PartitionNode prepareChoice(boolean abstractChoice, boolean parameterImplemented, EImplementationStatus status){
		CategoryNode parameter = createEmptyParameter(parameterImplemented); 
		PartitionNode choice = new PartitionNode("choice", "unimplemented value");
		parameter.addPartition(choice);
		if(abstractChoice == false){
			if(status == EImplementationStatus.IMPLEMENTED){
				choice.setValueString(ImplementedUserType.IMPLEMENTED_VALUE.name());
			}
		}
		else{
			PartitionNode child1 = new PartitionNode("child1", "unimplemented value");
			PartitionNode child2 = new PartitionNode("child2", "unimplemented value");
			choice.addPartition(child1);
			choice.addPartition(child2);
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
	
	protected CategoryNode createEmptyParameter(boolean implemented){
		String type = implemented ? ImplementedUserType.class.getCanonicalName() : "UnimplementedType";
		return new CategoryNode("parameter", type, "IRRELEVANT", false);
	}
	
	protected CategoryNode createParameter(EParameterType type, boolean expected, int noOfChildren, EImplementedChildren implementedChoices, EImplementationStatus status){
		CategoryNode parameter = new CategoryNode("name", "dummy", "dummy", expected);
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
				parameter.addPartition(new PartitionNode(String.valueOf(i), String.valueOf(i)));
			}
			else if(implementedChoices == EImplementedChildren.NONE || (implementedChoices == EImplementedChildren.SOME && i == 0)){
				parameter.addPartition(new PartitionNode(String.valueOf(i), "dummy"));
			}
			else{
				parameter.addPartition(new PartitionNode(String.valueOf(i), ImplementedUserType.IMPLEMENTED_VALUE.name()));
			}
		}
		
		return parameter;
	}
	
}
