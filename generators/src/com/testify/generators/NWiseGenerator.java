/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.generators;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.api.IAlgorithm;
import com.testify.ecfeed.api.IGenerator;
import com.testify.generators.algorithms.GenericNWiseAlgorithm;

public class NWiseGenerator<E> implements IGenerator<E> {
	Map<String, IAlgorithm<E>> fAlgorithms; 


	public void initialize(List<List<E>> algorithmInput){
		fAlgorithms = new LinkedHashMap<String, IAlgorithm<E>>(); 
		int axesCount = algorithmInput.size();
		for(int i = 1; i < axesCount; i++){
			fAlgorithms.put(i + "-wise", new GenericNWiseAlgorithm<E>(i));
		}
		fAlgorithms.put("Cartesian Product", new GenericNWiseAlgorithm<E>(axesCount));
	}

	@Override
	public String[] getAlgorithms() {
		return fAlgorithms.keySet().toArray(new String[]{});
	}

	@Override
	public IAlgorithm<E> getAlgorithm(String name) {
		return fAlgorithms.get(name);
	}
}
