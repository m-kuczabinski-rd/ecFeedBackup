package com.testify.ecfeed.modelif;

import java.util.ArrayList;
import java.util.List;

public class ModelOperationManager {
	private List<IModelOperation> fHistory;
	private int fHistoryIndex = 0;
	
	public ModelOperationManager(){
		fHistory = new ArrayList<IModelOperation>();
	}
	
	protected void updateHistory(IModelOperation operation){
		if(fHistory.size() > fHistoryIndex){
			fHistory = fHistory.subList(0, fHistoryIndex);
		}
		else if(fHistory.size() < fHistoryIndex){
			//shouldn't happen, but let's protect against it.
			fHistoryIndex = fHistory.size();
		}
		
		fHistory.add(operation);
		++fHistoryIndex;
	}
	
	public boolean undoEnabled(){
		return fHistoryIndex > 0;
	}
	
	public boolean redoEnabled(){
		return fHistoryIndex < fHistory.size();
	}
	
	public void undo() throws ModelIfException{
		if(fHistoryIndex > 0){
			IModelOperation operation = fHistory.get(fHistoryIndex - 1).reverseOperation();
			operation.execute();
			--fHistoryIndex;
		}
	}
	
	public void redo() throws ModelIfException{
		if(fHistoryIndex <= fHistory.size()){
			IModelOperation operation = fHistory.get(fHistoryIndex);
			operation.execute();
			++fHistoryIndex;
		}
	}
	
	public void execute(IModelOperation operation) throws ModelIfException{
		operation.execute();
		updateHistory(operation);
	}

}
