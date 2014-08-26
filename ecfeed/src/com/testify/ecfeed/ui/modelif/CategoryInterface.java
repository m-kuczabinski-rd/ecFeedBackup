package com.testify.ecfeed.ui.modelif;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.category.GenericOperationAddPartition;
import com.testify.ecfeed.modelif.java.category.CategoryOperationRename;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetDefaultValue;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetExpected;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetType;
import com.testify.ecfeed.modelif.java.category.CategoryOperationShift;
import com.testify.ecfeed.modelif.java.category.GenericOperationRemovePartition;
import com.testify.ecfeed.modelif.java.category.ITypeAdapterProvider;
import com.testify.ecfeed.modelif.java.common.RemoveNodesOperation;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;
import com.testify.ecfeed.ui.editor.TableViewerSection;

public class CategoryInterface extends GenericNodeInterface {
	
	private CategoryNode fTarget;
	private ITypeAdapterProvider fAdapterProvider;
	
	public CategoryInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
		fAdapterProvider = new TypeAdaptationSupport().getAdapterProvider();
	}

	public void setTarget(CategoryNode target){
		super.setTarget(target);
		fTarget = target;
	}

	public String getDefaultValue(String type) {
		return new EclipseModelBuilder().getDefaultExpectedValue(type);
	}

	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new CategoryOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public boolean setType(String newType, BasicSection source, IModelUpdateListener updateListener) {
		ITypeAdapterProvider adapterProvider = new TypeAdaptationSupport().getAdapterProvider();
		if(newType.equals(fTarget.getType())){
			return false;
		}
		return execute(new CategoryOperationSetType(fTarget, newType, adapterProvider), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}
	
	public boolean setExpected(boolean expected, BasicSection source, IModelUpdateListener updateListener){
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
			return execute(new CategoryOperationSetExpected(fTarget, expected), source, updateListener, Messages.DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean setDefaultValue(String valueString, TableViewerSection source, IModelUpdateListener updateListener) {
		if(fTarget.getDefaultValueString().equals(valueString) == false){
			return execute(new CategoryOperationSetDefaultValue(fTarget, valueString, fAdapterProvider.getAdapter(fTarget.getType())), source, updateListener, Messages.DIALOG_SET_DEFAULT_VALUE_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean hasLimitedValuesSet() {
		return !isPrimitive(fTarget.getType()) || isBoolean(fTarget.getType());
	}

	public static boolean hasLimitedValuesSet(CategoryNode category) {
		String type = category.getType();
		return !isPrimitive(type) || isBoolean(type);
	}

	public static boolean isPrimitive(String type) {
		return Arrays.asList(JavaUtils.supportedPrimitiveTypes()).contains(type);
	}
	
	public static boolean isUserType(String type) {
		return !isPrimitive(type);
	}
	
	public boolean isPrimitive() {
		return isPrimitive(fTarget.getType());
	}
	
	public boolean isUserType() {
		return !isPrimitive();
	}
	
	public static boolean isBoolean(String type){
		return type.equals(JavaUtils.getBooleanTypeName());
	}

	public List<String> getSpecialValues() {
		return new EclipseModelBuilder().getSpecialValues(fTarget.getType());
	}

	public static List<String> getSpecialValues(String type) {
		return new EclipseModelBuilder().getSpecialValues(type);
	}

	public String getType() {
		return fTarget.getType();
	}

	public static String[] supportedPrimitiveTypes() {
		return JavaUtils.supportedPrimitiveTypes();
	}

	public String getDefaultValue() {
		return fTarget.getDefaultValueString();
	}

	public boolean isExpected() {
		return fTarget.isExpected();
	}
	
	public String[] defaultValueSuggestions(){
		Set<String> items = new HashSet<String>(getSpecialValues());
		if(JavaUtils.isPrimitive(getType()) == false){
			for(PartitionNode p : fTarget.getLeafPartitions()){
				items.add(p.getValueString());
			}
			if(items.contains(fTarget.getDefaultValueString())== false){
				items.add(fTarget.getDefaultValueString());
			}
		}
		return items.toArray(new String[]{});
	}

	@Override
	public boolean moveUpDown(boolean up, BasicSection source, IModelUpdateListener updateListener){
		int index = CategoryOperationShift.nextAllowedIndex(fTarget, up);
		if(index != -1){
			return executeMoveOperation(new CategoryOperationShift(fTarget, index), source, updateListener);
		}
		return false;
	}

	public PartitionNode addNewPartition(BasicSection source, IModelUpdateListener updateListener) {
		String name = generatePartitionName();
		String value = generateNewPartitionValue();
		PartitionNode newPartition = new PartitionNode(name, value);
		if(addPartition(newPartition, source, updateListener)){
			return newPartition;
		}
		return null;
	}
	
	public boolean addPartition(PartitionNode newPartition, BasicSection source, IModelUpdateListener updateListener) {
		IModelOperation operation = new GenericOperationAddPartition(fTarget, newPartition, fTarget.getPartitions().size()); 
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_PARTITION_PROBLEM_TITLE);
	}
	
	public boolean removePartition(PartitionNode partition, BasicSection source, IModelUpdateListener updateListener) {
		IModelOperation operation = new GenericOperationRemovePartition(fTarget, partition);
		return execute(operation, source, updateListener, Messages.DIALOG_REMOVE_PARTITION_TITLE);
	}

	protected String generateNewPartitionValue() {
		EclipseModelBuilder builder = new EclipseModelBuilder();
		String value = builder.getDefaultExpectedValue(getType());
		if(isPrimitive() == false && builder.getSpecialValues(getType()).size() == 0){
			int i = 0;
			while(fTarget.getLeafPartitionValues().contains(value)){
				value = builder.getDefaultExpectedValue(getType()) + i++; 
			}
		}
		return value;
	}

	protected String generatePartitionName(){
		String name = Constants.DEFAULT_NEW_PARTITION_NAME;
		int i = 0;
		while(fTarget.getPartitionNames().contains(name)){
			name = Constants.DEFAULT_NEW_PARTITION_NAME + i++; 
		}
		return name;
	}

	public boolean removePartitions(Collection<PartitionNode> partitions, BasicSection source, IModelUpdateListener updateListener) {
		boolean displayWarning = false;
		for(PartitionNode p : partitions){
			if(fTarget.getMethod().mentioningConstraints(p).size() > 0 || fTarget.getMethod().mentioningTestCases(p).size() > 0){
				displayWarning = true;
			}
		}
		if(displayWarning){
			if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.DIALOG_REMOVE_PARTITION_WARNING_TITLE, 
					Messages.DIALOG_REMOVE_PARTITION_WARNING_MESSAGE) == false){
				return false;
			}
		}
		return execute(new RemoveNodesOperation(partitions), source, updateListener, Messages.DIALOG_REMOVE_PARTITIONS_PROBLEM_TITLE);
	}
}
