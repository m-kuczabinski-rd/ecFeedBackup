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

import static com.testify.ecfeed.modeladp.java.Constants.*;

public class Constants {
	public static final String USER_TYPE = "user.type";
	public static final String[] SUPPORTED_TYPES = {
			TYPE_NAME_BOOLEAN,
			TYPE_NAME_BYTE,
			TYPE_NAME_CHAR,
			TYPE_NAME_DOUBLE,
			TYPE_NAME_FLOAT,
			TYPE_NAME_INT,
			TYPE_NAME_LONG,
			TYPE_NAME_SHORT,
			TYPE_NAME_STRING,
			USER_TYPE
	};

}
