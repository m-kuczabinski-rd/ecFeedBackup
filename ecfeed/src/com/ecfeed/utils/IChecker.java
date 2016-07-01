package com.ecfeed.utils;

public interface IChecker {
	boolean check(String toCheck);
	String getErrorMessage(String toCheck);
}
