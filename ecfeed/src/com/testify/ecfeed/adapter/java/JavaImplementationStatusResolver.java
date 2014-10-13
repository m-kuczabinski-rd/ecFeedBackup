package com.testify.ecfeed.adapter.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IImplementationStatusResolver;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class JavaImplementationStatusResolver implements IImplementationStatusResolver{
	private ILoaderProvider fLoaderProvider;

	private class StatusResolver implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return implementationStatus(node);
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			return implementationStatus(node);
		}
		
	}
	
	public JavaImplementationStatusResolver(ILoaderProvider loaderProvider){
		fLoaderProvider = loaderProvider;
	}
	
	public EImplementationStatus getImplementationStatus(GenericNode node){
		try {
			return (EImplementationStatus)node.accept(new StatusResolver());
		} catch (Exception e) {
			return EImplementationStatus.IRRELEVANT;
		}
	}

	protected EImplementationStatus implementationStatus(RootNode node){
		for(ClassNode _class : node.getClasses()){
			if(implementationStatus(_class) != EImplementationStatus.IMPLEMENTED){
				return EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return EImplementationStatus.IMPLEMENTED;
	}
	
	protected EImplementationStatus implementationStatus(ClassNode classNode){
		if(classDefinitionImplemented(classNode) == false){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		for(MethodNode method : classNode.getMethods()){
			if(implementationStatus(method) != EImplementationStatus.IMPLEMENTED){
				return EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return EImplementationStatus.IMPLEMENTED;
	}
	
	protected EImplementationStatus implementationStatus(MethodNode method){
		if(methodDefinitionImplemented(method) == false){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		for(CategoryNode category : method.getCategories()){
			if(implementationStatus(category) != EImplementationStatus.IMPLEMENTED){
				return EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return EImplementationStatus.IMPLEMENTED;
	}
	
	protected EImplementationStatus implementationStatus(CategoryNode category){
		if(Arrays.asList(Constants.SUPPORTED_PRIMITIVE_TYPES).contains(category.getType())){
			return category.getPartitions().size() > 0 ? EImplementationStatus.IMPLEMENTED : EImplementationStatus.PARTIALLY_IMPLEMENTED;
		}
		
		Class<?> typeObject = createLoader().loadClass(category.getType());
		if(typeObject == null){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}

		if(category.getPartitions().size() == 0){
			return EImplementationStatus.PARTIALLY_IMPLEMENTED;
		}
		
		for(PartitionNode partition : category.getPartitions()){
			if(implementationStatus(partition) != EImplementationStatus.IMPLEMENTED){
				return EImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		
		return EImplementationStatus.IMPLEMENTED;
	}
	
	protected EImplementationStatus implementationStatus(PartitionNode partition){
		if(partition.isAbstract() == false){
			PartitionValueParser valueParser = new PartitionValueParser(createLoader());
			String type = partition.getCategory().getType();
			if(valueParser.parseValue(partition) != null || type.equals(Constants.TYPE_NAME_STRING)){
				return EImplementationStatus.IMPLEMENTED;
			}
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		else{
			int childrenCount = partition.getPartitions().size();
			int implementedChildren = 0;
			int notImplementedChildren = 0;
			for(PartitionNode child : partition.getPartitions()){
				if(getImplementationStatus(child) == EImplementationStatus.IMPLEMENTED){
					implementedChildren++;
				}
				else if(getImplementationStatus(child) == EImplementationStatus.NOT_IMPLEMENTED){
					notImplementedChildren++;
				}
			}
			if(implementedChildren == childrenCount){
				return EImplementationStatus.IMPLEMENTED;
			}
			else if(notImplementedChildren == childrenCount){
				return EImplementationStatus.NOT_IMPLEMENTED;
			}
			return EImplementationStatus.PARTIALLY_IMPLEMENTED;
		}
	}
	
	protected EImplementationStatus implementationStatus(TestCaseNode testCase){
		int testDataSize = testCase.getTestData().size();
		int implementedParameters = 0;
		int notImplementedParameters = 0;
		for(PartitionNode partition : testCase.getTestData()){
			EImplementationStatus status = implementationStatus(partition);
			if(status == EImplementationStatus.IMPLEMENTED){
				implementedParameters++;
			}
			else if(status == EImplementationStatus.NOT_IMPLEMENTED){
				notImplementedParameters++;
			}
		}
		if(implementedParameters == testDataSize){
			return EImplementationStatus.IMPLEMENTED;
		}
		else if(notImplementedParameters == testDataSize){
			return EImplementationStatus.NOT_IMPLEMENTED;
		}
		return EImplementationStatus.PARTIALLY_IMPLEMENTED;
	}

	protected EImplementationStatus implementationStatus(ConstraintNode constraint){
		return EImplementationStatus.IRRELEVANT;
	}

	private boolean classDefinitionImplemented(ClassNode classNode) {
		return (createLoader().loadClass(classNode.getQualifiedName()) != null);
	}
	
	private boolean methodDefinitionImplemented(MethodNode methodModel){
		Class<?> parentClass = createLoader().loadClass(JavaUtils.getQualifiedName(methodModel.getClassNode()));
		if(parentClass == null){
			return false;
		}
		for(Method m : parentClass.getMethods()){
			if(m.getReturnType().equals(Void.TYPE) == false){
				continue;
			}
			if(m.getName().equals(methodModel.getName()) == false){
				continue;
			}
			List<String> typeNames = getArgTypes(m);
			List<CategoryNode> modelCategories = methodModel.getCategories();
			if(typeNames.size() != methodModel.getCategories().size()){
				continue;
			}
			List<String> modelTypeNames = new ArrayList<>();
			for(CategoryNode parameter : modelCategories){
				modelTypeNames.add(parameter.getType());
			}
			if(modelTypeNames.equals(typeNames) == false){
				continue;
			}
			
//			for(int i = 0; i < typeNames.size(); i++){
//				String type = typeNames.get(i);
//				String modelType = modelCategories.get(i).getType();
//				if(typeNames.get(i).equals(modelCategories.get(i).getType()) == false){
//					continue;
//				}
//			}
			return true;
		}
		return false;
	}
	
	private List<String> getArgTypes(Method method) {
		List<String> argTypes = new ArrayList<String>();
		for(Class<?> parameter : method.getParameterTypes()){
			argTypes.add(JavaUtils.getTypeName(parameter.getCanonicalName()));
		}
		return argTypes;
	}

	private ModelClassLoader createLoader(){
		return fLoaderProvider.getLoader(true, null);
	}

}
