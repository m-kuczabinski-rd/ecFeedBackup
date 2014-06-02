package com.testify.ecfeed.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

import static com.testify.ecfeed.utils.ModelUtils.getDefaultExpectedValueString;

public class AdaptTypeSupport{

	public static String[] getSupportedTypes(){
		return new String[] { com.testify.ecfeed.model.Constants.TYPE_NAME_STRING, com.testify.ecfeed.model.Constants.TYPE_NAME_INT,
				com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN, com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE,
				com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE, com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR,
				com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT, com.testify.ecfeed.model.Constants.TYPE_NAME_LONG,
				com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT };
	}

	private static boolean assignDefaultValueString(CategoryNode category, String type){
		if(Arrays.asList(getSupportedTypes()).contains(type)){
			String expvalue = getDefaultExpectedValueString(type);
			if(expvalue != null){
				category.setDefaultValueString(expvalue);
				return true;
			}
		}
		return false;
	}

	// returns true if model has changed in any way
	public static boolean changeCategoryType(CategoryNode category, String type){
		String oldtype = category.getType();
		// If type is exactly the same or null or whatever else might happen
		if(oldtype == null)
			oldtype = "";
		if(type == null || oldtype.equals(type))
			return false;

		int compatibility = areTypesCompatible(oldtype, type);
		// Implicit conversion, no changes needed
		if(compatibility == 0){
			return false;
		} else{
			MethodNode method = category.getMethod();
			// if category has no parent method - might happen with abstract
			// models
			if(method == null){
				category.setType(type);
				if(compatibility == 2){
					category.setType(type);
					category.getOrdinaryPartitions().clear();
					category.setDefaultValueString(null);
				} else{
					if(category.isExpected()){
						if(category.getDefaultValueString() != null && adaptValueToType(category.getDefaultValueString(), oldtype) != null)
							category.setDefaultValueString(adaptValueToType(category.getDefaultValueString(), oldtype));
						category.getOrdinaryPartitions().clear();
					}
					for(PartitionNode partition : category.getPartitions()){
						adaptOrRemovePartitions(partition, type);
					}
				}
				// if no conversion was possible, try to assign predefined
				// default value
				if(category.getDefaultValueString() == null && Arrays.asList(getSupportedTypes()).contains(type)){
					assignDefaultValueString(category, type);
				}
			} else{
				int index = method.getCategories().indexOf(category);
				// types cannot be converted, remove everything connected
				if(compatibility == 2){
					// remove any mentioning constraints
					method.removeMentioningConstraints(category);
					category.getOrdinaryPartitions().clear();
					// Clear test cases
					method.getTestCases().clear();
					// add new category in place of removed one
					category.setType(type);

					if(!assignDefaultValueString(category, type))
						category.setDefaultValueString(null);
				}
				// types can be converted
				else{
					category.setType(type);
					// Expected Category
					if(category.isExpected()){
						// try to adapt or assign new default value
						if(category.getDefaultValueString() != null){
							String newvalue = adaptValueToType(category.getDefaultValueString(), type);
							if(newvalue != null){
								category.setDefaultValueString(newvalue);
							} else{
								assignDefaultValueString(category, type);
							}
						}
						// remove regular partitions in case there were any
						category.getOrdinaryPartitions().clear();
						// adapt or remove test cases
						Iterator<TestCaseNode> iterator = method.getTestCases().iterator();
						while(iterator.hasNext()){
							TestCaseNode testCase = iterator.next();
							String tcvalue = adaptValueToType(testCase.getTestData().get(index).getValueString(), type);
							if(tcvalue == null){
								iterator.remove();
							} else{
								testCase.getTestData().get(index).setValueString(tcvalue);
							}
						}
						// adapting constraints of expected category would be
						// really messy. Might add it at later date if need
						// occurs.
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
							if(!adaptOrRemovePartitions(partition, type))
								itr.remove();
						}
						if(!assignDefaultValueString(category, type))
							category.setDefaultValueString(null);
					}
				}
			}
		}
		return true;
	}

	// returns true if adapted successfully, false if destined for removal.
	private static boolean adaptOrRemovePartitions(PartitionNode partition, String type){
		List<PartitionNode> partitions = partition.getLeafPartitions();
		if(partitions.size() == 1 && partitions.contains(partition)){
			String newvalue = adaptValueToType(partition.getValueString(), type);
			if(newvalue != null){
				partition.setValueString(newvalue);
			} else{
				return false;
				// partition.getParent().removePartition(partition);
			}
		} else{
			Iterator<PartitionNode> itr = partitions.iterator();
			while(itr.hasNext()){
				PartitionNode childpart = itr.next();
				if(!adaptOrRemovePartitions(childpart, type))
					itr.remove();
				partition.partitionRemoved(partition);
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

	private static String adaptValueToBoolean(String value){
			return (Boolean.valueOf(value)).toString();
	}

	private static String adaptValueToByte(String value){
		// char to byte... Needed? If so, 2nd argument with type is needed. Or just make one method for all this stuff.
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
		}
		else if(value.length() == 0){
			return "//0";
		}
		else try{
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

	/*
	 * Returns 0 if types are equal, 1 if types can be converted and 2 if types
	 * cannot be converted.
	 */
	public static int areTypesCompatible(String oldtype, String newtype){
		if(oldtype.equals(newtype))
			return 0;

		switch(oldtype){
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 0;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_INT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_LONG:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT:
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 0;
			default:
				return 1;
			}
			// User-defined convert to string only.
		default:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_STRING:
				return 1;
			default:
				return 2;
			}
		}
	}

}
