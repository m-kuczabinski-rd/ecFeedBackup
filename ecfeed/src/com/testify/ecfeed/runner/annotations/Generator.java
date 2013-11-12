package com.testify.ecfeed.runner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.testify.ecfeed.api.IGenerator;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Generator {
	@SuppressWarnings("rawtypes")
	Class<? extends IGenerator> value();
}
