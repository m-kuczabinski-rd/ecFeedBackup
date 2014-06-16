package com.testify.ecfeed.ui.common;

import static com.testify.ecfeed.ui.common.Messages.DIALOG_METHOD_EXISTS_TITLE;
import static com.testify.ecfeed.ui.common.Messages.DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE;
import static com.testify.ecfeed.utils.ModelUtils.getJavaTypes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.AdaptTypeSupport;
import com.testify.ecfeed.utils.ModelUtils;

public class WarningModelOperations{

	public static void removePartition(PartitionNode partition, CategoryNode parent){

	}

	public static void removePartition(PartitionNode partition, PartitionNode parent){

	}

	public static void removeCategory(CategoryNode category){

	}
	
	public static boolean removeCategories(List<CategoryNode> categories, MethodNode method){
		{
			ArrayList<String> tmpTypes = method.getCategoriesTypes();
			for (CategoryNode node : categories) {
				for (int i = 0; i < method.getCategories().size(); ++i) {
					CategoryNode type = method.getCategories().get(i);
					if (type.getName().equals(node.getName()) && type.getType().equals(node.getType())) {
						tmpTypes.remove(node.getType());
					}
				}
			}
			if (method.getClassNode().getMethod(method.getName(), tmpTypes) == null) {
				// checking if there is any reason to display warning  - test cases and constraints
				boolean warn  = false;
				if(method.getTestCases().isEmpty()){
					for(ConstraintNode constraint: method.getConstraintNodes()){
						for(CategoryNode node : categories){
							if(constraint.mentions(node)){
								warn  = true;
								break;
							}
						}
						if(warn == true)
							break;
					}
				} else{
					warn =  true;
				}
				if(warn){
					if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_REMOVE_PARAMETERS_TITLE,
							Messages.DIALOG_REMOVE_PARAMETERS_MESSAGE)) {
						return false;
					}
				}
				boolean ischanged = false;
				for(CategoryNode category: categories){
					if(method.removeCategory(category) && !ischanged){
						method.clearTestCases();
						ischanged = true;
					}
				}
				return ischanged;
			} else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_METHOD_EXISTS_TITLE,
						Messages.DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE);
				return false;
			}
		}
		
	}

	public static boolean addCategory(CategoryNode category, MethodNode method){
		// checking if parameter with this name already exists...
		if(method.getCategory(category.getName()) != null){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DIALOG_CATEGORY_EXISTS_TITLE,
					Messages.DIALOG_CATEGORY_EXISTS_MESSAGE);
			return false;
		}
		// checking if class doesn't already contain method with name and
		// parameters same as after adding this category...
		ArrayList<String> tmpTypes = method.getCategoriesTypes();
		tmpTypes.add(category.getType());
		if(method.getClassNode().getMethod(method.getName(), tmpTypes) != null){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), DIALOG_METHOD_EXISTS_TITLE,
					DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE);
			return false;
		}
		// checking if data loss warning should appear...
		if(!method.getTestCases().isEmpty()){
			if(!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.DIALOG_DATA_MIGHT_BE_LOST_TITLE,
					Messages.DIALOG_DATA_MIGHT_BE_LOST_MESSAGE)){
				return false;
			}
			method.clearTestCases();
		}
		method.addCategory(category);
		return true;
	}

	public static boolean changeCategoryType(CategoryNode category, String newType){
		if (!getJavaTypes().contains(newType) && !ModelUtils.isClassQualifiedNameValid(newType)) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					Messages.DIALOG_PARAMETER_TYPE_PROBLEM_TITLE,
					Messages.DIALOG_PARAMETER_TYPE_PROBLEM_MESSAGE);
			return false;
		}

		if (!category.getType().equals(newType)) {
			ArrayList<String> tmpTypes = category.getMethod().getCategoriesTypes();
			for (int i = 0; i < category.getMethod().getCategories().size(); ++i) {
				CategoryNode type = category.getMethod().getCategories().get(i);
				if (type.getName().equals(category.getName()) && type.getType().equals(category.getType())) {
					tmpTypes.set(i, newType);
				}
			}
			if (category.getMethod().getClassNode().getMethod(category.getMethod().getName(), tmpTypes) == null) {
				// checking if there is any reason to display warning  - test cases and constraints
				boolean warn  = false;
				if(category.getMethod().getTestCases().isEmpty()){
					for(ConstraintNode constraint : category.getMethod().getConstraintNodes()){
						if(constraint.mentions(category)){
							warn = true;
							break;
						}
					}
				} else{
					warn =  true;
				}
				if(warn){
					if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
							Messages.DIALOG_DATA_MIGHT_BE_LOST_TITLE,
							Messages.DIALOG_DATA_MIGHT_BE_LOST_MESSAGE)) {
						return false;
					}
				}
				AdaptTypeSupport.changeCategoryType(category, newType);
				return true;
			} else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_METHOD_EXISTS_TITLE,
						Messages.DIALOG_METHOD_WITH_PARAMETERS_EXISTS_MESSAGE);
			}
		}
		return false;

	}

	public static boolean changeCategoryExpectedStatus(CategoryNode category, boolean expected){
		if (category.isExpected() != expected) {
			// checking if there is any reason to display warning  - test cases and constraints
			boolean warn  = false;
			if(category.getMethod().getTestCases().isEmpty()){
				for(ConstraintNode constraint : category.getMethod().getConstraintNodes()){
					if(constraint.mentions(category)){
						warn = true;
						break;
					}
				}
			} else{
				warn =  true;
			}
			if(warn){
				if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_DATA_MIGHT_BE_LOST_TITLE,
						Messages.DIALOG_DATA_MIGHT_BE_LOST_MESSAGE)) {
					return false;
				}
			}
			category.getMethod().changeCategoryExpectedStatus(category, expected);
			return true;
		}
		return false;
	}

	public static void removeMethod(MethodNode method){

	}

	public static void removeClass(ClassNode classnode){

	}

}
