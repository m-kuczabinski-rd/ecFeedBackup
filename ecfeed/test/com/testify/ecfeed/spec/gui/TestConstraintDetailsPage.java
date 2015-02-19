package com.testify.ecfeed.spec.gui;



public class TestConstraintDetailsPage{
	
	public enum EGeneratorCheckHelper{
		FULFILL_ALL, BLOCKED_BY_1, BLOCKED_BY_2, BLOCKED_BY_BOTH
	}
	
	public enum EStatementType{
		STATIC, AND, OR, CHOICE_IS, CHOICE_IS_NOT, LABEL_IS, LABEL_IS_NOT
	}

	/**
	 * It will require switch - every enum const represents one case which must be covered separately.
	 * I left "CGITmodel" method in the model, which shows how it should be tested - it is just presentation of results.
	 * Basically we need to create method like in presentation and test if results are correct for certain constraints used
	 * (full cartesian with no constraints used, then missing certain cases blocked by constraints)
	 * 
	 * Test with at least two constraints.
	 * Part of data set should fulfill all constraints,
	 * other part get blocked by one constraint,
	 * another part get blocked by second constraint,
	 * and the last one blocked by both."
	 */
	public void constraintGeneratorInfluenceTest(EGeneratorCheckHelper testCaseStatus){
		// TODO Auto-generated method stub
		System.out.println("constraintGeneratorInfluenceTest()");
	}
	
	/*
	 * It will require switch - every enum const represents one case which must be covered separately.
	 * Test with premise being true.
	 * I left "CETmodel" method in the model, which shows example constraints.
	 * Certain constraints should give certain results (i.e. static true should allow all test cases, static false should allow none, like shown in CETmodel)
	 * 
	 * Test considering that from true premise must result true consequence. We won't test if false premise prevents constraint from applying here.
	 * Test static statements,
	 * for AND,
	 * for OR,
	 * for choice fulfilling the rule,
	 * for labelled choices fulfilling the rule.
	 * Test all of them for true and false (in case of the last two - is & is not logic)
	 */
	public void constraintEvaluationTest(EStatementType statement, boolean statementValue){
		// TODO Auto-generated method stub
		System.out.println("constraintEvaluationTest()");
	}

	/*
	 * Ignore it, it is just a model for constraintGeneratorInfluenceTest
	 */
	public void CGITmodel(EGeneratorCheckHelper testCaseStatus){
		// TODO Auto-generated method stub
		System.out.println("CGITmodel(" + testCaseStatus + ")");
	}

	/*
	 * Ignore it, it is just a model for constraintEvaluationTest
	 */
	public void CETmodel(EStatementType statement, boolean statementValue){
		// TODO Auto-generated method stub
		System.out.println("CETmodel(" + statement + ", " + statementValue + ")");
	}


}


