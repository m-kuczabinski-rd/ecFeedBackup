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

package com.testify.ecfeed.ui.common;

import org.eclipse.swt.graphics.RGB;

public interface ColorConstants {
	RGB OBSOLETE_METHOD = new RGB(128, 0, 0);
	RGB EXPECTED_VALUE_CATEGORY = new RGB(0, 0, 128);
	RGB ABSTRACT_PARTITION = new RGB(0, 128, 128);
	RGB INHERITED_LABEL_FOREGROUND = new RGB(32,32,32);
	RGB INHERITED_LABEL_BACKGROUND = new RGB(196,196,196);
	
	RGB XML_COMMENT = new RGB(128, 0, 0);
	RGB PROC_INSTR = new RGB(128, 128, 128);
	RGB STRING = new RGB(0, 128, 0);
	RGB DEFAULT = new RGB(0, 0, 0);
	RGB TAG = new RGB(0, 0, 128);

	//enhancements
	RGB ESCAPED_CHAR = new RGB(128, 128, 0);
	RGB CDATA = new RGB(0, 128, 128);
	RGB CDATA_TEXT = new RGB(255, 0, 0);
	
	RGB CLASS_IMPLEMENTED = new RGB(0, 100, 0);
}
