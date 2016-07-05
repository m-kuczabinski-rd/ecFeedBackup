package com.ecfeed.spec.gui;

import com.ecfeed.testutils.ETypeName;

public class TestMethodDetailsPage{

	public void renameMethodTest(String newName, boolean duplicate, boolean isValid){
		// TODO Auto-generated method stub
		System.out.println("renameMethodTest(" + newName + ", " + duplicate + ", " + isValid + ")");
	}
	
	public void implementMethodTest(boolean isMethodImplemented, boolean hasUserType, boolean isClassImplemented){
		// TODO Auto-generated method stub
		System.out.println("implementMethodTest(" + isMethodImplemented + ", " + hasUserType + ", " + isClassImplemented + ")");
	}

	/*
	 * Test if:
	 * - "browse" button is disabled for methods in unimplemented classes
	 * - "browse" button is disabled, when no method with same signature is available in class
	 * - methods with different parameter types are ignored, even if method and parameters have mirror names
	 * - @expected annotation is ignored when comparing method signatures
	 * - parameter type doesn't matter (test for user types)
	 * - method and parameter names are updated after reassign
	 * - parameters will keep their expected status and no constraing nor test case is lost in the process.
	 * - the implementation status of class and methods is updated, along with "other methods" section
	 * There is no reason to make these boolean or any conditions. They are not really correlated.
	 * Thus I guess it will be fixed parameterless test case.
	 */
	public void reassignMethodTest(){
		// TODO Auto-generated method stub
		System.out.println("reassignMethodTest()");
	}

	/*
	 * This will be one fixed method, no real variables available.
	 * Ensure that unimplemented choices are unavailable.
	 * Ensure that parameters with no implemented choices are unavailable and they block the whole process from the start.
	 * Ensure that no test cases are saved and amount of generated cases is valid (prepare predefined data sets for that).
	 */
	public void testOnlineTest(){
		// TODO Auto-generated method stub
	}

	/*
	 * Test for:
	 * - empty methods
	 * - single parameter (partitioned and expected)
	 * - multiple partitioned and expected parameters
	 * I guess fixed test data will be the best here. The problem is confirming results, graph is DRAWN.
	 * Probably until gui tests are implemented I will change this.
	 */
	public void calculateCoverageTest(){
		// TODO Auto-generated method stub
		System.out.println("calculateCoverageTest()");
	}

	/*
	 * Test for standard gui operations:
	 * - viewer drag (if possible and elements are properly cut)
	 * - double click on elements opens his details page
	 * - add new parameter test (also for multiple new parameter - if consecutive names are properly changed);
	 * 
	 */
	public void parametersSectionTest(){
		// TODO Auto-generated method stub
		System.out.println("parametersSectionTest()");
	}

	public void removeSelectedParametersTest(ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("removeSelectedParametersTest(" + selectionType + ")");
	}

	/*
	 * Test just like change type operation. Two types, original value and expected bool determining, if value should be converted.
	 * Boolean for expected/partitioned parameter.
	 * Covering all edge cases makes for pretty large test suite, though.
	 */
	public void changeParameterTypeTest(ETypeName oldType, ETypeName newType, String originalValue, boolean isExpected, boolean isConvertable){
		// TODO Auto-generated method stub
		System.out.println("changeParameterTypeTest(" + oldType + ", " + newType + ", " + originalValue + ", " + isExpected + ", " + isConvertable + ")");
	}
	
	/*
	 * Generate choices and, for them, constraints and test cases. Test if:
	 * - conversion TO expected:
	 * 	- default value for type is correct
	 * 	- mentioning constraints are removed
	 * 	- values are replaced by default values in test cases
	 *  - boolean has true/false values available
	 *  
	 * - conversion FROM expected:
	 * 	- mentioning constraints are removed
	 * 	- test cases are removed
	 * 	- choices are again available no matter the type
	 */
	public void changeParametersExpectedStatusTest(boolean toExpected, ETypeName type, String defaultValues){
		// TODO Auto-generated method stub
		System.out.println("changeParametersExpectedStatusTest(" + toExpected + ")");
	}
	
	/* As above, but when changing TO expected:
	 * 	- user type keeps it's partitions, which are available for selection AND:
	 * 		- in case it has choices defined the first available is set as default value
	 * 		- in case it has no choices, but is implemented, one of the existing values is picked
	 * 		- if it has no choices and is not implemented, the default value is "VALUE"
	 */
	public void changeUserTypeParameterExpectedStatus(boolean toExpected, boolean hasChoices, boolean isImplemented){
		// TODO Auto-generated method stub
		System.out.println("changeUserTypeParameterExpectedStatus(" + toExpected + ", " + hasChoices + ", " + isImplemented + ")");
	}
	
	public void changeParametersNameTest(String newName, boolean duplicate, boolean isValid){
		// TODO Auto-generated method stub
		System.out.println("changeParametersNameTest(" + newName + ", " + duplicate + ", " + isValid + ")");
	}

	public void removeSelectedConstraints(ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("removeSelectedConstraints(" + selectionType + ")");
	}

	public void addNewConstraintTest(){
		// TODO Auto-generated method stub
		System.out.println("addNewConstraintTest()");
	}

	public void addNewParameterTest(boolean duplicate){
		// TODO Auto-generated method stub
		System.out.println("addNewParameterTest(" + duplicate + ")");
	}

	public void renameConstraintTest(String new_name){
		// TODO Auto-generated method stub
		System.out.println("renameConstraintTest(" + new_name + ")");
	}

	/*
	 * Test by adding several suites of total test cases amount less than limit (if possible) and larger than limit.
	 */
	public void testCaseViewerLimitTest(int size){
		// TODO Auto-generated method stub
		System.out.println("testCaseViewerLimitTest(" + size + ")");
	}

	/*
	 * Fixed test of different selections - all/none/partially to check graying out test suite etc.
	 * Doing it by enum with selection types is not necessary.
	 * Test:
	 * - select all by test suite
	 * - select all by test cases (one by one) and check if test suite is selected after all children are
	 * - select test suite with 2 children then deselect one case to check if it properly grays out then select again and check if the suite is checked
	 * - select test suite with 1 child then deselect one case to check if it deselects suite then select again and check if it the suite is checked
	 * - 
	 */
	public void testCaseViewerSelectionTest(){
		// TODO Auto-generated method stub
		System.out.println("testCaseViewerSelectionTest()");
	}

	/*
	 * Another fixed test... Check:
	 * - initial name is "default suite" 
	 * - change name combobox contains all already defined test suites
	 * - changing name for already existing suite
	 * - the parameters are listed in the order they appear in the method
	 * - all choice values are listed and expected ones are editable, including predefined values (min/max values for numeric etc.)
	 * - boolean and user type expected values are edited in read-only combo
	 * - if no test suite of the specified name exists, it will appear after creating test case
	 * 
	 * What with suite name test? Repeating all these steps for all name edge cases is nonsense.
	 * Also, every test suite name field should use same validation, so RenameSuite will handle it.
	 */
	public void addTestCaseTest(){
		// TODO Auto-generated method stub
		System.out.println("addTestCaseTest()");
	}
	
	/* 
	 * Another fixed scenario with several predefined setups:
	 * - check if test case of implemented methods and referring to implemented choices are executable
	 * - check if test case of not implemented method, but with implemented choices is NOT executable
	 * Test with user type - add one value that is not implemented yet and receive both executable and not.
	 */
	public void testCaseImplementationTest(){
		// TODO Auto-generated method stub
		System.out.println("testCaseImplementationTest()");
	}

	/*
	 * Also test if every test case is renamed - create multi-suite environment to test that.
	 */
	public void RenameSuiteTest(String suiteName, boolean evaluation){
		// TODO Auto-generated method stub
		System.out.println("RenameSuiteTest(" + suiteName + ", " + evaluation + ")");
	}
	
	/*
	 * Test constraint section of suite generator dialog:
	 * - initially all are checked
	 * - test check/uncheck all buttons
	 * - test for single constraint - selecting  and selecting->deselecting
	 * - test for plural constraint - deselecting one by one, selecting one by one, selecting/deselecting  by parent
	 */
	public void GenerateSuiteConstraintVieverTest(){
		// TODO Auto-generated method stub
		System.out.println("GenerateSuiteConstraintVieverTest()");
	}

	/*
	 * Test parameters section of suite generator dialog:
	 * - initially all are checked
	 * - at least one choice per parameter (or parameter, in expected case) must be checked
	 * - parameters with no choice cannot be checked and they block "ok" button - use user type here
	 * - test if selecting parents select all the children using multi-level choice hierarchy
	 */
	public void GenerateSuiteParametersViewerTest(){
		// TODO Auto-generated method stub
		System.out.println("GenerateSuiteParametersViewerTest()");
	}

	/*
	 * Test edge cases of every parameter, or test every separately. Number of parameters needed is not consistent, 
	 * not to mention handling them. I guess fixed mathod here, test for:
	 * - NWISE: two generators parameters: N must be value between 1 and number of params. Coverage must be between 1 and 100.
	 * - CARTESIAN: no parameters.
	 * - RANDOM: one parameter, must be larger than -1
	 * - ADAPTIVE_RANDOM: four parameters: depth (-1 and higher), candidate set size (0 and higher), length (0 and higher) and bool duplicate.
	 * 
	 * Will get to it later.
	 */
	public void GenerateSuiteGeneratorComboTest(EGeneratorType generatorType){
		// TODO Auto-generated method stub
		System.out.println("GenerateSuiteGeneratorComboText(" + generatorType + ")");
	}

	/*
	 * Standard viewer selection test.
	 */
	public void removeSelectedCasesTest(ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("removeSelectedCasesTest(" + selectionType + ")");
	}

	/*
	 * If isImplemented is false, at least one selected test case is not implemented
	 */
	public void executeSelectedTest(ESelectionType selectionType, boolean isImplemented){
		// TODO Auto-generated method stub
		System.out.println("executeSelectedTest(" + selectionType + ", " + isImplemented + ")");
	}
	

}

