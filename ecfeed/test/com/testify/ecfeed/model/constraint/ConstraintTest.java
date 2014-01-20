package com.testify.ecfeed.model.constraint;

import org.junit.Before;
import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class ConstraintTest {
		private CategoryNode fCategory = new CategoryNode("category", "type");

		private PartitionNode fP1 = new PartitionNode("p1", 0);
		private PartitionNode fP2 = new PartitionNode("p2", 0);
		private PartitionNode fP3 = new PartitionNode("p3", 0);

		private PartitionNode fP11 = new PartitionNode("p11", 0);
		private PartitionNode fP12 = new PartitionNode("p12", 0);
		private PartitionNode fP13 = new PartitionNode("p13", 0);

		private PartitionNode fP21 = new PartitionNode("p21", 0);
		private PartitionNode fP22 = new PartitionNode("p22", 0);
		private PartitionNode fP23 = new PartitionNode("p23", 0);

		private PartitionNode fP221 = new PartitionNode("p21", 0);
		private PartitionNode fP222 = new PartitionNode("p22", 0);
		private PartitionNode fP223 = new PartitionNode("p23", 0);

		private PartitionNode fP31 = new PartitionNode("p31", 0);
		private PartitionNode fP32 = new PartitionNode("p32", 0);
		private PartitionNode fP33 = new PartitionNode("p33", 0);

		@Before
		private void prepareStructure(){
			fP1.addPartition(fP11);
			fP1.addPartition(fP12);
			fP1.addPartition(fP13);

			fP2.addPartition(fP21);
			fP2.addPartition(fP22);
			fP2.addPartition(fP23);

			fP22.addPartition(fP221);
			fP22.addPartition(fP222);
			fP22.addPartition(fP223);

			fP3.addPartition(fP31);
			fP3.addPartition(fP32);
			fP3.addPartition(fP33);

			fCategory.addPartition(fP1);
			fCategory.addPartition(fP2);
			fCategory.addPartition(fP3);
		}
	

}
