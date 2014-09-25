package com.testify.ecfeed.runner.junit;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modeladp.java.ModelClassLoader;
import com.testify.ecfeed.modeladp.java.PartitionValueParser;
import com.testify.ecfeed.runner.junit.AbstractFrameworkMethod;
import com.testify.ecfeed.ui.common.Constants;

public class AbstractFrameworkMethodTest {

	private final String FUNCTION_UNDER_TEST_NAME = "functionUnderTest";
	
	private final List<String> INT_ARGS;
	private final List<String> STRING_ARGS;
	private final List<String> ENUM_ARGS;
	
	private Result fResult = new Result();
	
	public enum Enum{
		VALUE1, VALUE2, VALUE3, VALUE4
	}
	
	public static class Result{
		int arg1;
		String arg2;
		Enum arg3;
		
		public void reset(){
			arg1 = 0;
			arg2 = null;
			arg3 = null;
		}
	}
	
	public AbstractFrameworkMethodTest(){
		INT_ARGS = new ArrayList<String>();
		INT_ARGS.addAll(Arrays.asList(Constants.INTEGER_SPECIAL_VALUES));
		INT_ARGS.addAll(Arrays.asList(new String[]{"-1", "0", "1"}));
		STRING_ARGS = new ArrayList<String>();
		STRING_ARGS.addAll(Arrays.asList(Constants.STRING_SPECIAL_VALUES));
		STRING_ARGS.addAll(Arrays.asList(new String[]{"", "a", "Aa"}));
		ENUM_ARGS = new ArrayList<String>();
		for(Enum e : Enum.values()){
			ENUM_ARGS.add(e.name());
		}
	}
	
	public void functionUnderTest(int arg1, String arg2, Enum arg3){
		fResult.reset();
		
		fResult.arg1 = arg1;
		fResult.arg2 = arg2;
		fResult.arg3 = arg3;
	}
	
	@Test
	public void invokeTest() {
		try {
			Method method = this.getClass().getMethod(FUNCTION_UNDER_TEST_NAME, int.class, String.class, Enum.class);
			CategoryNode intCategory = new CategoryNode("intCategory", "int", "0", false);
			CategoryNode stringCategory = new CategoryNode("stringCategory", "String", "0", false);
			CategoryNode enumCategory = new CategoryNode("enumCategory", Enum.class.getCanonicalName(), Enum.values()[0].name(), false);
			PartitionValueParser parser = new PartitionValueParser(new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader()));
			
			ModelClassLoader loader = new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader());
			AbstractFrameworkMethod frameworkMethod = new AbstractFrameworkMethod(method, loader);
			for(String intArg : INT_ARGS){
				for(String stringArg : STRING_ARGS){
					for(String enumArg : ENUM_ARGS){
						List<PartitionNode> args = new ArrayList<PartitionNode>();
						PartitionNode intPartition = new PartitionNode(intArg, intArg);
						intCategory.addPartition(intPartition);
						PartitionNode stringPartition = new PartitionNode(stringArg, stringArg);
						stringCategory.addPartition(stringPartition);
						PartitionNode enumPartition = new PartitionNode(enumArg, enumArg);
						enumCategory.addPartition(enumPartition);
						args.add(intPartition);
						args.add(stringPartition);
						args.add(enumPartition);
						
						frameworkMethod.invoke(this, args);
						
						assertEquals(fResult.arg1, parser.parseValue(intPartition));
						assertEquals(fResult.arg2, parser.parseValue(stringPartition));
						assertEquals(fResult.arg3, parser.parseValue(enumPartition));
					}
				}
			}
		} catch (Throwable e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}
