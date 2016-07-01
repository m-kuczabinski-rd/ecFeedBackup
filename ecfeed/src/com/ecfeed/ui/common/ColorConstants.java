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

package com.ecfeed.ui.common;

import org.eclipse.swt.graphics.RGB;

public class ColorConstants {
	public static final RGB DISABLED_EDIT_BACKGROUND = new RGB(0, 0, 128);;
	public static RGB OBSOLETE_METHOD = new RGB(128, 0, 0);
	public static RGB EXPECTED_VALUE_CATEGORY = new RGB(0, 0, 128);
	public static RGB ABSTRACT_PARTITION = new RGB(0, 128, 128);
	public static RGB INHERITED_LABEL_FOREGROUND = new RGB(32,32,32);
	public static RGB INHERITED_LABEL_BACKGROUND = new RGB(196,196,196);

	public static RGB ITEM_IMPLEMENTED = new RGB(0, 100, 0);
	public static RGB TEST_CASE_EXECUTABLE = ITEM_IMPLEMENTED;
}
