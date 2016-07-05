/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.spec.gui;

/*
 * description:
 * Single first/middle/last index refer to single node selection - two edge cases and one in between;
 * Single type clustered means several nodes of same type and parent selected one after another;
 * Single type interleaved means several nodes of same type and parent selected not consecutively;
 * Single type multiple parents means several nodes of same type with different parents;
 * Multiple types clustered means several nodes of many types and same parent;
 * Multiple types interleaved means  several nodes of many types and same parent selected not consecutively;
 * Multiple types multiple parents means nodes of many types and many parents (I'm pretty sure that cluster/interleave stress is not needed here)
 */
public enum ESelectionType{
	SINGLE_FIRST_INDEX, SINGLE_MIDDLE_INDEX, SINGLE_LAST_INDEX,
	SINGLE_TYPE_CLUSTERED, SINGLE_TYPE_INTERLEAVED, SINGLE_TYPE_MULTIPLE_PARENTS,
	MULTIPLE_TYPES_CLUSTERED, MULTIPLE_TYPES_INTERLEAVED, MULTIPLE_TYPE_MULTIPLE_PARENTS;
}
