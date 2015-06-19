package com.testify.ecfeed.runner.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.runner.Messages;
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

		try {
			invokeRemotely(
					instance.getClass().getName(),
					testMethod.getName(),
					createParameters(testMethod, arguments));
		} catch (RunnerException e) {
			throw new RunnerException(
					Messages.CANNOT_INVOKE_TEST_METHOD(fTarget.toString(), testData.toString(), e.getMessage()));
		}
	}

	private void invokeRemotely(String className, String functionName, String testParams) throws RunnerException {

		System.out.println();
		System.out.println(Messages.LAUNCHING_ANDROID_INSTRUMENTATION());

		Process process = startProcess(className, functionName, testParams); 
		logOutput(process);
		waitFor(process);
		System.out.println("Exit code:" + process.exitValue());

		System.out.println(Messages.ANDROID_INSTRUMENTATION_FINISHED());    	
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
		} catch (IOException e) {
			throw new RunnerException(
					Messages.CANNOT_START_ANDROID_INSTRUMENTATION_PROCESS("adb am shell instrument", e.getMessage()));
		}

		return process;
	}

	private void logOutput(Process process) throws RunnerException {

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		boolean testFailed = false;
		String line;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				
				if (isTestFailedLine(line)) {
					testFailed = true;
				}
			}
		} catch (IOException e) {
			throw new RunnerException(Messages.IO_EXCEPITON_OCCURED(e.getMessage()));
		}
		
		if (testFailed) {
			throw new RunnerException("Test unsuccesful.");
		}
	}
	
	public boolean isTestFailedLine(String line) {
		
		if (line.isEmpty()) {
			return false;
		}
		if (line.indexOf("Tests run:") == -1) {
			return false;
		}
		if (getProblemItemsCount(line, "Failures") > 0) {
			return true;
		}
		if (getProblemItemsCount(line, "Errors") > 0) {
			return true;
		}
		return true;
	}

	private int getProblemItemsCount(String line, String itemName) {
		
		String item = itemName + ":";
		
		int index = line.indexOf(item); 
		if (index == -1) {
			return 0;
		}
		return getCounter(line, index + item.length());
	}
	
	private int getCounter(String line, int startIndex) {
		
		int digitBeg = getDigitBegIndex(line, startIndex);
		if (digitBeg == -1) {
			return 0;
		}
		
		int digitEnd = getDigitEndIndex(line, digitBeg);
		if (digitEnd == -1) {
			return 0;
		}
		
		String digit = line.substring(digitBeg, digitEnd);
		try {
			return Integer.decode(digit);
		} catch (NumberFormatException exc) {
			return 0;
		}
	}
	
	private int getDigitBegIndex(String line, int startIndex) {

		for(int index = startIndex; index < line.length(); index++) {
			Character ch = line.charAt(index);
			
			if (Character.isDigit(ch)) {
				return index;
			}
		}
		return -1;
	}
	
	private int getDigitEndIndex(String line, int startIndex) {

		int index = -1;
		
		for(index = startIndex; index < line.length(); index++) {
			Character ch = line.charAt(index);
			
			if (!Character.isDigit(ch)) {
				return index;
			}
		}
		return index;
	}
	
	private void waitFor(Process process) throws RunnerException {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw new RunnerException(Messages.INTERRUPTED_EXCEPTION_OCCURED(e.getMessage()));
		}
	}

	private String createParameters(Method testMethod, Object[] arguments) throws RunnerException {

		Class<?>[] paramTypes = testMethod.getParameterTypes();

		checkParamsAndArgs(paramTypes.length, arguments.length);

		return createParamsWithArgsStr(paramTypes, arguments);
	}

	private void checkParamsAndArgs(int params, int args) throws RunnerException {
		if (params != args) {
			throw new RunnerException(Messages.INVALID_NUMBER_OF_PARAMS_ARGS(params, args)); 
		}		
	}

	private String createParamsWithArgsStr(Class<?>[] paramTypes, Object[] arguments) {
		final String PARAM_SEPARATOR = ", ";
		String result = "";

		for(int index = 0; index < paramTypes.length; index++) {
			result = result + createParamWithArg(paramTypes[index], arguments[index]);

			if (index < paramTypes.length - 1) {
				result = result + PARAM_SEPARATOR;
			}
		}

		return result;		
	}

	private String createParamWithArg(Class<?> paramType, Object argument) {
		final String ARG_BEG_MARKER = "[";
		final String ARG_END_MARKER = "]";

		return paramType.getName() + ARG_BEG_MARKER + argument.toString() + ARG_END_MARKER; 
	}
}

