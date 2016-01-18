package com.testify.ecfeed.adapter.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.core.adapter.java.JavaUtils;
import com.testify.ecfeed.core.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;

@RunWith(OnlineRunner.class)
@EcModel("test/com.testify.ecfeed.adapter.java.ect")
@Generator(CartesianProductGenerator.class)
@Constraints(Constraints.ALL)
public class JavaUtilsTest {

	@Test
	public void isValidTypeNameTest(String packageName, String className, boolean valid){
//		packageName = "";
//		className = "int";
//		valid = false;
//		System.out.println("isValidTypeNameTest(" + packageName + ", " + className + ", " + valid + ")");
		String typeName;
		if(packageName == null || className == null){
			typeName = null;
		}
		else if(packageName.length() > 0){
			typeName = packageName + "." + className;
		}
		else{
			typeName = className;
		}
		boolean result = JavaUtils.isValidTypeName(typeName);
		assertEquals(valid, result);
	}

}
