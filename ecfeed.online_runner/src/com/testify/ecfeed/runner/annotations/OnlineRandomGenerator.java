package com.testify.ecfeed.runner.annotations;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.api.IAlgorithm;
import com.testify.ecfeed.api.IRuntimeGenerator;

public abstract class OnlineRandomGenerator<E> implements IRuntimeGenerator<E>{

	public OnlineRandomGenerator(Method method) {
		super();
	}

	public static final int FOREVER = 100;

		
}
