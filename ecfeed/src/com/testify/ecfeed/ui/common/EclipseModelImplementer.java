package com.testify.ecfeed.ui.common;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class EclipseModelImplementer extends AbstractModelImplementer {

	private IFileInfoProvider fFileInfoProvider;

	public EclipseModelImplementer(IFileInfoProvider fileInfoProvider) {
		super(new EclipseImplementationStatusResolver());
		fFileInfoProvider = fileInfoProvider;
	}
	
	@Override
	public boolean implement(GenericNode node){
		boolean result = super.implement(node);
		if(result){
			CachedImplementationStatusResolver.clearCache(node);
		}
		CachedImplementationStatusResolver.clearCache(node);
		return result; 
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
			for(CategoryNode parameter : node.getCategories()){
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
	protected void implementParameterDefinition(CategoryNode node) throws CoreException {
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
		unit.createType(enumDefinitionContent(node), null, false, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void implementPartitionDefinition(PartitionNode node) throws CoreException {
		String typeName = node.getCategory().getType();
		IType enumType = getJavaProject().findType(typeName);
		CompilationUnit unit = getCompilationUnit(enumType);
		EnumDeclaration enumDeclaration = getEnumDeclaration(unit, typeName);
		if(enumDeclaration != null){
			EnumConstantDeclaration constant = unit.getAST().newEnumConstantDeclaration();
			constant.setName(unit.getAST().newSimpleName(node.getValueString()));
			enumDeclaration.enumConstants().add(constant);
			saveChanges(unit, enumType.getResource().getLocation());
		}
	}

	protected boolean implementable(ClassNode node){
		if(classDefinitionImplemented(node)){
			return hasImplementableNode(node.getMethods());
		}
		return classDefinitionImplementable(node);
	}
	
	protected boolean implementable(MethodNode node){
		if(methodDefinitionImplemented(node)){
			return hasImplementableNode(node.getCategories()) || hasImplementableNode(node.getTestCases());
		}
		return methodDefinitionImplementable(node);
	}
	
	protected boolean implementable(CategoryNode node){
		if(parameterDefinitionImplemented(node)){
			return hasImplementableNode(node.getPartitions());
		}
		return parameterDefinitionImplementable(node);
	}
	
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
	protected boolean parameterDefinitionImplemented(CategoryNode node) {
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
		String content = "System.out.println(\"" + node.getName() + "(\" + ";
				
		for(int i = 0; i < node.getCategories().size(); ++i){
			CategoryNode parameter = node.getCategories().get(i);
			args += JavaUtils.getLocalName(parameter.getType()) + " " + parameter.getName();
			content += node.getCategories().get(i).getName();
			if(i != node.getCategories().size() - 1){
				args += ", ";
				content += " + \", \" + ";
			}
		}
		content += " + \")\");";
		return "public void " + node.getName() + "(" + args + "){\n\t" + comment + "\n\t" + content + "\n}";
	}
	
	protected String enumDefinitionContent(CategoryNode node){
		return "public enum " + JavaUtils.getLocalName(node.getType()) + "{\n\n}";
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

	private boolean parameterDefinitionImplementable(CategoryNode parameter) {
		try {
			return getJavaProject().findType(parameter.getType()) == null;
		}catch (CoreException e) {
		}
		return false;
	}

	private CompilationUnit getCompilationUnit(IType type) throws CoreException{
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
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
}
