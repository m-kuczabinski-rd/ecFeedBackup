package com.testify.ecfeed.utils;

import static com.testify.ecfeed.utils.ModelUtils.getDefaultExpectedValueString;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class AdaptTypeSupport{

	public static enum ConversionType{
		DONE, POSSIBLE, IMPOSSIBLE;
	}

	// returns true if model has changed in any way
	public static boolean changeCategoryType(CategoryNode category, String newtype){
		String oldtype = category.getType();
		// If type is exactly the same or null or whatever else might happen
		if(oldtype == null)
			oldtype = "";
		if(newtype == null || oldtype.equals(newtype))
			return false;

		ConversionType compatibility = areTypesCompatible(oldtype, newtype);
		// Implicit conversion, no changes needed
		if(compatibility == ConversionType.DONE){
			return false;
		} else{
			MethodNode method = category.getMethod();
			// if category has no parent method - might happen with abstract
			// models
			if(method == null){
				orphanCategoryTypeChange(category, compatibility, oldtype, newtype);
			} else{
				// types cannot be converted, remove everything connected
				if(compatibility == ConversionType.IMPOSSIBLE){
					impossibleCategoryTypeChange(category, oldtype, newtype);
				}
				// types can be converted
				else{
					possibleCategoryTypeChange(category, oldtype, newtype);
				}
			}
		}
		return true;
	}

	public static String adaptValueToType(String value, String type){
		switch(type){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return adaptValueToBoolean(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return adaptValueToByte(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return adaptValueToCharacter(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return adaptValueToDouble(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return adaptValueToFloat(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return adaptValueToInteger(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return adaptValueToLong(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return adaptValueToShort(value);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return adaptValueToString(value);
		default:
		}
		return null;
	}

	/*
	 * Returns 0 if types are equal, 1 if types can be converted and 2 if types
	 * cannot be converted.
	 */
	public static ConversionType areTypesCompatible(String oldtype, String newtype){
		if(oldtype.equals(newtype))
			return ConversionType.DONE;

		switch(oldtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return booleanCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return byteCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return charCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return doubleCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return floatCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return intCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return longCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return shortCompatibility(newtype);
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return stringCompatibility(newtype);
		default:
			return enumCompatibility(newtype);
		}
	}

	private static void orphanCategoryTypeChange(CategoryNode category, ConversionType compatibility, String oldtype, String newtype){
		category.setType(newtype);
		if(compatibility == ConversionType.IMPOSSIBLE){
			category.setType(newtype);
			category.getOrdinaryPartitions().clear();
			category.setDefaultValueString(null);
		} else{
			if(category.isExpected()){
				if(category.getDefaultValueString() != null && adaptValueToType(category.getDefaultValueString(), oldtype) != null)
					category.setDefaultValueString(adaptValueToType(category.getDefaultValueString(), oldtype));
				category.getOrdinaryPartitions().clear();
			}
			for(PartitionNode partition : category.getPartitions()){
				adaptOrRemovePartitions(partition, newtype);
			}
		}
		// if no conversion was possible, try to assign predefined
		// default value
		if(category.getDefaultValueString() == null && ModelUtils.getJavaTypes().contains(newtype)){
			assignDefaultValueString(category, newtype);
		}
	}

	private static void impossibleCategoryTypeChange(CategoryNode category, String oldtype, String newtype){
		MethodNode method = category.getMethod();
		// remove any mentioning constraints
		method.removeMentioningConstraints(category);
		category.getOrdinaryPartitions().clear();
		// Clear test cases
		method.getTestCases().clear();
		// add new category in place of removed one
		category.setType(newtype);

		if(!assignDefaultValueString(category, newtype))
			category.setDefaultValueString("");
	}

	private static void possibleCategoryTypeChange(CategoryNode category, String oldtype, String newtype){
		MethodNode method = category.getMethod();
		int index = method.getCategories().indexOf(category);
		category.setType(newtype);
		// Expected Category
		if(category.isExpected()){
			// try to adapt or assign new default value
			if(category.getDefaultValueString() != null){
				String newvalue = adaptValueToType(category.getDefaultValueString(), newtype);
				if(newvalue != null){
					category.setDefaultValueString(newvalue);
				} else{
					assignDefaultValueString(category, newtype);
				}
			}
			// remove regular partitions in case there were any
			category.getOrdinaryPartitions().clear();
			// adapt or remove test cases
			Iterator<TestCaseNode> iterator = method.getTestCases().iterator();
			while(iterator.hasNext()){
				TestCaseNode testCase = iterator.next();
				String tcvalue = adaptValueToType(testCase.getTestData().get(index).getValueString(), newtype);
				if(tcvalue == null){
					iterator.remove();
				} else{
					testCase.getTestData().get(index).setValueString(tcvalue);
				}
			}
			method.removeMentioningConstraints(category);
		}
		// Partitioned Category
		else{
			// Try to adapt partitions; If it fails - remove
			// partition. Mentioning test cases and constraints are
			// handled in model.
			Iterator<PartitionNode> itr = category.getPartitions().iterator();
			while(itr.hasNext()){
				PartitionNode partition = itr.next();
				if(!adaptOrRemovePartitions(partition, newtype)){
					category.removePartition(partition);
				}
			}
			if(!assignDefaultValueString(category, newtype))
				category.setDefaultValueString(null);
		}
	}

	private static boolean assignDefaultValueString(CategoryNode category, String type){
		if(ModelUtils.getJavaTypes().contains(type)){
			String expvalue = getDefaultExpectedValueString(type);
			if(expvalue != null){
				category.setDefaultValueString(expvalue);
				return true;
			}
		}
		return false;
	}

	// returns true if adapted successfully, false if destined for removal.
	private static boolean adaptOrRemovePartitions(PartitionNode partition, String type){
		List<PartitionNode> partitions = partition.getPartitions();
		if(!partitions.isEmpty()){
			Iterator<PartitionNode> itr = partitions.iterator();
			while(itr.hasNext()){
				PartitionNode childpart = itr.next();
				if(!adaptOrRemovePartitions(childpart, type)){
					itr.remove();
					childpart.getParent().removePartition(childpart);
					childpart.getParent().partitionRemoved(partition);
				}
			}
		}
		if(partition.getPartitions().isEmpty()){
			String newvalue = adaptValueToType(partition.getValueString(), type);
			if(newvalue != null){
				partition.setValueString(newvalue);
			} else{
				return false;
			}
		}

		return true;
	}

	private static String adaptValueToBoolean(String value){
		return (Boolean.valueOf(value)).toString();
	}

	private static String adaptValueToByte(String value){
		try{
			NumberFormat formatter = NumberFormat.getInstance();
			formatter.setParseIntegerOnly(true);
			Byte numvalue = formatter.parse(value).byteValue();
			return numvalue.toString();
		} catch(ParseException e){
		}
		return null;
	}

	private static String adaptValueToCharacter(String value){
		if(value.length() == 1){
			return value;
		} else if(value.length() == 0){
			return "//0";
		} else
			try{
				byte numvalue = Byte.parseByte(value);
				return Character.toString((char)numvalue);
			} catch(NumberFormatException e){
			}
		return null;
	}

	private static String adaptValueToDouble(String value){
		try{
			NumberFormat formatter = NumberFormat.getInstance();
			Double numvalue = formatter.parse(value).doubleValue();
			return numvalue.toString();
		} catch(ParseException e){
		}
		return null;
	}

	private static String adaptValueToFloat(String value){
		try{
			NumberFormat formatter = NumberFormat.getInstance();
			Float numvalue = formatter.parse(value).floatValue();
			return numvalue.toString();
		} catch(ParseException e){
		}
		return null;
	}

	private static String adaptValueToShort(String value){
		try{
			NumberFormat formatter = NumberFormat.getInstance();
			formatter.setParseIntegerOnly(true);
			Short numvalue = formatter.parse(value).shortValue();
			return numvalue.toString();
		} catch(ParseException e){
		}
		return null;
	}

	private static String adaptValueToInteger(String value){
		try{
			NumberFormat formatter = NumberFormat.getInstance();
			formatter.setParseIntegerOnly(true);
			Integer numvalue = formatter.parse(value).intValue();
			return numvalue.toString();
		} catch(ParseException e){
		}
		return null;
	}

	private static String adaptValueToLong(String value){
		try{
			NumberFormat formatter = NumberFormat.getInstance();
			formatter.setParseIntegerOnly(true);
			Long numvalue = formatter.parse(value).longValue();
			return numvalue.toString();
		} catch(ParseException e){
		}
		return null;
	}

	private static String adaptValueToString(String value){
		return value;
	}

	private static ConversionType booleanCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			return ConversionType.DONE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType byteCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			return ConversionType.DONE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType charCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			return ConversionType.DONE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType doubleCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			return ConversionType.DONE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType floatCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			return ConversionType.DONE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType intCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			return ConversionType.DONE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType longCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			return ConversionType.DONE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType shortCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			return ConversionType.DONE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}

	private static ConversionType stringCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.DONE;
		default:
			return ConversionType.POSSIBLE;
		}
	}

	private static ConversionType enumCompatibility(String newtype){
		switch(newtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			return ConversionType.POSSIBLE;
		default:
			return ConversionType.IMPOSSIBLE;
		}
	}
}
