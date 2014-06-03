package com.testify.ecfeed.ui.editor;
public abstract class MenuOperation{
protected String operationName;

public abstract void operate();

public MenuOperation(String opname){
operationName = opname;
}

public String getOperationName(){
return operationName;
}

public abstract boolean isEnabled();
}