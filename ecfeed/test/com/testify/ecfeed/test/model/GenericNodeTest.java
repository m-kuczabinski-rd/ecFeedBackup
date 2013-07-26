package com.testify.ecfeed.test.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.databene.feed4junit.Feeder;

import com.testify.ecfeed.model.GenericNode;

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
	
	@Test
	public void testEquals(){
		GenericNode parent_1 = new GenericNode("parent");
		GenericNode child_1_1 = new GenericNode("child_1");
		GenericNode grandchild_1_1_1 = new GenericNode("grandchild_1_1");
		GenericNode grandchild_1_1_2 = new GenericNode("grandchild_1_2");
		GenericNode child_1_2 = new GenericNode("child_2");
		GenericNode grandchild_1_2_1 = new GenericNode("grandchild_2_1");
		GenericNode grandchild_1_2_2 = new GenericNode("grandchild_2_2");
		
		parent_1.addChild(child_1_1);
		child_1_1.addChild(grandchild_1_1_1);
		child_1_1.addChild(grandchild_1_1_2);
		parent_1.addChild(child_1_2);
		child_1_1.addChild(grandchild_1_2_1);
		child_1_1.addChild(grandchild_1_2_2);
		
		GenericNode parent_1_copy = new GenericNode("parent");
		GenericNode child_1_1_copy = new GenericNode("child_1");
		GenericNode grandchild_1_1_1_copy = new GenericNode("grandchild_1_1");
		GenericNode grandchild_1_1_2_copy = new GenericNode("grandchild_1_2");
		GenericNode child_1_2_copy = new GenericNode("child_2");
		GenericNode grandchild_1_2_1_copy = new GenericNode("grandchild_2_1");
		GenericNode grandchild_1_2_2_copy = new GenericNode("grandchild_2_2");
		
		parent_1_copy.addChild(child_1_1_copy);
		child_1_1_copy.addChild(grandchild_1_1_1_copy);
		child_1_1_copy.addChild(grandchild_1_1_2_copy);
		parent_1_copy.addChild(child_1_2_copy);
		child_1_1_copy.addChild(grandchild_1_2_1_copy);
		child_1_1_copy.addChild(grandchild_1_2_2_copy);
		
		assertTrue(parent_1.equals(parent_1_copy));
		
		grandchild_1_2_2_copy.setName("grandchild_2_2_changed");
		assertFalse(parent_1.equals(parent_1_copy));
	}
}
