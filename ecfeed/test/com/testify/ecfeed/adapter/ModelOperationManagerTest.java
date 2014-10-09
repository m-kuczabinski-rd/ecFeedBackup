package com.testify.ecfeed.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.junit.StaticRunner;
import com.testify.ecfeed.junit.annotations.EcModel;

@RunWith(StaticRunner.class)
@EcModel("test/com.testify.ecfeed.adapter.ect")
public class ModelOperationManagerTest {
	
	private static class Operation implements IModelOperation{
		private boolean fExecuted;
		private int fId;
		private static int fLastId = 0;
		
		public Operation(){
			fId = ++fLastId;
		}
		
		@Override
		public void execute() throws ModelOperationException {
			fExecuted = true;
		}

		@Override
		public boolean modelUpdated() {
			return true;
		}

		@Override
		public IModelOperation reverseOperation() {
			return new Operation() {
				@Override
				public void execute() throws ModelOperationException {
					fExecuted = false;
				}
			};
		}

		@Override
		public String getName() {
			return "Operation";
		}
		
		public boolean isExecuted(){
			return fExecuted;
		}
		
		public int getId(){
			return fId;
		}
		
		@Override
		public String toString(){
			return getName() + " " + getId() + "[" + (isExecuted()?"":"not ") + "executed]";
		}
	}
	
	@Test
	public void executeTest(){
		ModelOperationManager operationManager = new ModelOperationManager();
		Operation operation = new Operation();
		try{
			operationManager.execute(operation);
			assertTrue(operation.isExecuted());
		}
		catch(ModelOperationException e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void undoEnabledTest(int operations, int undos, int redos, int additionalOperations){
		if(undos > operations || redos > undos){
			//improperly defined test case, skip it
			return;
		}
//		System.out.println("undoEnabledTest(" + operations + ", " + undos + ", " + redos + ", " + additionalOperations + ")");
		int historyCount = operations - undos + redos + additionalOperations;
		ModelOperationManager operationManager = new ModelOperationManager();
		executeScenario(operationManager, operations, undos, redos, additionalOperations);
//		System.out.println("expected: " + (historyCount > 0) + ", is: " + operationManager.undoEnabled());
		assertEquals(historyCount > 0, operationManager.undoEnabled());
	}

	@Test
	public void redoEnabledTest(int operationsPerformed, int undosPerformed, int redosPerformed){
		if(undosPerformed > operationsPerformed || redosPerformed > undosPerformed){
			//improperly defined test case, skip it
			return;
		}
		ModelOperationManager operationManager = new ModelOperationManager();
		executeScenario(operationManager, operationsPerformed, undosPerformed, redosPerformed, 0);
		
		boolean expected = (undosPerformed > 0) && (undosPerformed > redosPerformed);
		
		assertEquals(expected, operationManager.redoEnabled());
	}
	
	@Test
	public void undoTest(int operations, int undos, int redos, int additionalOperations){
		if(undos > operations || redos > undos){
			//improperly defined test case, skip it
			return;
		}
//		System.out.println("undoTest(" + operations + ", " + undos + ", " + redos + ", " + additionalOperations + ")");
		ModelOperationManager operationManager = new ModelOperationManager();
		List<Operation> executed = executeScenario(operationManager, operations, undos, redos, additionalOperations);
		int pointer = operations - undos + redos + additionalOperations;
		if(pointer > 0){
			assertTrue(executed.get(pointer - 1).isExecuted());
			try{
				operationManager.undo();
			}
			catch(Exception e){
				fail("Unexpected exception: " + e.getMessage());
			}
			assertFalse(executed.get(pointer - 1).isExecuted());
		}
	}
	
	@Test
	public void redoTest(int operations, int undos, int redos){
		if(undos > operations || redos > undos){
			//improperly defined test case, skip it
			return;
		}
//		System.out.println("redoTest(" + operations + ", " + undos + ", " + redos + ")");
		ModelOperationManager operationManager = new ModelOperationManager();
		List<Operation> executed = executeScenario(operationManager, operations, undos, redos, 0);
		int pointer = operations - undos + redos;
		if(pointer < executed.size()){
			assertFalse(executed.get(pointer).isExecuted());
			try{
				operationManager.redo();
			}
			catch(Exception e){
				fail("Unexpected exception: " + e.getMessage());
			}
			assertTrue(executed.get(pointer).isExecuted());
		}
	}
	
	protected List<Operation> executeScenario(ModelOperationManager manager, int operations, int undos, int redos, int additionalOperations){
		List<Operation> performed = executeOperations(manager, operations);
		executeUndo(manager, undos);
		executeRedo(manager, redos);
		if(additionalOperations > 0){
			Iterator<Operation> iterator = performed.iterator();
			while(iterator.hasNext()){
				if(iterator.next().isExecuted() == false){
					iterator.remove();
				}
			}
			performed.addAll(executeOperations(manager, additionalOperations));
		}
		return performed;
	}
	
	protected List<Operation> executeOperations(ModelOperationManager manager, int times){
		List<Operation> executed = new ArrayList<>();
		for(int i = 0; i < times; i++){
			try{
				Operation operation = new Operation(); 
				manager.execute(operation);
				executed.add(operation);
			}
			catch(ModelOperationException e){
				fail("Unexpected exception: " + e.getMessage());
			}
		}
		return executed;
	}
	
	protected void executeUndo(ModelOperationManager manager, int times){
		for(int i = 0; i < times; i++){
			try{
				manager.undo();
			}
			catch(Exception e){
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	protected void executeRedo(ModelOperationManager manager, int times){
		for(int i = 0; i < times; i++){
			try{
				manager.redo();
			}
			catch(Exception e){
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

}
