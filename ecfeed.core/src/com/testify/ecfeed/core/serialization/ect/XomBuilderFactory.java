/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.serialization.ect;

public class XomBuilderFactory {

	public static XomBuilder createXomBuilder(int modelVersion) {
		if (modelVersion == 0) {
			return new XomBuilderVersion0();
		}
		return new XomBuilderVersion1();
	}
}
