/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.ecfeed.core.adapter.java.JavaPrimitiveTypePredicate;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;


public class CopyNodeTest{

	@Test
	public void copyRootTest(){
		RootNode root = new RootNode("name", ModelVersionDistributor.getCurrentVersion());
		ClassNode class1 = new ClassNode("class1");
		ClassNode class2 = new ClassNode("class2");
		GlobalParameterNode par1 = new GlobalParameterNode("par1", "int");
		GlobalParameterNode par2 = new GlobalParameterNode("par2", "int");
		root.addClass(class1);
		root.addClass(class2);
		root.addParameter(par1);
		root.addParameter(par2);

		RootNode copy = root.getCopy();
		assertTrue(root.compare(copy));
	}

	@Test
	public void copyClassTest(){
		ClassNode classNode = new ClassNode("Class");
		MethodNode method1 = new MethodNode("method1");
		MethodNode method2 = new MethodNode("method2");
		GlobalParameterNode par1 = new GlobalParameterNode("par1", "int");
		GlobalParameterNode par2 = new GlobalParameterNode("par2", "int");
		classNode.addMethod(method1);
		classNode.addMethod(method2);
		classNode.addParameter(par1);
		classNode.addParameter(par2);

		ClassNode copy = classNode.getCopy();
		assertTrue(classNode.compare(copy));
	}

	@Test
	public void copyMethodTest(){
		MethodNode method = new MethodNode("method");
		MethodParameterNode par1 = new MethodParameterNode("par1", "int", "0", false);
		MethodParameterNode par2 = new MethodParameterNode("par1", "int", "0", true);
		ConstraintNode constraint1 = new ConstraintNode("constraint1", new Constraint(new StaticStatement(true), new StaticStatement(true)));
		ConstraintNode constraint2 = new ConstraintNode("constraint2", new Constraint(new StaticStatement(true), new StaticStatement(true)));
		ChoiceNode choice1 = new ChoiceNode("choice1", "0");
		par1.addChoice(choice1);
		ChoiceNode expectedChoice1 = new ChoiceNode("expected", "0");
		expectedChoice1.setParent(par2);
		ChoiceNode expectedChoice2 = new ChoiceNode("expected", "2");
		expectedChoice2.setParent(par2);
		TestCaseNode testCase1 = new TestCaseNode("test case 1", Arrays.asList(new ChoiceNode[]{choice1, expectedChoice1}));
		TestCaseNode testCase2 = new TestCaseNode("test case 1", Arrays.asList(new ChoiceNode[]{choice1, expectedChoice2}));

		method.addParameter(par1);
		method.addParameter(par2);
		method.addConstraint(constraint1);
		method.addConstraint(constraint2);
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);

		MethodNode copy = method.getCopy();
		assertTrue(method.compare(copy));
	}

	@Test
	public void copyGlobalParameterTest(){
		GlobalParameterNode parameter = new GlobalParameterNode("parameter", "int");
		ChoiceNode choice1 = new ChoiceNode("choice1", "1");
		ChoiceNode choice11 = new ChoiceNode("choice11", "11");
		ChoiceNode choice12 = new ChoiceNode("choice12", "12");
		ChoiceNode choice2 = new ChoiceNode("choice1", "2");
		ChoiceNode choice21 = new ChoiceNode("choice11", "21");
		ChoiceNode choice22 = new ChoiceNode("choice12", "22");
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice1.addChoice(choice21);
		choice1.addChoice(choice22);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		GlobalParameterNode copy = parameter.getCopy();
		assertTrue(parameter.compare(copy));
	}

	@Test
	public void copyMethodParameterTest(){
		MethodParameterNode parameter = new MethodParameterNode("parameter", "int", "0", false);
		ChoiceNode choice1 = new ChoiceNode("choice1", "1");
		ChoiceNode choice11 = new ChoiceNode("choice11", "11");
		ChoiceNode choice12 = new ChoiceNode("choice12", "12");
		ChoiceNode choice2 = new ChoiceNode("choice1", "2");
		ChoiceNode choice21 = new ChoiceNode("choice11", "21");
		ChoiceNode choice22 = new ChoiceNode("choice12", "22");
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice1.addChoice(choice21);
		choice1.addChoice(choice22);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		MethodParameterNode copy = parameter.getCopy();
		assertTrue(parameter.compare(copy));
	}

	@Test
	public void copyConstraintTest(){
		MethodNode method = new MethodNode("method");
		MethodParameterNode par1 = new MethodParameterNode("par1", "int", "0", false);
		MethodParameterNode par2 = new MethodParameterNode("par1", "int", "0", true);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0");
		choice1.addLabel("label");
		par1.addChoice(choice1);

		ChoiceNode expectedChoice = new ChoiceNode("expected", "0");
		expectedChoice.setParent(par2);

		method.addParameter(par1);
		method.addParameter(par2);

		StatementArray premise = new StatementArray(EStatementOperator.OR);
		premise.addStatement(new StaticStatement(true));
		premise.addStatement(new ChoicesParentStatement(par1, EStatementRelation.EQUAL, choice1));
		premise.addStatement(new ChoicesParentStatement(par1, EStatementRelation.NOT, "label"));
		ExpectedValueStatement consequence = new ExpectedValueStatement(par2, expectedChoice, new JavaPrimitiveTypePredicate());

		ConstraintNode constraint = new ConstraintNode("constraint", new Constraint(premise, consequence));
		method.addConstraint(constraint);

		ConstraintNode copy = constraint.getCopy();
		assertTrue(constraint.compare(copy));
	}

	@Test
	public void copyTestCaseTest(){
		MethodNode method = new MethodNode("method");
		MethodParameterNode par1 = new MethodParameterNode("par1", "int", "0", false);
		MethodParameterNode par2 = new MethodParameterNode("par1", "int", "0", true);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0");
		par1.addChoice(choice1);
		ChoiceNode expectedChoice1 = new ChoiceNode("expected", "0");
		expectedChoice1.setParent(par2);
		ChoiceNode expectedChoice2 = new ChoiceNode("expected", "2");
		expectedChoice2.setParent(par2);
		TestCaseNode testCase = new TestCaseNode("test case 1", Arrays.asList(new ChoiceNode[]{choice1, expectedChoice1}));

		method.addParameter(par1);
		method.addParameter(par2);
		method.addTestCase(testCase);

		TestCaseNode copy = testCase.getCopy();
		assertTrue(testCase.compare(copy));
	}

	@Test
	public void copyChoiceTest(){
		ChoiceNode choice = new ChoiceNode("choice", "0");
		ChoiceNode choice1 = new ChoiceNode("choice1", "0");
		ChoiceNode choice11 = new ChoiceNode("choice11", "0");
		ChoiceNode choice12 = new ChoiceNode("choice12", "0");
		ChoiceNode choice2 = new ChoiceNode("choice2", "0");
		ChoiceNode choice21 = new ChoiceNode("choice21", "0");
		ChoiceNode choice22 = new ChoiceNode("choice22", "0");

		choice.addChoice(choice1);
		choice.addChoice(choice2);
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice2.addChoice(choice21);
		choice2.addChoice(choice22);

		ChoiceNode copy = choice.getCopy();
		assertTrue(choice.compare(copy));
	}

	@Test
	public void copyStaticStatementTest(){
		StaticStatement statement1 = new StaticStatement(true);
		StaticStatement statement2 = new StaticStatement(false);

		StaticStatement copy1 = statement1.getCopy();
		StaticStatement copy2 = statement2.getCopy();

		assertTrue(statement1.compare(copy1));
		assertTrue(statement2.compare(copy2));
	}

	@Test
	public void copyStatementArrayTest(){
		for(EStatementOperator operator : new EStatementOperator[]{EStatementOperator.AND, EStatementOperator.OR}){
			StatementArray array = new StatementArray(operator);
			array.addStatement(new StaticStatement(true));
			array.addStatement(new StaticStatement(false));
			array.addStatement(new StaticStatement(true));
			array.addStatement(new StaticStatement(false));

			StatementArray copy = array.getCopy();
			assertTrue(array.compare(copy));
		}
	}

	@Test
	public void choiceStatementTest(){
		MethodParameterNode parameter = new MethodParameterNode("parameter", "int", "65", false);
		ChoiceNode choice = new ChoiceNode("choice", "876");
		parameter.addChoice(choice);
		choice.addLabel("label");

		ChoicesParentStatement statement1 = new ChoicesParentStatement(parameter, EStatementRelation.EQUAL, choice);
		ChoicesParentStatement statement2 = new ChoicesParentStatement(parameter, EStatementRelation.EQUAL, "label");

		ChoicesParentStatement copy1 = statement1.getCopy();
		ChoicesParentStatement copy2 = statement2.getCopy();

		assertTrue(statement1.compare(copy1));
		assertTrue(statement2.compare(copy2));
	}

	@Test
	public void expectedStatementTest(){
		MethodParameterNode parameter = new MethodParameterNode("parameter", "int", "65", true);
		ChoiceNode choice = new ChoiceNode("expected", "876");
		choice.setParent(parameter);

		ExpectedValueStatement statement = new ExpectedValueStatement(parameter, choice, new JavaPrimitiveTypePredicate());
		ExpectedValueStatement copy = statement.getCopy();
		assertTrue(statement.compare(copy));
	}
	//	RootNode fRoot;
	//
	//	ClassNode fClass1;
	//	ClassNode fClass2;
	//	MethodNode fMethod1;
	//	MethodNode fMethod2;
	//	MethodParameterNode fPartCat1;
	//	MethodParameterNode fPartCat2;
	//	MethodParameterNode fExCat1;
	//	MethodParameterNode fExCat2;
	//	ChoiceNode fChoice1;
	//	ChoiceNode fChoice2;
	//	ChoiceNode fChoice3;
	//
	//	String fLabel1;
	//	String fLabel2;
	//
	//	// ConstraintNode fConNode1;
	//	// ConstraintNode fConNode2;
	//
	//	@Before
	//	public void setup(){
	//		fRoot = new RootNode("Model");
	//		fClass1 = new ClassNode("com.ecfeed.model.Class1");
	//		fClass2 = new ClassNode("com.ecfeed.model.Class2");
	//		fMethod1 = new MethodNode("firstMethod");
	//		fMethod2 = new MethodNode("secondMethod");
	//		fPartCat1 = new MethodParameterNode("pcat1", "type", "0", false);
	//		fPartCat2 = new MethodParameterNode("pcat2", "type2", "0", false);
	//		fExCat1 = new MethodParameterNode("ecat1", "type", "0", true);
	//		fExCat1.setDefaultValueString("value1");
	//		fExCat2 = new MethodParameterNode("ecat2", "type", "0", true);
	//		fExCat2.setDefaultValueString("value2");
	//		fChoice1 = new ChoiceNode("p1", "value1");
	//		fChoice2 = new ChoiceNode("p2", "value2");
	//		fChoice3 = new ChoiceNode("p3", "value3");
	//		fLabel1 = "label1";
	//		fLabel2 = "label2";
	//
	//		fRoot.addClass(fClass1);
	//		fRoot.addClass(fClass2);
	//		fClass1.addMethod(fMethod1);
	//		fClass2.addMethod(fMethod2);
	//		fMethod1.addParameter(fPartCat1);
	//		fMethod1.addParameter(fExCat1);
	//		fMethod2.addParameter(fPartCat2);
	//		fMethod2.addParameter(fExCat2);
	//		fPartCat1.addChoice(fChoice1);
	//		fPartCat2.addChoice(fChoice3);
	//		fChoice1.addChoice(fChoice2);
	//		fChoice1.addLabel(fLabel1);
	//		fChoice2.addLabel(fLabel2);
	//	}
	//
	//	public void testNode(AbstractNode node, AbstractNode copy){
	//		assertTrue(node.getClass().isInstance(copy));
	//		assertNotEquals(node, copy);
	//		assertEquals(node.getName(), copy.getName());
	//	}
	//
	//	public void testParent(AbstractNode node, AbstractNode parent, boolean isParent){
	//		if(isParent)
	//			assertEquals(node.getParent(), parent);
	//		else
	//			assertNotEquals(node.getParent(), parent);
	//	}
	//
	//	public void testChoices(ChoiceNode choice, ChoiceNode copy){
	//		testNode(choice, copy);
	//		assertEquals(choice.getValueString(), copy.getValueString());
	//	}
	//
	//	public void testChoiceLabels(ChoiceNode choice, ChoiceNode copy){
	//		assertEquals(choice.getLabels().size(), copy.getLabels().size());
	//		assertEquals(choice.getChildren().size(), copy.getChildren().size());
	//		// contains all and no more labels?
	//		assertTrue(copy.getLabels().containsAll(choice.getLabels()));
	//		assertTrue(choice.getLabels().containsAll(copy.getLabels()));
	//	}
	//
	//	public void testChoiceChildrenLabels(ChoiceNode childcopy, String parentlabel, String childlabel){
	//		assertTrue(childcopy.getLabels().contains(childlabel));
	//		assertTrue(childcopy.getAllLabels().contains(parentlabel));
	//	}
	//
	//	@Test
	//	public void choiceCopyTest(){
	//		// single choice copied properly?
	//		ChoiceNode copy = fChoice3.getCopy();
	//		testChoices(fChoice3, copy);
	//		testParent(copy, fChoice3.getParent(), true);
	//		// hierarchical choice copy tests
	//		// labels copied properly?
	//		copy = fChoice1.getCopy();
	//		testChoiceLabels(fChoice1, fChoice1.getCopy());
	//		// children copied properly?
	//		ChoiceNode childcopy = (ChoiceNode)copy.getChild(fChoice2.getName());
	//		testChoices(fChoice2, childcopy);
	//		testParent(childcopy, copy, true);
	//		testParent(fChoice2, childcopy.getParent(), false);
	//
	//		// children labels copied properly?
	//		testChoiceChildrenLabels(childcopy, fLabel1, fLabel2);
	//	}
	//
	//	public void testChoicesParentParameters(MethodParameterNode parameter, MethodParameterNode copy, String parentlabel, String childlabel){
	//		testNode(parameter, copy);
	//		assertEquals(parameter.getChildren().size(), copy.getChildren().size());
	//		assertEquals(parameter.getAllChoiceNames().size(), copy.getAllChoiceNames().size());
	//
	//		// choices copied properly?
	//		ChoiceNode choice = parameter.getChoices().get(0);
	//		ChoiceNode choicecopy = copy.getChoice(choice.getName());
	//		testChoices(choicecopy, choice);
	//		testParent(choicecopy, copy, true);
	//		// labels copied properly?
	//		assertTrue(copy.getLeafLabels().contains(parentlabel));
	//		assertTrue(copy.getLeafLabels().contains(childlabel));
	//		testChoiceLabels(choice, choicecopy);
	//		// children choices copied properly?
	//		ChoiceNode choiceChild = choice.getChoices().get(0);
	//		ChoiceNode choicecopyChild = choicecopy.getChoice(choiceChild.getName());
	//		testChoices(choicecopyChild, choiceChild);
	//		testParent(choicecopyChild, choicecopy, true);
	//		// children choice labels copied properly?
	//		testChoiceChildrenLabels(choicecopyChild, parentlabel, childlabel);
	//	}
	//
	//	@Test
	//	public void choicesParentParameterCopyTest(){
	//		MethodParameterNode copy = fPartCat1.getCopy();
	//		// parameters copied properly?
	//		testChoicesParentParameters(fPartCat1, copy, fLabel1, fLabel2);
	//		testParent(copy, fPartCat1.getParent(), true);
	//	}
	//
	//	public void testExpectedParameters(MethodParameterNode parameter, MethodParameterNode copy){
	//		testNode(parameter, copy);
	//		String choice = parameter.getDefaultValue();
	//		String choicecopy = copy.getDefaultValue();
	//		assertEquals(choice, choicecopy);
	////		testChoices(choice, choicecopy);
	////		testParent(choice, choicecopy.getParent(), false);
	//	}
	//
	//	@Test
	//	public void expectedParameterCopyTest(){
	//		MethodParameterNode copy = fExCat1.getCopy();
	//		testExpectedParameters(fExCat1, copy);
	//		testParent(copy, fExCat1.getParent(), true);
	//	}
	//
	//	public void testMethods(MethodNode method, MethodNode copy, String parentlabel, String childlabel){
	//		testNode(method, copy);
	//		// Test choices parent parameter
	//		MethodParameterNode partcat = method.getMethodParameters(false).get(0);
	//		MethodParameterNode copypartcat = copy.getMethodParameter(partcat.getName());
	//		testChoicesParentParameters(partcat, copypartcat, parentlabel, childlabel);
	//		testParent(copypartcat, copy, true);
	//		// Test expected parameter
	//		MethodParameterNode expcat = method.getMethodParameters(true).get(0);
	//		MethodParameterNode copyexpcat = copy.getMethodParameter(expcat.getName());
	//		testExpectedParameters(fExCat1, copyexpcat);
	//		testParent(copyexpcat, copy, true);
	//	}
	//
	//	@Test
	//	public void methodCopyTest(){
	//		MethodNode copy = fMethod1.getCopy();
	//		if(copy == null)System.out.println("COPY!!!");
	//		testMethods(fMethod1, copy, fLabel1, fLabel2);
	//		testParent(fMethod1, copy.getParent(), true);
	//	}
	//
	//	public void testClasses(ClassNode classnode, ClassNode copy, String parentlabel, String childlabel){
	//		testNode(classnode, copy);
	//
	//		MethodNode method = classnode.getMethods().get(0);
	//		MethodNode copymeth = copy.getMethod(method.getName(), method.getParametersTypes());
	//
	//		testMethods(method, copymeth, parentlabel, childlabel);
	//		testParent(copymeth, copy, true);
	//	}
	//
	//	@Test
	//	public void classCopyTest(){
	//		ClassNode copy = fClass1.getCopy();
	//		testClasses(fClass1, copy, fLabel1, fLabel2);
	//		testParent(copy, fClass1.getParent(), true);
	//	}
	//
	//	public void testRoots(RootNode root, RootNode copy, String parentlabel, String childlabel){
	//		testNode(root, copy);
	//
	//		ClassNode classnode = root.getClasses().get(0);
	//		ClassNode copyclass = copy.getClassModel(classnode.getName());
	//
	//		testClasses(classnode, copyclass, parentlabel, childlabel);
	//		testParent(copyclass, copy, true);
	//	}
	//
	//	@Test
	//	public void rootCopyTest(){
	//		RootNode copy = fRoot.getCopy();
	//		testRoots(fRoot, copy, fLabel1, fLabel2);
	//		this.testParent(copy, null, true);
	//	}
	//
}