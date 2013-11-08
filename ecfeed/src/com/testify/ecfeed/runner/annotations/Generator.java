package com.testify.ecfeed.runner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.model.PartitionNode;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Generator {
	Class<IGenerator<PartitionNode>> value();
}
