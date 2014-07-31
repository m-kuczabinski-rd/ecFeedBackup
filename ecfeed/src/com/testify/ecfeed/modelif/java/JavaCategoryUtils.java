package com.testify.ecfeed.modelif.java;

import java.net.URLClassLoader;
import java.util.Arrays;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class JavaCategoryUtils {

	public static ImplementationStatus getImplementationStatus(
			CategoryNode category) {
		if(Arrays.asList(Constants.SUPPORTED_PRIMITIVE_TYPES).contains(category.getType())){
			return category.getPartitions().size() > 0 ? ImplementationStatus.IMPLEMENTED : ImplementationStatus.PARTIALLY_IMPLEMENTED;
		}
		
		URLClassLoader loader = LoaderProvider.getClassLoader(false, null);
		Class<?> typeObject = LoaderProvider.loadClass(loader, category.getType());
		if(typeObject == null){
			return ImplementationStatus.NOT_IMPLEMENTED;
		}
		
		for(PartitionNode partition : category.getPartitions()){
			if(JavaPartitionUtils.getImplementationStatus(partition) != ImplementationStatus.IMPLEMENTED){
				return ImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		
		return ImplementationStatus.IMPLEMENTED;
	}

}
