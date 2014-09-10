package com.testify.ecfeed.modelif.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.operations.Messages;


public class JavaMethodUtils {

	public static List<String> getArgTypes(MethodNode method) {
		List<String> result = new ArrayList<String>();
		for(CategoryNode c : method.getCategories()){
			result.add(c.getType());
		}
		return result;
	}

	public static List<String> getArgNames(MethodNode method) {
		List<String> result = new ArrayList<String>();
		for(CategoryNode c : method.getCategories()){
			result.add(c.getName());
		}
		return result;
	}

	public static boolean validateMethodName(String name, List<String> problems) {
		boolean valid = name.matches(Constants.REGEX_METHOD_NODE_NAME);
		valid &= Arrays.asList(Constants.JAVA_KEYWORDS).contains(name) == false;
		if(valid == false){
			if(problems != null){
				problems.add(Messages.METHOD_NAME_REGEX_PROBLEM);
			}
		}
		return valid;
	}

	public static boolean validateMethodName(String name) {
		return validateMethodName(name, null);
	}

}
