package com.testify.ecfeed.modelif.java;

import java.net.URLClassLoader;

import com.testify.ecfeed.model.PartitionNode;

public class JavaPartitionUtils {
	
	public JavaPartitionUtils(URLClassLoader loader) {
	}

	public static ImplementationStatus getImplementationStatus(PartitionNode partition){
//		if(getPartitionValue(partition) == null){
//			return ImplementationStatus.IMPLEMENTED;
//		}
		return ImplementationStatus.NOT_IMPLEMENTED;
	}

//	public static Object getPartitionValue(PartitionNode partition) {
//		switch (partition.getCategory().getType()){
//		
//		}
//	}
}
