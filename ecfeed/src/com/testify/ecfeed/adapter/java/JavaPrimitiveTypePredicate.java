/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.adapter.java;

import com.testify.ecfeed.model.IPrimitiveTypePredicate;

public class JavaPrimitiveTypePredicate implements IPrimitiveTypePredicate{
	@Override
	public boolean isPrimitive(String type){
		return JavaUtils.isPrimitive(type);
	}
}

