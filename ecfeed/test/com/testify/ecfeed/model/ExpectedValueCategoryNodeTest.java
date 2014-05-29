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
		CategoryNode category = new CategoryNode("name", "type", true);
		assertEquals(0, category.getChildren().size());
		assertFalse(category.hasChildren());
	}
	
	@Test
	public void testGetDefaultValue() {
		CategoryNode category = new CategoryNode("name", "type", true);
		category.setDefaultValueString("0");
		assertEquals("0", category.getDefaultValueString());
		
		category.setDefaultValueString("1");
		assertEquals("1", category.getDefaultValueString());
	}
	
	@Test
	public void testGetDefaultValuePartition() {
		CategoryNode category = new CategoryNode("name", "type", true);
		category.setDefaultValueString("value");
		assertEquals("value", category.getDefaultValuePartition().getValueString());
	}
}
