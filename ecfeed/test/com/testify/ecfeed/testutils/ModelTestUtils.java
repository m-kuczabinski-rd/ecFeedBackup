package com.testify.ecfeed.testutils;

import static org.junit.Assert.fail;

import com.testify.ecfeed.model.IGenericNode;

public class ModelTestUtils {
	
	public static void assertElementsEqual(IGenericNode n, IGenericNode n1) {
		ModelStringifier stringifier = new ModelStringifier();
		if(n.compare(n1) == false){
			fail("Parsed element differs from original\n" + stringifier.stringify(n, 0) + "\n" + stringifier.stringify(n1, 0));
		}
	}

}
