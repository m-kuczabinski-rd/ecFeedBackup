/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CopyNodeTest{

	RootNode fRoot;

	ClassNode fClass1;
	ClassNode fClass2;
	MethodNode fMethod1;
	MethodNode fMethod2;
	ParameterNode fPartCat1;
	ParameterNode fPartCat2;
	ParameterNode fExCat1;
	ParameterNode fExCat2;
	ChoiceNode fChoice1;
	ChoiceNode fChoice2;
	ChoiceNode fChoice3;

	String fLabel1;
	String fLabel2;

	// ConstraintNode fConNode1;
	// ConstraintNode fConNode2;

	@Before
	public void setup(){
		fRoot = new RootNode("Model");
		fClass1 = new ClassNode("com.testify.ecfeed.model.Class1");
		fClass2 = new ClassNode("com.testify.ecfeed.model.Class2");
		fMethod1 = new MethodNode("firstMethod");
		fMethod2 = new MethodNode("secondMethod");
		fPartCat1 = new ParameterNode("pcat1", "type", "0", false);
		fPartCat2 = new ParameterNode("pcat2", "type2", "0", false);
		fExCat1 = new ParameterNode("ecat1", "type", "0", true);
		fExCat1.setDefaultValueString("value1");
		fExCat2 = new ParameterNode("ecat2", "type", "0", true);
		fExCat2.setDefaultValueString("value2");
		fChoice1 = new ChoiceNode("p1", "value1");
		fChoice2 = new ChoiceNode("p2", "value2");
		fChoice3 = new ChoiceNode("p3", "value3");
		fLabel1 = "label1";
		fLabel2 = "label2";

		fRoot.addClass(fClass1);
		fRoot.addClass(fClass2);
		fClass1.addMethod(fMethod1);
		fClass2.addMethod(fMethod2);
		fMethod1.addParameter(fPartCat1);
		fMethod1.addParameter(fExCat1);
		fMethod2.addParameter(fPartCat2);
		fMethod2.addParameter(fExCat2);
		fPartCat1.addChoice(fChoice1);
		fPartCat2.addChoice(fChoice3);
		fChoice1.addChoice(fChoice2);
		fChoice1.addLabel(fLabel1);
		fChoice2.addLabel(fLabel2);
	}

	public void testNode(AbstractNode node, AbstractNode copy){
		assertTrue(node.getClass().isInstance(copy));
		assertNotEquals(node, copy);
		assertEquals(node.getName(), copy.getName());
	}

	public void testParent(AbstractNode node, AbstractNode parent, boolean isParent){
		if(isParent)
			assertEquals(node.getParent(), parent);
		else
			assertNotEquals(node.getParent(), parent);
	}

	public void testChoices(ChoiceNode choice, ChoiceNode copy){
		testNode(choice, copy);
		assertEquals(choice.getValueString(), copy.getValueString());
	}

	public void testChoiceLabels(ChoiceNode choice, ChoiceNode copy){
		assertEquals(choice.getLabels().size(), copy.getLabels().size());
		assertEquals(choice.getChildren().size(), copy.getChildren().size());
		// contains all and no more labels?
		assertTrue(copy.getLabels().containsAll(choice.getLabels()));
		assertTrue(choice.getLabels().containsAll(copy.getLabels()));
	}

	public void testChoiceChildrenLabels(ChoiceNode childcopy, String parentlabel, String childlabel){
		assertTrue(childcopy.getLabels().contains(childlabel));
		assertTrue(childcopy.getAllLabels().contains(parentlabel));
	}

	@Test
	public void choiceCopyTest(){
		// single choice copied properly?
		ChoiceNode copy = fChoice3.getCopy();
		testChoices(fChoice3, copy);
		testParent(copy, fChoice3.getParent(), true);
		// hierarchical choice copy tests
		// labels copied properly?
		copy = fChoice1.getCopy();
		testChoiceLabels(fChoice1, fChoice1.getCopy());
		// children copied properly?
		ChoiceNode childcopy = (ChoiceNode)copy.getChild(fChoice2.getName());
		testChoices(fChoice2, childcopy);
		testParent(childcopy, copy, true);
		testParent(fChoice2, childcopy.getParent(), false);

		// children labels copied properly?
		testChoiceChildrenLabels(childcopy, fLabel1, fLabel2);
	}

	public void testDecomposedParameters(ParameterNode parameter, ParameterNode copy, String parentlabel, String childlabel){
		testNode(parameter, copy);
		assertEquals(parameter.getChildren().size(), copy.getChildren().size());
		assertEquals(parameter.getAllChoiceNames().size(), copy.getAllChoiceNames().size());

		// choices copied properly?
		ChoiceNode choice = parameter.getChoices().get(0);
		ChoiceNode choicecopy = copy.getChoice(choice.getName());
		testChoices(choicecopy, choice);
		testParent(choicecopy, copy, true);
		// labels copied properly?
		assertTrue(copy.getLeafLabels().contains(parentlabel));
		assertTrue(copy.getLeafLabels().contains(childlabel));
		testChoiceLabels(choice, choicecopy);
		// children choices copied properly?
		ChoiceNode choiceChild = choice.getChoices().get(0);
		ChoiceNode choicecopyChild = choicecopy.getChoice(choiceChild.getName());
		testChoices(choicecopyChild, choiceChild);
		testParent(choicecopyChild, choicecopy, true);
		// children choice labels copied properly?
		testChoiceChildrenLabels(choicecopyChild, parentlabel, childlabel);
	}

	@Test
	public void decomposedParameterCopyTest(){
		ParameterNode copy = fPartCat1.getCopy();
		// parameters copied properly?
		testDecomposedParameters(fPartCat1, copy, fLabel1, fLabel2);
		testParent(copy, fPartCat1.getParent(), true);
	}

	public void testExpectedParameters(ParameterNode parameter, ParameterNode copy){
		testNode(parameter, copy);
//		ChoiceNode choice = parameter.getDefaultValueChoice();
//		ChoiceNode choicecopy = copy.getDefaultValueChoice();
//		testChoices(choice, choicecopy);
//		testParent(choice, choicecopy.getParent(), false);
	}

	@Test
	public void expectedParameterCopyTest(){
		ParameterNode copy = fExCat1.getCopy();
		testExpectedParameters(fExCat1, copy);
		testParent(copy, fExCat1.getParent(), true);
	}

	public void testMethods(MethodNode method, MethodNode copy, String parentlabel, String childlabel){
		testNode(method, copy);
		// Test decomposed parameter
		ParameterNode partcat = method.getParameters(false).get(0);
		ParameterNode copypartcat = copy.getParameter(partcat.getName());
		testDecomposedParameters(partcat, copypartcat, parentlabel, childlabel);
		testParent(copypartcat, copy, true);
		// Test expected parameter
		ParameterNode expcat = method.getParameters(true).get(0);
		ParameterNode copyexpcat = copy.getParameter(expcat.getName());
		testExpectedParameters(fExCat1, copyexpcat);
		testParent(copyexpcat, copy, true);
	}

	@Test
	public void methodCopyTest(){
		MethodNode copy = fMethod1.getCopy();
		if(copy == null)System.out.println("COPY!!!");	
		testMethods(fMethod1, copy, fLabel1, fLabel2);
		testParent(fMethod1, copy.getParent(), true);
	}

	public void testClasses(ClassNode classnode, ClassNode copy, String parentlabel, String childlabel){
		testNode(classnode, copy);

		MethodNode method = classnode.getMethods().get(0);
		MethodNode copymeth = copy.getMethod(method.getName(), method.getParametersTypes());

		testMethods(method, copymeth, parentlabel, childlabel);
		testParent(copymeth, copy, true);
	}

	@Test
	public void classCopyTest(){
		ClassNode copy = fClass1.getCopy();
		testClasses(fClass1, copy, fLabel1, fLabel2);
		testParent(copy, fClass1.getParent(), true);
	}

	public void testRoots(RootNode root, RootNode copy, String parentlabel, String childlabel){
		testNode(root, copy);

		ClassNode classnode = root.getClasses().get(0);
		ClassNode copyclass = copy.getClassModel(classnode.getName());

		testClasses(classnode, copyclass, parentlabel, childlabel);
		testParent(copyclass, copy, true);
	}

	@Test
	public void rootCopyTest(){
		RootNode copy = fRoot.getCopy();
		testRoots(fRoot, copy, fLabel1, fLabel2);
		this.testParent(copy, null, true);
	}

}