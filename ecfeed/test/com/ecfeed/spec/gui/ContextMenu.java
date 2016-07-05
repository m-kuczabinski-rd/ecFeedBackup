package com.ecfeed.spec.gui;

import com.ecfeed.testutils.ENodeType;

public class ContextMenu{

	/**
	 *  possible only if single node is selected.
	 */
	public void addChildTest(ENodeType nodeType, ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("addChildTest(" + nodeType + ", " + selectionType + ")");
	}

	public void copyTest(ENodeType sourceType, ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("copyTest(" + sourceType + ", " + selectionType + ")");
	}

	public void pasteSelectionPossibilityTest(ENodeType targetType, ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("pasteSelectionPossibilityTest(" + selectionType + ", " + targetType + ")");
	}

	public void pastePossibilityTest(ENodeType targetType, ENodeType sourceType){
		// TODO Auto-generated method stub
		System.out.println("pastePossibilityTest(" + targetType + ", " + sourceType + ")");
	}

	public void selectAllTest(){
		// TODO Auto-generated method stub
		System.out.println("selectAllTest()");
	}

	public void expandCollapseTest(){
		// TODO Auto-generated method stub
		System.out.println("expandCollapseTest()");
	}

	public void MoveUpDownTest(){
		// TODO Auto-generated method stub
		System.out.println("MoveUpDownTest()");
	}

	public void pasteDuplicateNameTest(ENodeType nodeType, boolean duplicateName){
		// TODO Auto-generated method stub
		System.out.println("pasteDuplicateNameTest(" + nodeType + ", " + duplicateName + ")");
	}

	public void pasteBulkAtomicTest(ENodeType nodeType, ENodeValidationStatus validNodes){
		// TODO Auto-generated method stub
		System.out.println("pasteBulkAtomicTest(" + nodeType + ")");
	}

	public void pasteNodesTest(ENodeType sourceType){
		// TODO Auto-generated method stub
		System.out.println("pasteNodesTest(" + sourceType + ")");
	}

	// nodes have to be of the same type, but don't have to be of same parent
	public void cutTest(ENodeType nodeType, ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("cutTest(" + nodeType + ", " + selectionType + ")");
	}

	public void deleteTest(ENodeType nodeType){
		// TODO Auto-generated method stub
		System.out.println("deleteTest(" + nodeType + ")");
	}
	
	/**
	 * @param masterNode this is independent node, i.e. parent node, not affected by changes on slaveNode
	 * @param slaveNode this node is dependent on masterNode and must be removed along it, i.e. child node, or constraint referencing choice (choice - masterNode)
	 * Test cases (of course try undo then redo to ensure correctness:
	 * - remove method parameter choice and referencing constraint
	 * - remove global parameter choice and referencing constraint
	 * - remove method parameter and referencing constraint
	 * - remove global parameter and referencing constraint
	 * - remove method parameter choice and mentioning test case
	 * - remove global parameter choice and mentioning test case
	 * - remove global parameter and linking method parameter
	 * - remove any node and its child
	 * There should be constraint regulating which nodes can be master/slave in certain relation.
	 * Also, nodes which cannot be master (i.e. TestCase and Constraint) should be excluded from master choices,
	 * same with RootNode in slaves.
	 */
	public void multipleDeleteTest(ENodeType masterNode, ENodeType slaveNode){
		// TODO Auto-generated method stub
		System.out.println("multipleDeleteTest()");
	}
	
	/** Duplicate signature test:
	 * - remove method and method parameter in other method, that would make duplicate signature if not method removal
	 * - remove method and global parameter linked in other method, that would make duplicate signature if not method removal
	 * - remove multiple method parameters which would cause duplicate signature as midway result
	 * - remove multiple global parameters which would cause duplicate signature as midway result
	 * - remove multiple method and global parameters which would cause duplicate signature as midway result
	 * Example of "midway result":
	 * given we have two methods, and select parameters in square brackets:
	 * Method1(int, [int])
	 * Method2([int])
	 * if we select parameter in Method1 first, then it will be queued for removal first and therefore midway state would be:
	 * Method1(int)
	 * Method2([int])
	 * which is duplicate. Of course methods must bear same name, in this example 1 and 2 suffixes are given just to make things clear.
	 */
	public void onDeleteDuplicateTest(){
		// TODO Auto-generated method stub
		System.out.println("onDeleteDuplicateTest()");
	}

}

