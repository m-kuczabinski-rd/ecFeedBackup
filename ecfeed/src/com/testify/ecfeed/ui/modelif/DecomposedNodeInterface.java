package com.testify.ecfeed.ui.modelif;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.GenericOperationAddChoice;
import com.testify.ecfeed.adapter.operations.GenericOperationRemoveChoice;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.DecomposedNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.Messages;

public class DecomposedNodeInterface extends AbstractNodeInterface {

	private DecomposedNode fTarget;

	public DecomposedNodeInterface(IModelUpdateContext updateContext){
		super(updateContext);
	}

	public void setTarget(DecomposedNode target){
		super.setTarget(target);
		fTarget = target;
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
		IModelOperation operation = new GenericOperationAddChoice(fTarget, newChoice, new EclipseTypeAdapterProvider(), fTarget.getChoices().size(), true);
		return execute(operation, Messages.DIALOG_ADD_CHOICE_PROBLEM_TITLE);
	}

	public boolean removeChoice(ChoiceNode choice) {
		IModelOperation operation = new GenericOperationRemoveChoice(fTarget, choice, true);
		return execute(operation, Messages.DIALOG_REMOVE_CHOICE_TITLE);
	}

	public boolean removeChoices(Collection<ChoiceNode> choices) {
		boolean displayWarning = false;
		for(ChoiceNode p : choices){
			if(fTarget.getParameter().getMethod().mentioningConstraints(p).size() > 0 || fTarget.getParameter().getMethod().mentioningTestCases(p).size() > 0){
				displayWarning = true;
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

	protected String generateNewChoiceValue() {
		EclipseModelBuilder builder = new EclipseModelBuilder();
		String type = fTarget.getParameter().getType();
		String value = builder.getDefaultExpectedValue(type);
		if(isPrimitive() == false && builder.getSpecialValues(type).size() == 0){
			int i = 0;
			while(fTarget.getLeafChoiceValues().contains(value)){
				value = builder.getDefaultExpectedValue(type) + i++;
			}
		}
		return value;
	}

	public boolean isPrimitive() {
		return ParameterInterface.isPrimitive(fTarget.getParameter().getType());
	}

	public boolean isUserType() {
		return !isPrimitive();
	}

	public List<String> getSpecialValues() {
		return new EclipseModelBuilder().getSpecialValues(fTarget.getParameter().getType());
	}

	public boolean hasLimitedValuesSet() {
		return !isPrimitive() || isBoolean();
	}

	public  boolean isBoolean() {
		return ParameterInterface.isBoolean(fTarget.getParameter().getType());
	}

	protected String generateChoiceName(){
		String name = Constants.DEFAULT_NEW_PARTITION_NAME;
		int i = 0;
		while(fTarget.getChoiceNames().contains(name)){
			name = Constants.DEFAULT_NEW_PARTITION_NAME + i++;
		}
		return name;
	}

}
