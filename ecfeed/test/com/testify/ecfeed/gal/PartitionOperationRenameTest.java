package com.testify.ecfeed.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationRename;

public class PartitionOperationRenameTest {

	@Test
	public void setValidNameTest(){
		CategoryNode category = new CategoryNode("name", "int", "0", false);
		PartitionNode p1 = new PartitionNode("p1", "0");
		PartitionNode p11 = new PartitionNode("p11", "0");
		PartitionNode p12 = new PartitionNode("p12", "0");
		p1.addPartition(p11);
		p1.addPartition(p12);
		PartitionNode p2 = new PartitionNode("p2", "0");
		
		category.addPartition(p1);
		category.addPartition(p2);
		
		PartitionOperationRename operation1 = new PartitionOperationRename(p1, "p3");
		PartitionOperationRename operation2 = new PartitionOperationRename(p11, "p13");
		try {
			operation1.execute();
			operation2.execute();
		} catch (ModelIfException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
		
		assertEquals("p3", p1.getName());
		assertEquals("p13", p11.getName());
	}

	@Test
	public void setNotUniqueNameTest(){
		CategoryNode category = new CategoryNode("name", "int", "0", false);
		PartitionNode p1 = new PartitionNode("p1", "0");
		PartitionNode p11 = new PartitionNode("p11", "0");
		PartitionNode p12 = new PartitionNode("p12", "0");
		p1.addPartition(p11);
		p1.addPartition(p12);
		
		PartitionNode p2 = new PartitionNode("p2", "0");
		
		category.addPartition(p1);
		category.addPartition(p2);
		
		PartitionOperationRename operation = new PartitionOperationRename(p1, "p2");
		try {
			operation.execute();
			fail("exception expected");
		} catch (ModelIfException e) {
			assertEquals("p1", p1.getName());
		}

		operation = new PartitionOperationRename(p11, "p12");
		try {
			operation.execute();
			fail("exception expected");
		} catch (ModelIfException e) {
			assertEquals("p1", p1.getName());
		}
	}
	
	@Test
	public void wrongNameTest(){
		CategoryNode category = new CategoryNode("name", "int", "0", false);
		PartitionNode p1 = new PartitionNode("p1", "0");
		PartitionNode p2 = new PartitionNode("p2", "0");
		
		category.addPartition(p1);
		category.addPartition(p2);

		List<PartitionOperationRename> unallowed = new ArrayList<PartitionOperationRename>();
		//empty string
		unallowed.add(new PartitionOperationRename(p1, ""));
		//white space only
		unallowed.add(new PartitionOperationRename(p1, "   "));
		//white space at start
		unallowed.add(new PartitionOperationRename(p1, " x"));
		//forbidden character
		unallowed.add(new PartitionOperationRename(p1, "x&"));
		//too long [65 characters]
//		String tooLong = "";
//		for(int i = 0; i < 65; i++){
//			tooLong += "x";
//		}
//		unallowed.add(new PartitionOperationRename(p1, tooLong));
		
		for(PartitionOperationRename operation : unallowed){
			try{
				operation.execute();
				fail("Exception expected for operation " + operation);
			}
			catch(ModelIfException e){
				assertEquals("p1", p1.getName());
			}
		}
	}

	@Test
	public void reverseOperationTest(){
		CategoryNode category = new CategoryNode("name", "int", "0", false);
		PartitionNode p1 = new PartitionNode("p1", "0");
		PartitionNode p2 = new PartitionNode("p2", "0");
		
		category.addPartition(p1);
		category.addPartition(p2);
		
		IModelOperation renameOperation = new PartitionOperationRename(p1, "p3");
		try{
			renameOperation.execute();
		}
		catch (ModelIfException e){
			fail("unexpected exception: " + e.getMessage());
		}
		assertEquals("p3", p1.getName());
		
		IModelOperation reverseOperation = renameOperation.reverseOperation();
		try{
			reverseOperation.execute();
		}
		catch (ModelIfException e){
			fail("unexpected exception: " + e.getMessage());
		}
		assertEquals("p1", p1.getName());
	}
}
