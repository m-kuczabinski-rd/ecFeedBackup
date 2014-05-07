/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExpectedValueCategoryNodeTest {

	@Test
	public void testGetChildren(){
		ExpectedCategoryNode category = new ExpectedCategoryNode("name", "type", 0);
		assertEquals(0, category.getChildren().size());
		assertFalse(category.hasChildren());
	}
	
	@Test
	public void testGetDefaultValue() {
		ExpectedCategoryNode category = new ExpectedCategoryNode("name", "type", 0);
		assertEquals(0, category.getDefaultValue());
		
		category.setDefaultValue(1);
		assertEquals(1, category.getDefaultValue());
	}
	
	@Test
	public void testGetDefaultValuePartition() {
		ExpectedCategoryNode category = new ExpectedCategoryNode("name", "type", "value");
		assertEquals("value", category.getDefaultValuePartition().getValue());
	}
}
