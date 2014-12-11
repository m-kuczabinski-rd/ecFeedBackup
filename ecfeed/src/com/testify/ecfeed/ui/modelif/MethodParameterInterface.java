package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.operations.MethodParameterOperationSetLink;
import com.testify.ecfeed.adapter.operations.MethodParameterOperationSetLinked;
import com.testify.ecfeed.adapter.operations.MethodParameterOperationSetType;
import com.testify.ecfeed.adapter.operations.ParameterOperationSetDefaultValue;
import com.testify.ecfeed.adapter.operations.ParameterOperationSetExpected;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.GlobalParametersParentNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.common.Messages;

public class MethodParameterInterface extends AbstractParameterInterface {

	private MethodParameterNode fTarget;

	public MethodParameterInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public void setTarget(AbstractParameterNode target){
		super.setTarget(target);
		fTarget = (MethodParameterNode)target;
	}

	public boolean isExpected() {
		return fTarget.isExpected();
	}

	public String getDefaultValue() {
		return fTarget.getDefaultValue();
	}

	public boolean setExpected(boolean expected){
		if(expected != fTarget.isExpected()){
			MethodNode method = fTarget.getMethod();
			if(method != null){
				boolean testCases = method.getTestCases().size() > 0;
				boolean constraints = method.mentioningConstraints(fTarget).size() > 0;
				if(testCases || constraints){
					String message = "";
					if(testCases){
						if(expected){
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_ALTERED + "\n";
						}
						else{
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_REMOVED + "\n";
						}
					}
					if(constraints){
						message += Messages.DIALOG_SET_CATEGORY_EXPECTED_CONSTRAINTS_REMOVED;
					}
					if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_SET_CATEGORY_EXPECTED_WARNING_TITLE, message) == false){
						return false;
					}
				}
			}
			return execute(new ParameterOperationSetExpected(fTarget, expected), Messages.DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean setDefaultValue(String valueString) {
		if(fTarget.getDefaultValue().equals(valueString) == false){
			IModelOperation operation = new ParameterOperationSetDefaultValue(fTarget, valueString, getTypeAdapterProvider().getAdapter(fTarget.getType()));
			return execute(operation, Messages.DIALOG_SET_DEFAULT_VALUE_PROBLEM_TITLE);
		}
		return false;
	}

	public String[] defaultValueSuggestions(){
		Set<String> items = new HashSet<String>(getSpecialValues());
		if(JavaUtils.isPrimitive(getType()) == false){
			for(ChoiceNode p : fTarget.getLeafChoices()){
				items.add(p.getValueString());
			}
			if(items.contains(fTarget.getDefaultValue())== false){
				items.add(fTarget.getDefaultValue());
			}
		}
		return items.toArray(new String[]{});
	}

	public boolean setLinked(boolean linked) {
		MethodParameterOperationSetLinked operation = new MethodParameterOperationSetLinked(fTarget, linked);
		MethodNode method = fTarget.getMethod();
		List<String> types = method.getParametersTypes();
		if(linked){
			//check the type of the link. If it causes collision, set different link
			boolean newLinkNecessary = false;
			if(fTarget.getLink() == null){
				newLinkNecessary = true;
			}else{
				String linkType = fTarget.getLink().getType();
				types.set(fTarget.getIndex(), linkType);
				if(method.getClassNode().getMethod(method.getName(), types) != null && (method.getClassNode().getMethod(method.getName(), types) != method)){
					newLinkNecessary = true;
				}
			}
			if(newLinkNecessary){
				boolean linkFound = false;
				for(GlobalParameterNode link : getAvailableLinks()){
					types.set(fTarget.getIndex(), link.getType());
					if(method.getClassNode().getMethod(method.getName(), types) == null){
						operation.addOperation(0, new MethodParameterOperationSetLink(fTarget, link));
						linkFound = true;
						break;
					}
				}
				if(linkFound == false){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_SET_PARAMETER_LINKED_PROBLEM_TITLE, Messages.DIALOG_NO_VALID_LINK_AVAILABLE_PROBLEM_MESSAGE);
					return false;
				}
			}
		}else{
			//check the type of the unlinked parameter. If it causes collision, set new type
			String type = fTarget.getRealType();
			types.set(fTarget.getIndex(), type);
			if(method.getClassNode().getMethod(method.getName(), types) != null && method.getClassNode().getMethod(method.getName(), types) != method){
				type = fTarget.getType();
				operation.addOperation(0, new MethodParameterOperationSetType(fTarget, type, getAdapterProvider()));
			}
		}

		return execute(operation, Messages.DIALOG_SET_PARAMETER_LINKED_PROBLEM_TITLE);
	}

	public boolean isLinked() {
		return fTarget.isLinked();
	}

	public boolean setLink(GlobalParameterNode link) {
		IModelOperation operation = new MethodParameterOperationSetLink(fTarget, link);
		return execute(operation, Messages.DIALOG_SET_PARAMETER_LINK_PROBLEM_TITLE);
	}


	public GlobalParameterNode getGlobalParameter(String path) {
		String parameterName = path;
		GlobalParametersParentNode parametersParent;
		if(path.indexOf(":") != -1){
			String parentName = path.substring(0, path.indexOf(":"));
			parameterName = path.substring(path.indexOf(":") + 1);
			parametersParent = fTarget.getMethod().getClassNode();
			if(parametersParent.getName().equals(parentName) == false){
				return null;
			}
		}
		else{
			parametersParent = (RootNode)fTarget.getRoot();
		}
		return parametersParent.getGlobalParameter(parameterName);
	}

	public GlobalParameterNode getLink() {
		return fTarget.getLink();
	}

	public List<GlobalParameterNode> getAvailableLinks() {
		List<GlobalParameterNode> result = new ArrayList<GlobalParameterNode>();
		result.addAll(((RootNode)fTarget.getRoot()).getGlobalParameters());
		result.addAll(fTarget.getMethod().getClassNode().getGlobalParameters());
		return result;
	}

	@Override
	public boolean setType(String newType) {
		if(newType.equals(fTarget.getType())){
			return false;
		}
		return execute(new MethodParameterOperationSetType(fTarget, newType, getAdapterProvider()), Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE);
	}
}
