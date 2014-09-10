package com.testify.ecfeed.modelif.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IImplementationStatusResolver;
import com.testify.ecfeed.modelif.ImplementationStatus;

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
	
	public ImplementationStatus getImplementationStatus(GenericNode node){
		try {
			return (ImplementationStatus)node.accept(new StatusResolver());
		} catch (Exception e) {
			return ImplementationStatus.IRRELEVANT;
		}
	}

	protected ImplementationStatus implementationStatus(RootNode classNode){
		return ImplementationStatus.IRRELEVANT;
	}
	
	protected ImplementationStatus implementationStatus(ClassNode classNode){
		if(classDefinitionImplemented(classNode) == false){
			return ImplementationStatus.NOT_IMPLEMENTED;
		}
		for(MethodNode method : classNode.getMethods()){
			if(implementationStatus(method) != ImplementationStatus.IMPLEMENTED){
				return ImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return ImplementationStatus.IMPLEMENTED;
	}
	
	protected ImplementationStatus implementationStatus(MethodNode method){
		if(methodDefinitionImplemented(method) == false){
			return ImplementationStatus.NOT_IMPLEMENTED;
		}
		for(CategoryNode category : method.getCategories()){
			if(implementationStatus(category) != ImplementationStatus.IMPLEMENTED){
				return ImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		return ImplementationStatus.IMPLEMENTED;
	}
	
	protected ImplementationStatus implementationStatus(CategoryNode category){
		if(Arrays.asList(Constants.SUPPORTED_PRIMITIVE_TYPES).contains(category.getType())){
			return category.getPartitions().size() > 0 ? ImplementationStatus.IMPLEMENTED : ImplementationStatus.PARTIALLY_IMPLEMENTED;
		}
		
		Class<?> typeObject = createLoader().loadClass(category.getType());
		if(typeObject == null){
			return ImplementationStatus.NOT_IMPLEMENTED;
		}
		
		for(PartitionNode partition : category.getPartitions()){
			if(implementationStatus(partition) != ImplementationStatus.IMPLEMENTED){
				return ImplementationStatus.PARTIALLY_IMPLEMENTED;
			}
		}
		
		return ImplementationStatus.IMPLEMENTED;
	}
	
	protected ImplementationStatus implementationStatus(PartitionNode partition){
		if(partition.isAbstract() == false){
			PartitionValueParser valueParser = new PartitionValueParser(createLoader());
			String type = partition.getCategory().getType();
			if(valueParser.parseValue(partition) != null || type.equals(Constants.TYPE_NAME_STRING)){
				return ImplementationStatus.IMPLEMENTED;
			}
			return ImplementationStatus.NOT_IMPLEMENTED;
		}
		else{
			int childrenCount = partition.getPartitions().size();
			int implementedChildren = 0;
			int notImplementedChildren = 0;
			for(PartitionNode child : partition.getPartitions()){
				if(getImplementationStatus(child) == ImplementationStatus.IMPLEMENTED){
					implementedChildren++;
				}
				else if(getImplementationStatus(child) == ImplementationStatus.NOT_IMPLEMENTED){
					notImplementedChildren++;
				}
			}
			if(implementedChildren == childrenCount){
				return ImplementationStatus.IMPLEMENTED;
			}
			else if(notImplementedChildren == childrenCount){
				return ImplementationStatus.NOT_IMPLEMENTED;
			}
			return ImplementationStatus.PARTIALLY_IMPLEMENTED;
		}
	}
	
	protected ImplementationStatus implementationStatus(TestCaseNode testCase){
		int testDataSize = testCase.getTestData().size();
		int implementedParameters = 0;
		int notImplementedParameters = 0;
		for(PartitionNode partition : testCase.getTestData()){
			ImplementationStatus status = implementationStatus(partition);
			if(status == ImplementationStatus.IMPLEMENTED){
				implementedParameters++;
			}
			else if(status == ImplementationStatus.NOT_IMPLEMENTED){
				notImplementedParameters++;
			}
		}
		if(implementedParameters == testDataSize){
			return ImplementationStatus.IMPLEMENTED;
		}
		else if(notImplementedParameters == testDataSize){
			return ImplementationStatus.NOT_IMPLEMENTED;
		}
		return ImplementationStatus.PARTIALLY_IMPLEMENTED;
	}

	protected ImplementationStatus implementationStatus(ConstraintNode constraint){
		return ImplementationStatus.IRRELEVANT;
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
			for(int i = 0; i < typeNames.size(); i++){
				if(typeNames.get(i).equals(modelCategories.get(i).getName()) == false){
					continue;
				}
			}
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
