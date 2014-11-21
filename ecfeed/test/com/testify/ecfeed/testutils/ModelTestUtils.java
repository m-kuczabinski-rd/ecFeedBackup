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

package com.testify.ecfeed.testutils;

import static org.junit.Assert.fail;

import com.testify.ecfeed.model.AbstractNode;

public class ModelTestUtils {
	
	public static void assertElementsEqual(AbstractNode n, AbstractNode n1) {
		ModelStringifier stringifier = new ModelStringifier();
		if(n.compare(n1) == false){
			fail("Parsed element differs from original\n" + stringifier.stringify(n, 0) + "\n" + stringifier.stringify(n1, 0));
		}
	}

}
