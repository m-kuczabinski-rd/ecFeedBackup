package com.testify.ecfeed.gal.java.partition;

public class Messages {

	public static final String PARTITION_NAME_NOT_UNIQUE_PROBLEM = "Partition name must be unique within a category or parent partition";
	public static final String PARTITION_NAME_REGEX_PROBLEM = "Partition name must contain between 1 and 64 characters and do not contain only white space characters";
	public static final String PARTITION_VALUE_PROBLEM(String value){
		return "Value " + value + " is not valid for given category.\n" + 
				"Partition value must fit to type and range of the represented variable.\n" +
				"Partitions of user defined type must follow Java enum defining rules.";
	}

}
