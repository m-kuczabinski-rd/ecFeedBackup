/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.GenericOperationAddChoice;
import com.ecfeed.core.adapter.operations.GenericOperationRemoveChoice;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.Constants;
import com.ecfeed.ui.common.EclipseModelBuilder;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IFileInfoProvider;

public class ChoicesParentInterface extends AbstractNodeInterface {

	public ChoicesParentInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider){
		super(updateContext, fileInfoProvider);
	}

	public AbstractParameterNode getParameter() {
		return getTarget().getParameter();
	}

	public ChoiceNode addNewChoice() {
		String name = generateChoiceName();
		String value = generateNewChoiceValue();
		ChoiceNode newChoice = new ChoiceNode(name, value);
		if(addChoice(newChoice)){
			return newChoice;
		}
		return null;
	}

	public boolean addChoice(ChoiceNode newChoice) {
		IModelOperation operation = new GenericOperationAddChoice(getTarget(), newChoice, new EclipseTypeAdapterProvider(), getTarget().getChoices().size(), true);
		return execute(operation, Messages.DIALOG_ADD_CHOICE_PROBLEM_TITLE);
	}

	public boolean removeChoice(ChoiceNode choice) {
		IModelOperation operation = new GenericOperationRemoveChoice(getTarget(), choice, getAdapterProvider(), true);
		return execute(operation, Messages.DIALOG_REMOVE_CHOICE_TITLE);
	}

	public boolean removeChoices(Collection<ChoiceNode> choices) {
		boolean displayWarning = false;
		for(MethodNode method : getTarget().getParameter().getMethods()){
			for(ChoiceNode p : choices){
				if(method.mentioningConstraints(p).size() > 0 || method.mentioningTestCases(p).size() > 0){
					displayWarning = true;
				}
			}
		}
		if(displayWarning){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_REMOVE_CHOICE_WARNING_TITLE,
					Messages.DIALOG_REMOVE_CHOICE_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return removeChildren(choices, Messages.DIALOG_REMOVE_CHOICES_PROBLEM_TITLE);
	}

	public boolean isPrimitive() {
		return AbstractParameterInterface.isPrimitive(getTarget().getParameter().getType());
	}

	public boolean isUserType() {
		return !isPrimitive();
	}

	public List<String> getSpecialValues() {
		return new EclipseModelBuilder().getSpecialValues(getTarget().getParameter().getType());
	}

	public boolean hasLimitedValuesSet() {
		return !isPrimitive() || isBoolean();
	}

	public  boolean isBoolean() {
		return AbstractParameterInterface.isBoolean(getTarget().getParameter().getType());
	}

	@Override
	protected ChoicesParentNode getTarget(){
		return (ChoicesParentNode)super.getTarget();
	}

	protected String generateNewChoiceValue() {
		EclipseModelBuilder builder = new EclipseModelBuilder();
		String type = getTarget().getParameter().getType();
		String value = builder.getDefaultExpectedValue(type);
		if(isPrimitive() == false && builder.getSpecialValues(type).size() == 0){
			int i = 0;
			while(getTarget().getLeafChoiceValues().contains(value)){
				value = builder.getDefaultExpectedValue(type) + i++;
			}
		}
		return value;
	}

	protected String generateChoiceName(){
		String name = Constants.DEFAULT_NEW_PARTITION_NAME;
		int i = 0;
		
		ArrayList<String> choiceNames = new ArrayList<>();
		for(ChoiceNode choice: getTarget().getChoices()){
			choiceNames.add(choice.getName());
		}
		
		while(choiceNames.contains(name)){
			name = Constants.DEFAULT_NEW_PARTITION_NAME + i++;
		}
		return name;
	}

}
