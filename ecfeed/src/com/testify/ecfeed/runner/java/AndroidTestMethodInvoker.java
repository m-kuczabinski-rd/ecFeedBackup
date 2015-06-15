package com.testify.ecfeed.runner.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.runner.RunnerException;

public class AndroidTestMethodInvoker implements TestMethodInvoker {
	
	private String fTestRunner = null;
	
	public AndroidTestMethodInvoker(String testRunner) {
		fTestRunner = testRunner;
	}
	
	@Override
	public void invoke(Method testMethod, Object instance, Object[] arguments,
			MethodNode fTarget, List<ChoiceNode> testData)
			throws RunnerException {
		
		invokeRemotely(
			"com.mamlambo.article.simplecalc.test.EctTest", // TODO - instance.getClass().getName(),
			testMethod.getName(),
			createParameters(testMethod, arguments));
	}
	
	private void invokeRemotely(String className, String functionName, String testParams) throws RunnerException {
    	Process process = startProcess(className, functionName, testParams); 
    	logOutput(process);
    	waitFor(process);
	}
	
	private Process startProcess(String className, String functionName, String testParams) throws RunnerException {
		
    	ProcessBuilder pb 
		= new ProcessBuilder(
			"adb", 
			"shell",
			"am",
			"instrument",
			"-w",        			
			"-e",
			"ecFeed",
			className + ", " + functionName + ", " + testParams,
			fTestRunner);
		
    	Process process = null;
		try {
			process = pb.start();
		} catch (IOException e1) {
			throw new RunnerException("Can not start process."); // TODO
		}

		return process;
	}
	
	private void logOutput(Process process) throws RunnerException {
		InputStream is = process.getInputStream();
    	InputStreamReader isr = new InputStreamReader(is);
    	BufferedReader br = new BufferedReader(isr);
    	String line;
        
        System.out.println("\nWriting output:");
        try {
			while ((line = br.readLine()) != null) {
			  System.out.println(line);      
			}
		} catch (IOException e) {
			throw new RunnerException("IOException occured."); // TODO
		}
        System.out.println("Program terminated.");
	}
	
	private void waitFor(Process process) throws RunnerException {
        try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw new RunnerException("Interrupted exception."); // TODO
		}
	}
	
	private String createParameters(Method testMethod, Object[] arguments) throws RunnerException {
		
		Class<?>[] paramTypes = testMethod.getParameterTypes();
		
		if (paramTypes.length != arguments.length) {
			throw new RunnerException("Invalid number of parameters and arguments."); // TODO
		}
		
		String result = "";
		
		for(int index = 0; index < paramTypes.length; index++) {
			result = result + createParamWithArg(paramTypes[index], arguments[index]);
			
			if (index < paramTypes.length - 1) {
				result = result + ", ";
			}
		}
		
		return result;
	}
	
	private String createParamWithArg(Class<?> paramType, Object argument) {
		return paramType.getName() + "[" + argument.toString() + "]"; 
	}
}
