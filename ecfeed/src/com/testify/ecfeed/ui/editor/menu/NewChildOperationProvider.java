package com.testify.ecfeed.ui.editor.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.modelif.MethodInterface;
import com.testify.ecfeed.ui.modelif.PartitionedNodeInterface;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class NewChildOperationProvider implements IModelVisitor {
	
	private final String ADD_CLASS_MENU_OPERATION_OPERATION_NAME = "Add new class";
	private final String ADD_METHOD_MENU_OPERATION_OPERATION_NAME = "Add new method";
	private final String ADD_PARAMETER_MENU_OPERATION_OPERATION_NAME = "Add new parameter";
	private final String ADD_TEST_CASE_MENU_OPERATION_OPERATION_NAME = "Add new test case";
	private final String ADD_PARTITION_MENU_OPERATION_OPERATION_NAME = "Add new partition";
	private final String ADD_CONSTRAINT_MENU_OPERATION_OPERATION_NAME = "Add new constraint";

	protected ModelOperationManager fOperationManager;
	protected AbstractFormPart fSource;
	protected IModelUpdateListener fUpdateListener;


	private abstract class AbstractAddChildOperation extends MenuOperation{
		public AbstractAddChildOperation(String name) {
			super(name);
		}
		
		@Override
		public boolean isEnabled() {
			return true;
		}
	}
	
	private class AddClassOperation extends AbstractAddChildOperation{

		private RootInterface fRootIf;

		public AddClassOperation(RootNode target) {
			super(ADD_CLASS_MENU_OPERATION_OPERATION_NAME);
			fRootIf = new RootInterface(fOperationManager);
			fRootIf.setTarget(target);
		}

		@Override
		public Object execute() {
			return fRootIf.addNewClass(fSource, fUpdateListener);
		}
	}
	
	private class AddMethodOperation extends AbstractAddChildOperation{

		private ClassInterface fClassIf;

		public AddMethodOperation(ClassNode target) {
			super(ADD_METHOD_MENU_OPERATION_OPERATION_NAME);
			fClassIf = new ClassInterface(fOperationManager);
			fClassIf.setTarget(target);
		}

		@Override
		public Object execute() {
			return fClassIf.addNewMethod(fSource, fUpdateListener);
		}
	}
	
	private abstract class AddMethodChildOperation extends AbstractAddChildOperation{
		protected MethodInterface fMethodIf;
	
		public AddMethodChildOperation(String name, MethodNode target) {
			super(name);
			fMethodIf = new MethodInterface(fOperationManager);
			fMethodIf.setTarget(target);
		}
	}

	private class AddTestCaseOperation extends AddMethodChildOperation{
		public AddTestCaseOperation(MethodNode target) {
			super(ADD_TEST_CASE_MENU_OPERATION_OPERATION_NAME, target);
		}

		@Override
		public Object execute() {
			return fMethodIf.addTestCase(fSource, fUpdateListener);
		}
	}
	
	private class AddParameterOperation extends AddMethodChildOperation{
		public AddParameterOperation(MethodNode target) {
			super(ADD_PARAMETER_MENU_OPERATION_OPERATION_NAME, target);
		}

		@Override
		public Object execute() {
			return fMethodIf.addNewParameter(fSource, fUpdateListener);
		}
	}
	
	private class AddConstraintOperation extends AddMethodChildOperation{
		public AddConstraintOperation(MethodNode target) {
			super(ADD_CONSTRAINT_MENU_OPERATION_OPERATION_NAME, target);
		}

		@Override
		public Object execute() {
			return fMethodIf.addNewConstraint(fSource, fUpdateListener);
		}
	}
	
	private class AddPartitionOperation extends AbstractAddChildOperation{
		private PartitionedNodeInterface fPartitionedNodeIf;

		public AddPartitionOperation(PartitionedNode target) {
			super(ADD_PARTITION_MENU_OPERATION_OPERATION_NAME);
			fPartitionedNodeIf = new PartitionedNodeInterface(fOperationManager);
			fPartitionedNodeIf.setTarget(target);
		}

		@Override
		public Object execute() {
			return fPartitionedNodeIf.addNewPartition(fSource, fUpdateListener);
		}
	}
	
	public NewChildOperationProvider(ModelOperationManager operationManager, AbstractFormPart source, IModelUpdateListener updateListener){
		fOperationManager = operationManager;
		fSource = source;
		fUpdateListener = updateListener;
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		return createList(new AddClassOperation(node));
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		return createList(new AddMethodOperation(node));
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		List<MenuOperation> list = new ArrayList<>();
		list.add(new AddParameterOperation(node));
		list.add(new AddConstraintOperation(node));
		list.add(new AddTestCaseOperation(node));
		return list;
	}

	@Override
	public Object visit(CategoryNode node) throws Exception {
		return createList(new AddPartitionOperation(node));
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(PartitionNode node) throws Exception {
		return createList(new AddPartitionOperation(node));
	}

	private Object createList(MenuOperation operation) {
		return Arrays.asList(new MenuOperation[]{operation});
	}

}
