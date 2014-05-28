package com.testify.ecfeed.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

import static com.testify.ecfeed.utils.ModelUtils.getDefaultExpectedValue;

public class AdaptTypeSupport{

	public static String[] getSupportedTypes(){
		return new String[] { com.testify.ecfeed.model.Constants.TYPE_NAME_STRING, com.testify.ecfeed.model.Constants.TYPE_NAME_INT,
				com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN, com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE,
				com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE, com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR,
				com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT, com.testify.ecfeed.model.Constants.TYPE_NAME_LONG,
				com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT };
	}

	private static boolean assignDefaultValue(CategoryNode category, String type){
		if(Arrays.asList(getSupportedTypes()).contains(type)){
			Object expvalue = getDefaultExpectedValue(type);
			if(expvalue != null){
				category.setDefaultValue(expvalue);
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
					category.setDefaultValue(null);
				} else{
					if(category.isExpected()){
						if(category.getDefaultValue() != null && adaptValueToType(category.getDefaultValue(), oldtype) != null)
							category.setDefaultValue(adaptValueToType(category.getDefaultValue(), oldtype));
						category.getOrdinaryPartitions().clear();
					}
					for(PartitionNode partition : category.getPartitions()){
						adaptOrRemovePartitions(partition, type);
					}
				}
				// if no conversion was possible, try to assign predefined
				// default value
				if(category.getDefaultValue() == null && Arrays.asList(getSupportedTypes()).contains(type)){
					assignDefaultValue(category, type);
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

					if(!assignDefaultValue(category, type))
						category.setDefaultValue(null);
				}
				// types can be converted
				else{
					category.setType(type);
					// Expected Category
					if(category.isExpected()){
						// try to adapt or assign new default value
						if(category.getDefaultValue() != null){
							Object newvalue = adaptValueToType(category.getDefaultValue(), type);
							if(newvalue != null){
								category.setDefaultValue(newvalue);
							} else{
								assignDefaultValue(category, type);
							}
						}
						// remove regular partitions in case there were any
						category.getOrdinaryPartitions().clear();
						// adapt or remove test cases
						Iterator<TestCaseNode> iterator = method.getTestCases().iterator();
						while(iterator.hasNext()){
							TestCaseNode testCase = iterator.next();
							Object tcvalue = adaptValueToType(testCase.getTestData().get(index).getValue(), type);
							if(tcvalue == null){
								iterator.remove();
							} else{
								testCase.getTestData().get(index).setValue(tcvalue);
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
						if(!assignDefaultValue(category, type))
							category.setDefaultValue(null);
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
			Object newvalue = adaptValueToType(partition.getValue(), type);
			if(newvalue != null){
				partition.setValue(newvalue);
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

	public static Object adaptValueToType(Object value, String type){
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

	private static Object adaptValueToBoolean(Object value){
		if(value instanceof Boolean){
			return value;
		} else if(value instanceof Byte){
			if((Byte)value != 0)
				return true;
			else
				return false;
		} else if(value instanceof Short){
			if((Short)value != 0)
				return true;
			else
				return false;
		} else if(value instanceof Integer){
			if((Integer)value != 0)
				return true;
			else
				return false;
		} else if(value instanceof Long){
			if((Long)value != 0)
				return true;
			else
				return false;
		} else if(value instanceof String){
			return Boolean.parseBoolean((String)value);
		}
		return null;
	}

	private static Object adaptValueToByte(Object value){
		try{
			if(value instanceof Byte){
				return value;
			} else if(value instanceof Boolean){
				if((Boolean)value)
					return (byte)1;
				else
					return (byte)0;
			} else if(value instanceof Short){
				return (byte)((short)value);
			} else if(value instanceof Integer){
				return (byte)((int)value);
			} else if(value instanceof Long){
				return (byte)((long)value);
			} else if(value instanceof Character){
				return (byte)((char)value);
			} else if(value instanceof String){
				return Byte.parseByte((String)value);
			} else if(value instanceof Float){
				return (byte)((float)value);
			} else if(value instanceof Double){
				return (byte)((double)value);
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	private static Object adaptValueToCharacter(Object value){
		if(value instanceof Character){
			return value;
		} else if(value instanceof Byte){
			return (char)((byte)value);
		} else if(value instanceof Short){
			return (char)((short)value);
		} else if(value instanceof String){
			if(((String)value).length() == 1)
				return ((String)value).charAt(0);
			else if(((String)value).length() == 0)
				return '\0';
		}
		return null;
	}

	private static Object adaptValueToDouble(Object value){
		try{
			if(value instanceof Double){
				return value;
			} else if(value instanceof Byte){
				return (double)(byte)value;
			} else if(value instanceof Short){
				return (double)(short)value;
			} else if(value instanceof Integer){
				return (double)(int)value;
			} else if(value instanceof Long){
				return (double)(long)value;
			} else if(value instanceof String){
				return Double.parseDouble((String)value);
			} else if(value instanceof Float){
				return (double)(float)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	private static Object adaptValueToFloat(Object value){
		try{
			if(value instanceof Float){
				return value;
			} else if(value instanceof Byte){
				return (float)(byte)value;
			} else if(value instanceof Short){
				return (float)(short)value;
			} else if(value instanceof Long){
				return (float)(long)value;
			} else if(value instanceof Integer){
				return (float)(int)value;
			} else if(value instanceof String){
				return Float.parseFloat((String)value);
			} else if(value instanceof Double){
				return (float)(double)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	private static Object adaptValueToShort(Object value){
		try{
			if(value instanceof Short){
				return value;
			} else if(value instanceof Boolean){
				if((Boolean)value)
					return (short)1;
				else
					return (short)0;
			} else if(value instanceof Byte){
				return (short)(byte)value;
			} else if(value instanceof Integer){
				return (short)(int)value;
			} else if(value instanceof Long){
				return (short)(long)value;
			} else if(value instanceof Character){
				return (short)((char)value);
			} else if(value instanceof String){
				return Short.parseShort((String)value);
			} else if(value instanceof Float){
				return (short)(float)value;
			} else if(value instanceof Double){
				return (short)(double)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	private static Object adaptValueToInteger(Object value){
		try{
			if(value instanceof Integer){
				return value;
			} else if(value instanceof Boolean){
				if((Boolean)value)
					return 1;
				else
					return 0;
			} else if(value instanceof Byte){
				return (int)(byte)value;
			} else if(value instanceof Short){
				return (int)(short)value;
			} else if(value instanceof Long){
				return (int)(long)value;
			} else if(value instanceof String){
				return Integer.parseInt((String)value);
			} else if(value instanceof Float){
				return (int)(float)value;
			} else if(value instanceof Double){
				return (int)(double)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	private static Object adaptValueToLong(Object value){
		try{
			if(value instanceof Long){
				return value;
			} else if(value instanceof Boolean){
				if((Boolean)value)
					return (long)1;
				else
					return (long)0;
			} else if(value instanceof Byte){
				return (long)(byte)value;
			} else if(value instanceof Short){
				return (long)(short)value;
			} else if(value instanceof Integer){
				return (long)(int)value;
			} else if(value instanceof String){
				return Long.parseLong((String)value);
			} else if(value instanceof Float){
				return (long)(float)value;
			} else if(value instanceof Double){
				return (long)(double)value;
			}
		} catch(NumberFormatException e){
		}
		return null;
	}

	private static Object adaptValueToString(Object value){
		try{
			if(value instanceof Boolean){
				return Boolean.toString((boolean)value);
			} else if(value instanceof Byte){
				return ((Byte)value).toString();
			} else if(value instanceof Short){
				return ((Short)value).toString();
			} else if(value instanceof Integer){
				return ((Integer)value).toString();
			} else if(value instanceof Long){
				return ((Long)value).toString();
			} else if(value instanceof Character){
				if((char)value == '\0')
					return "";
				return ((Character)value).toString();
			} else if(value instanceof String){
				return value;
			} else if(value instanceof Float){
				return ((Float)value).toString();
			} else if(value instanceof Double){
				return ((Double)value).toString();
			} else if(value != null && value instanceof Enum<?>){
				Enum<?> e = (Enum<?>)value;
				return e.name();
			}
		} catch(NumberFormatException e){
		}
		return null;
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
				return 1;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE:
				return 2;
			case com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT:
				return 2;
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
		case com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE:
			switch(newtype){
			case com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN:
				return 1;
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
				return 1;
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
				return 1;
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
				return 1;
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
				return 1;
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
				return 2;
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
