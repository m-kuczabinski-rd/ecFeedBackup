package com.testify.ecfeed.utils;

public interface IChecker {
	boolean check(String toCheck);
	String getErrorMessage(String toCheck);
}
