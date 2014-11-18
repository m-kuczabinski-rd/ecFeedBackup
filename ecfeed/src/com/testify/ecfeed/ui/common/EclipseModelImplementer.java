package com.testify.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import com.testify.ecfeed.adapter.AbstractModelImplementer;
import com.testify.ecfeed.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class EclipseModelImplementer extends AbstractModelImplementer {

	private final IFileInfoProvider fFileInfoProvider;

	public EclipseModelImplementer(IFileInfoProvider fileInfoProvider) {
		super(new EclipseImplementationStatusResolver());
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public boolean implement(GenericNode node){
		refreshWorkspace();
		boolean result = super.implement(node);
		CachedImplementationStatusResolver.clearCache(node);
		refreshWorkspace();
		return result;
	}

	@Override
	protected boolean implement(ParameterNode node) throws CoreException{
		if(parameterDefinitionImplemented(node) == false){
			implementParameterDefinition(node, node.getLeafPartitionValues());
		}
		else{
			List<PartitionNode> unimplemented = unimplementedChoices(node.getLeafPartitions());
			implementChoicesDefinitions(unimplemented);
			for(PartitionNode choice : unimplemented){
				CachedImplementationStatusResolver.clearCache(choice);
			}
		}
		return true;
	}

	@Override
	protected boolean implement(PartitionNode node) throws CoreException{
		ParameterNode parameter = node.getCategory();
		if(parameterDefinitionImplemented(parameter) == false){
			if(parameterDefinitionImplementable(parameter)){
				implementParameterDefinition(parameter, new HashSet<String>(Arrays.asList(new String[]{node.getValueString()})));
			}
			else{
				return false;
			}
		}
		else{
			if(node.isAbstract()){
				implementChoicesDefinitions(unimplementedChoices(node.getLeafPartitions()));
			}
			else{
				if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
					implementChoicesDefinitions(Arrays.asList(new PartitionNode[]{node}));
				}
			}
		}
		return true;
	}

	@Override
	protected void implementClassDefinition(ClassNode node) throws CoreException {
		String packageName = JavaUtils.getPackageName(node.getName());
		String className = JavaUtils.getLocalName(node.getName());
		String unitName = className + ".java";
		IPackageFragment packageFragment = getPackageFragment(packageName);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);
		unit.createType(classDefinitionContent(node), null, false, null);
	}

	@Override
	protected void implementMethodDefinition(MethodNode node) throws CoreException {
		if(classDefinitionImplemented(node.getClassNode()) == false){
			implementClassDefinition(node.getClassNode());
		}
		IType classType = getJavaProject().findType(JavaUtils.getQualifiedName(node.getClassNode()));
		if(classType != null){
			classType.createMethod(methodDefinitionContent(node), null, false, null);
			for(ParameterNode parameter : node.getCategories()){
				String type = parameter.getType();
				if(JavaUtils.isUserType(type)){
					String packageName = JavaUtils.getPackageName(type);
					if(packageName.equals(JavaUtils.getPackageName(node.getClassNode())) == false){
						classType.getCompilationUnit().createImport(type, null, null);
					}
				}
			}
		}
	}

	@Override
	protected void implementParameterDefinition(ParameterNode node) throws CoreException {
		implementParameterDefinition(node, null);
	}

	protected void implementParameterDefinition(ParameterNode node, Set<String> fields) throws CoreException {
		String typeName = node.getType();
		if(JavaUtils.isPrimitive(typeName)){
			return;
		}
		if(JavaUtils.isValidTypeName(typeName) == false){
			return;
		}
		String packageName = JavaUtils.getPackageName(typeName);
		String localName = JavaUtils.getLocalName(typeName);
		String unitName = localName + ".java";
		IPackageFragment packageFragment = getPackageFragment(packageName);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);
		unit.createType(enumDefinitionContent(node, fields), null, false, null);
	}

	@Override
	protected void implementChoiceDefinition(PartitionNode node) throws CoreException {
		if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
			implementChoicesDefinitions(Arrays.asList(new PartitionNode[]{node}));
		}
	}

	@SuppressWarnings("unchecked")
	protected void implementChoicesDefinitions(List<PartitionNode> nodes) throws CoreException {
		refreshWorkspace();
		ParameterNode parent = getParameter(nodes);
		if(parent == null){
			return;
		}
		String typeName = parent.getType();
		if(parameterDefinitionImplemented(parent) == false){
			implementParameterDefinition(parent);
		}
		IType enumType = getJavaProject().findType(typeName);
		CompilationUnit unit = getCompilationUnit(enumType);
		EnumDeclaration enumDeclaration = getEnumDeclaration(unit, typeName);
		if(enumDeclaration != null){
			for(PartitionNode node : nodes){
				EnumConstantDeclaration constant = unit.getAST().newEnumConstantDeclaration();
				constant.setName(unit.getAST().newSimpleName(node.getValueString()));
				enumDeclaration.enumConstants().add(constant);
			}
			saveChanges(unit, enumType.getResource().getLocation());
		}
		enumType.getResource().refreshLocal(IResource.DEPTH_ONE, null);
		refreshWorkspace();
	}

	@Override
	protected boolean implementable(ClassNode node){
		if(classDefinitionImplemented(node)){
			return hasImplementableNode(node.getMethods());
		}
		return classDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(MethodNode node){
		if(methodDefinitionImplemented(node)){
			return hasImplementableNode(node.getCategories()) || hasImplementableNode(node.getTestCases());
		}
		return methodDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(ParameterNode node){
		if(parameterDefinitionImplemented(node)){
			return hasImplementableNode(node.getPartitions());
		}
		return parameterDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(PartitionNode node){
		if(node.isAbstract()){
			return hasImplementableNode(node.getPartitions());
		}
		if(parameterDefinitionImplemented(node.getCategory())){
			try{
				IType type = getJavaProject().findType(node.getCategory().getType());
				if(type.isEnum() == false){
					return false;
				}
				boolean hasConstructor = false;
				boolean hasParameterlessConstructor = false;
				for(IMethod constructor : type.getMethods()){
					if(constructor.isConstructor() == false){
						continue;
					}
					hasConstructor = true;
					if(constructor.getNumberOfParameters() == 0){
						hasParameterlessConstructor = true;
					}
				}
				if(hasConstructor && (hasParameterlessConstructor == false)){
					return false;
				}
			}
			catch(CoreException e){
				return false;
			}
		}
		else{
			if(parameterDefinitionImplementable(node.getCategory()) == false){
				return false;
			}
		}

		return JavaUtils.isValidJavaIdentifier(node.getValueString());
	}

	@Override
	protected boolean classDefinitionImplemented(ClassNode node) {
		try{
			IType type = getJavaProject().findType(node.getName());
			return (type != null) && type.isClass();
		}catch(CoreException e){}
		return false;
	}

	@Override
	protected boolean methodDefinitionImplemented(MethodNode node) {
		try{
			IType type = getJavaProject().findType(node.getClassNode().getName());
			if(type == null){
				return false;
			}
			EclipseModelBuilder builder = new EclipseModelBuilder();
			for(IMethod method : type.getMethods()){
				MethodNode model = builder.buildMethodModel(method);
				if(model != null && model.getName().equals(node.getName()) && model.getCategoriesTypes().equals(node.getCategoriesTypes())){
					return true;
				}
			}
		}catch(CoreException e){}
		return false;
	}

	@Override
	protected boolean parameterDefinitionImplemented(ParameterNode node) {
		try{
			IType type = getJavaProject().findType(node.getType());
			return (type != null) && type.isEnum();
		}catch(CoreException e){}
		return false;
	}

	protected String classDefinitionContent(ClassNode node){
		return "public class " + JavaUtils.getLocalName(node) + "{\n\n}";
	}

	protected String methodDefinitionContent(MethodNode node){
		String args = "";
		String comment = "// TODO Auto-generated method stub";
		String content = "System.out.println(\"" + node.getName() + "(";

		if(node.getCategories().size() > 0){
			content +=  "\" + ";
			for(int i = 0; i < node.getCategories().size(); ++i){
				ParameterNode parameter = node.getCategories().get(i);
				args += JavaUtils.getLocalName(parameter.getType()) + " " + parameter.getName();
				content += node.getCategories().get(i).getName();
				if(i != node.getCategories().size() - 1){
					args += ", ";
					content += " + \", \"";
				}
				content += " + ";
			}
			content += "\")\");";
		}
		else{
			content += ")\");";
		}
		return "public void " + node.getName() + "(" + args + "){\n\t" + comment + "\n\t" + content + "\n}";
	}

	protected String enumDefinitionContent(ParameterNode node, Set<String> fields){
		String fieldsDefinition = "";
		if(fields != null){
			for(String field: fields){
				fieldsDefinition += field + ", ";
			}
			fieldsDefinition = fieldsDefinition.substring(0, fieldsDefinition.length() - 2);
		}
		String result = "public enum " + JavaUtils.getLocalName(node.getType()) + "{\n\t" + fieldsDefinition + "\n}";
		return result;
	}

	private boolean methodDefinitionImplementable(MethodNode node) {
		if(classDefinitionImplemented(node.getClassNode()) == false){
			if(classDefinitionImplementable(node.getClassNode()) == false){
				return false;
			}
		}
		try{
			IType type = getJavaProject().findType(node.getClassNode().getName());
			EclipseModelBuilder builder = new EclipseModelBuilder();
			if(type != null){
				for(IMethod method : type.getMethods()){
					MethodNode model = builder.buildMethodModel(method);
					if(model.getName().equals(node.getName()) && model.getCategoriesTypes().equals(node.getCategoriesTypes())){
						return hasImplementableNode(node.getChildren());
					}
				}
			}
			return true;
		}catch(CoreException e){}
		return false;
	}

	private boolean classDefinitionImplementable(ClassNode node) {
		try{
			return getJavaProject().findType(node.getName()) == null;
		}catch(CoreException e){}
		return false;
	}

	private boolean parameterDefinitionImplementable(ParameterNode parameter) {
		try {
			String type = parameter.getType();
			if(JavaUtils.isPrimitive(type)){
				return false;
			}
			else{
				return getJavaProject().findType(type) == null;
			}
		}catch (CoreException e) {
		}
		return false;
	}

	private CompilationUnit getCompilationUnit(IType type) throws CoreException{
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(type.getCompilationUnit());
		CompilationUnit unit = (CompilationUnit)parser.createAST(null);
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		IPath path = type.getResource().getLocation();
		bufferManager.connect(path, LocationKind.LOCATION, null);
		unit.recordModifications();
		return unit;
	}

	private EnumDeclaration getEnumDeclaration(CompilationUnit unit, String typeName) {
		String className = JavaUtils.getLocalName(typeName);
		for (Object object : unit.types()) {
			AbstractTypeDeclaration declaration = (AbstractTypeDeclaration)object;
			if (declaration.getName().toString().equals(className) && declaration instanceof EnumDeclaration) {
				return (EnumDeclaration)declaration;
			}
		}
		return null;
	}

	private void saveChanges(CompilationUnit unit, IPath location) throws CoreException {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(location, LocationKind.LOCATION);
		IDocument document = textFileBuffer.getDocument();
		TextEdit edits = unit.rewrite(document, null);
		try {
			edits.apply(document);
			textFileBuffer.commit(null, false);
			bufferManager.disconnect(location, LocationKind.LOCATION, null);
			refreshWorkspace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private IJavaProject getJavaProject() throws CoreException{
		if(fFileInfoProvider.getProject().hasNature(JavaCore.NATURE_ID)){
			return JavaCore.create(fFileInfoProvider.getProject());
		}
		return null;
	}

	private IPackageFragment getPackageFragment(String name) throws CoreException{
		IPackageFragmentRoot packageFragmentRoot = getPackageFragmentRoot();
		IPackageFragment packageFragment = packageFragmentRoot.getPackageFragment(name);
		if(packageFragment.exists() == false){
			packageFragment = packageFragmentRoot.createPackageFragment(name, false, null);
		}
		return packageFragment;
	}

	private IPackageFragmentRoot getPackageFragmentRoot() throws CoreException{
		IPackageFragmentRoot root = fFileInfoProvider.getPackageFragmentRoot();
		if(root == null){
			root = getAnySourceFolder();
		}
		if(root == null){
			root = createNewSourceFolder("src");
		}
		return root;
	}

	private IPackageFragmentRoot getAnySourceFolder() throws CoreException {
		if(fFileInfoProvider.getProject().hasNature(JavaCore.NATURE_ID)){
			IJavaProject project = JavaCore.create(fFileInfoProvider.getProject());
			for (IPackageFragmentRoot packageFragmentRoot: project.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
					return packageFragmentRoot;
				}
			}
		}
		return null;
	}

	private IPackageFragmentRoot createNewSourceFolder(String name) throws CoreException {
		IProject project = fFileInfoProvider.getProject();
		IJavaProject javaProject = JavaCore.create(project);
		IFolder srcFolder = project.getFolder(name);
		int i = 0;
		while(srcFolder.exists()){
			String newName = name + i++;
			srcFolder = project.getFolder(newName);
		}
		srcFolder.create(false, true, null);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(srcFolder);

		IClasspathEntry[] entries = javaProject.getRawClasspath();
		IClasspathEntry[] updated = new IClasspathEntry[entries.length + 1];
		System.arraycopy(entries, 0, updated, 0, entries.length);
		updated[entries.length] = JavaCore.newSourceEntry(root.getPath());
		javaProject.setRawClasspath(updated, null);
		return root;
	}

	private List<PartitionNode> unimplementedChoices(List<PartitionNode> choices){
		List<PartitionNode> unimplemented = new ArrayList<>();
		for(PartitionNode choice : choices){
			if(implementable(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				unimplemented.add(choice);
			}
		}
		return unimplemented;
	}

	private ParameterNode getParameter(List<PartitionNode> nodes) {
		if(nodes.size() == 0){
			return null;
		}
		ParameterNode parameter = nodes.get(0).getCategory();
		for(PartitionNode node : nodes){
			if(node.getCategory() != parameter){
				return null;
			}
		}
		return parameter;
	}

	private void refreshWorkspace() {
		try {
			getJavaProject().getProject().getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
