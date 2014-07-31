package com.testify.ecfeed.modelif.java;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;

public class JavaMethodUtils {
	public static ImplementationStatus implementationStatus(MethodNode method){
		if(methodDefinitionImplemented(method) == false){
			return ImplementationStatus.NOT_IMPLEMENTED;
		}
		for(CategoryNode category : method.getCategories()){
			if(JavaCategoryUtils.getImplementationStatus(category) != ImplementationStatus.IMPLEMENTED){
				return ImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return ImplementationStatus.IMPLEMENTED;
	}
	
	private static boolean methodDefinitionImplemented(MethodNode methodModel){
		URLClassLoader loader = LoaderProvider.getClassLoader(true, null);
		Class<?> parentClass = LoaderProvider.loadClass(loader, JavaClassUtils.getQualifiedName(methodModel.getClassNode()));
		if(parentClass == null){
			return false;
		}
		for(Method m : parentClass.getMethods()){
			if(m.getReturnType().equals(Void.TYPE) == false){
				break;
			}
			if(m.getName().equals(methodModel.getName()) == false){
				break;
			}
			List<String> typeNames = getArgTypes(m);
			List<CategoryNode> modelCategories = methodModel.getCategories();
			if(typeNames.size() != methodModel.getCategories().size()){
				break;
			}
			for(int i = 0; i < typeNames.size(); i++){
				if(typeNames.get(i).equals(modelCategories.get(i).getName()) == false){
					break;
				}
			}
			return true;
		}
		return false;
	}
	
	private static List<String> getArgTypes(Method method) {
		List<String> argTypes = new ArrayList<String>();
		for(Class<?> parameter : method.getParameterTypes()){
			argTypes.add(JavaUtils.getTypeName(parameter.getCanonicalName()));
		}
		return argTypes;
	}
}
