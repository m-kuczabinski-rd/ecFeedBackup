package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.databene.feed4junit.Feeder;

@RunWith(Feeder.class)
public class GenericNodeTest extends GenericNode {

	public GenericNodeTest() {
		super("dummy");
	}

	@Test
	public void testName(String name, String newName) {
		GenericNode node = new GenericNode(name);
		
		assertEquals(name, node.getName());
		
		node.setName(newName);
		assertEquals(newName, node.getName());
	}
	
	@Test
	public void testChildren(){
		GenericNode parent = new GenericNode("parent");
		GenericNode child1 = new GenericNode("child1");
		GenericNode child2 = new GenericNode("child2");
		
		assertEquals(false, parent.hasChildren());
		assertEquals(0, parent.getChildren().size());
		
		parent.addChild(child1);
		assertEquals(true, parent.hasChildren());
		assertEquals(1, parent.getChildren().size());
		assertEquals(parent, child1.getParent());
		assertEquals(child1, parent.getChildren().elementAt(0));
		
		parent.addChild(child2);
		assertEquals(true, parent.hasChildren());
		assertEquals(2, parent.getChildren().size());
		assertEquals(parent, child2.getParent());
		assertEquals(child1, parent.getChildren().elementAt(0));
		assertEquals(child2, parent.getChildren().elementAt(1));
		
		parent.removeChild(child1);
		assertEquals(true, parent.hasChildren());
		assertEquals(1, parent.getChildren().size());
		assertEquals(null, child1.getParent());
		
		parent.removeChild(child2);
		assertEquals(false, parent.hasChildren());
		assertEquals(0, parent.getChildren().size());
		assertEquals(null, child2.getParent());
		
		child1.setParent(parent);
		assertEquals(true, parent.hasChildren());
		assertEquals(1, parent.getChildren().size());
		assertEquals(parent, child1.getParent());
		assertEquals(child1, parent.getChildren().elementAt(0));

	}
	
	@Test
	public void testGetRoot(){
		GenericNode parent = new GenericNode("parent");
		GenericNode child = new GenericNode("child");
		GenericNode grandchild = new GenericNode("grandchild");
		
		parent.addChild(child);
		child.addChild(grandchild);
		
		assertEquals(parent, grandchild.getRoot());
		
		
	}
}
