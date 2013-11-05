/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Linesh Raghawan (linesh.raghawan(at)testify.no) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.runner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeGenerator {

	Class<OnlineRandomGenerator> value();
	
}
