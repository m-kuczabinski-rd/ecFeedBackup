/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
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

import com.ecfeed.android.external.AndroidMethodImplementerExt;
import com.ecfeed.android.external.AndroidUserClassImplementerExt;
import com.ecfeed.android.external.IClassImplementHelper;
import com.ecfeed.android.external.IImplementerExt;
import com.ecfeed.android.external.IInstallationDirFileHelper;
import com.ecfeed.android.external.IMethodImplementHelper;
import com.ecfeed.android.external.IProjectHelper;
import com.ecfeed.android.external.ImplementerExt;
import com.ecfeed.core.adapter.AbstractJavaModelImplementer;
import com.ecfeed.core.adapter.CachedImplementationStatusResolver;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.PackageClassHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.utils.EclipsePackageFragmentGetter;
import com.ecfeed.ui.common.utils.EclipseProjectHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.common.utils.JavaUserClassImplementer;

public class EclipseModelImplementer extends AbstractJavaModelImplementer {

	private final IFileInfoProvider fFileInfoProvider;

	public EclipseModelImplementer(IFileInfoProvider fileInfoProvider) {
		super(new EclipseImplementationStatusResolver(fileInfoProvider));
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public boolean implement(AbstractNode node) throws Exception{
		refreshWorkspace();
		boolean result = super.implement(node);
		CachedImplementationStatusResolver.clearCache(node);
		refreshWorkspace();
		return result;
	}

	@Override
	protected boolean implement(AbstractParameterNode parameterNode) throws CoreException, EcException{
		validateIfUserType(parameterNode);

		if(parameterDefinitionImplemented(parameterNode) == false){
			implementParameterDefinition(parameterNode, parameterNode.getLeafChoiceValues());
		}
		else{
			List<ChoiceNode> unimplemented = unimplementedChoices(parameterNode.getLeafChoices());
			implementChoicesDefinitions(unimplemented);
			for(ChoiceNode choice : unimplemented){
				CachedImplementationStatusResolver.clearCache(choice);
			}
		}
		return true;
	}

	private void validateIfUserType(AbstractParameterNode parameterNode) throws EcException {
		String type = parameterNode.getType();
		if (!JavaUtils.isUserType(type)) {
			return;
		}

		String thePackage = PackageClassHelper.getPackage(type);
		if (thePackage != null) {
			return;
		}

		AbstractNode parentNode = parameterNode.getParent();
		if (!(parentNode instanceof MethodNode)) {
			final String PACKAGE_NAME_REQUIRED_1 = "Package name is required for user type: %s (parameter: %s).";
			EcException.report(String.format(PACKAGE_NAME_REQUIRED_1, type, parameterNode.getName()));
		}

		AbstractNode grandParentNode = parentNode.getParent();
		if (!(grandParentNode instanceof ClassNode)) {
			final String PACKAGE_NAME_REQUIRED_2 = "Package name is required for user type: %s (method: %s, parameter: %s).";
			EcException.report(String.format(PACKAGE_NAME_REQUIRED_2, type, parentNode.getName(), parameterNode.getName()));
		}

		final String PACKAGE_NAME_REQUIRED_3 = "Package name is required for user type: %s (class: %s, method: %s, parameter: %s).";
		EcException.report(String.format(PACKAGE_NAME_REQUIRED_3, type, grandParentNode.getName(), parentNode.getName(), parameterNode.getName()));
	}

	@Override
	protected boolean implement(ChoiceNode choiceNode) throws CoreException, EcException{
		AbstractParameterNode parameter = choiceNode.getParameter();
		if(parameterDefinitionImplemented(parameter) == false){
			if(parameterDefinitionImplementable(parameter)){
				implementParameterDefinition(parameter, new HashSet<String>(Arrays.asList(new String[]{choiceNode.getValueString()})));
			}
			else{
				return false;
			}
		}
		else{
			if(choiceNode.isAbstract()){
				implementChoicesDefinitions(unimplementedChoices(choiceNode.getLeafChoices()));
			}
			else{
				if(implementable(choiceNode) && getImplementationStatus(choiceNode) != EImplementationStatus.IMPLEMENTED){
					implementChoicesDefinitions(Arrays.asList(new ChoiceNode[]{choiceNode}));
				}
			}
		}
		return true;
	}

	@Override
	protected void implementAndroidCode(ClassNode classNode) throws EcException {
		ImplementerExt implementer = createImplementer(classNode);
		implementer.implementContent();
	}

	@Override
	protected void implementClassDefinition(ClassNode classNode) throws CoreException, EcException {
		String projectPath = new EclipseProjectHelper(fFileInfoProvider).getProjectPath();
		IClassImplementHelper implementHelper = new EclipseClassImplementHelper(fFileInfoProvider);

		String thePackage = JavaUtils.getPackageName(classNode.getName());
		String classNameWithoutExtension = JavaUtils.getLocalName(classNode.getName());

		if (classNode.getRunOnAndroid()) {
			AndroidUserClassImplementerExt.implementContent(
					projectPath, thePackage, classNameWithoutExtension, implementHelper);
		} else {
			JavaUserClassImplementer implementer = 
					new JavaUserClassImplementer(
							projectPath, thePackage, classNameWithoutExtension, implementHelper);
			implementer.implementContent();
		}
	}

	@Override
	protected void implementMethodDefinition(MethodNode methodNode) throws CoreException, EcException {
		if(!classDefinitionImplemented(methodNode.getClassNode())){
			implementClassDefinition(methodNode.getClassNode());
		}
		IImplementerExt methodImplementer = createMethodImplementer(methodNode);
		methodImplementer.implementContent();
	}

	@Override
	protected void implementParameterDefinition(AbstractParameterNode node) throws CoreException, EcException {
		implementParameterDefinition(node, null);
	}

	protected void implementParameterDefinition(AbstractParameterNode node, Set<String> fields) throws CoreException, EcException {
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
		//		IPackageFragment packageFragment = getPackageFragment(packageName);
		IPackageFragment packageFragment = 
				EclipsePackageFragmentGetter.getPackageFragment(packageName, fFileInfoProvider);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);
		unit.createType(enumDefinitionContent(node, fields), null, false, null);
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	@Override
	protected void implementChoiceDefinition(ChoiceNode node) throws CoreException, EcException {
		if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
			implementChoicesDefinitions(Arrays.asList(new ChoiceNode[]{node}));
		}
	}

	protected void implementChoicesDefinitions(List<ChoiceNode> nodes) throws CoreException, EcException {
		refreshWorkspace();
		AbstractParameterNode parent = getParameter(nodes);
		if(parent == null){
			return;
		}
		String typeName = parent.getType();
		if(parameterDefinitionImplemented(parent) == false){
			implementParameterDefinition(parent);
		}
		IType enumType = getJavaProject().findType(typeName);
		ICompilationUnit iUnit = enumType.getCompilationUnit();
		CompilationUnit unit = getCompilationUnit(enumType);

		addEnumItems(unit, typeName, nodes, enumType);

		enumType.getResource().refreshLocal(IResource.DEPTH_ONE, null);
		iUnit.becomeWorkingCopy(null);
		iUnit.commitWorkingCopy(true, null);
		refreshWorkspace();
	}

	@SuppressWarnings("unchecked")
	private void addEnumItems(
			CompilationUnit unit,
			String typeName, 
			List<ChoiceNode> nodes,
			IType enumType) throws CoreException {

		EnumDeclaration enumDeclaration = getEnumDeclaration(unit, typeName);
		if (enumDeclaration == null){
			return;
		}

		List<String> enumItemNames = new ArrayList<String>();

		for (ChoiceNode node : nodes) {
			EnumConstantDeclaration constant = unit.getAST().newEnumConstantDeclaration();
			String enumItemName = node.getValueString();

			if (enumItemNames.contains(enumItemName)) {
				continue;
			}
			constant.setName(unit.getAST().newSimpleName(enumItemName));
			enumDeclaration.enumConstants().add(constant);
			enumItemNames.add(enumItemName);
		}

		saveChanges(unit, enumType.getResource().getLocation());
	}

	@Override
	protected boolean implementable(ClassNode node) throws EcException{
		if(!androidCodeImplemented(node)) {
			return true;
		}
		if(classDefinitionImplemented(node)){
			return hasImplementableNode(node.getMethods());
		}
		return classDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(MethodNode node) throws EcException{
		ClassNode classNode = node.getClassNode();
		if(!androidCodeImplemented(classNode)) {
			return true;
		}		
		if(methodDefinitionImplemented(node)){
			return hasImplementableNode(node.getParameters()) || hasImplementableNode(node.getTestCases());
		}
		return methodDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(MethodParameterNode node){
		if(parameterDefinitionImplemented(node)){
			return hasImplementableNode(node.getChoices());
		}
		return parameterDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(GlobalParameterNode node){
		if(parameterDefinitionImplemented(node)){
			return hasImplementableNode(node.getChoices());
		}
		return parameterDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(ChoiceNode node){
		if(node.isAbstract()){
			return hasImplementableNode(node.getChoices());
		}
		if(parameterDefinitionImplemented(node.getParameter())){
			try{
				IType type = getJavaProject().findType(node.getParameter().getType());
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
			if(parameterDefinitionImplementable(node.getParameter()) == false){
				return false;
			}
		}

		return JavaUtils.isValidJavaIdentifier(node.getValueString());
	}

	@Override
	protected boolean androidCodeImplemented(ClassNode classNode) throws EcException {
		if (!classNode.getRunOnAndroid()) {
			return true;
		}
		if (!new EclipseProjectHelper(fFileInfoProvider).isAndroidProject()) {
			return true;
		}
		ImplementerExt implementer = createImplementer(classNode);
		return implementer.contentImplemented();
	}

	@Override
	protected boolean classDefinitionImplemented(ClassNode node) {
		try{
			IType type = getJavaProject().findType(node.getName());
			return (type != null) && type.isClass();
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	@Override
	protected boolean methodDefinitionImplemented(MethodNode methodNode) {
		IImplementerExt methodImplementer = createMethodImplementer(methodNode);
		return methodImplementer.contentImplemented();
	}

	@Override
	protected boolean parameterDefinitionImplemented(AbstractParameterNode node) {
		try{
			IType type = getJavaProject().findType(node.getType());
			return (type != null) && type.isEnum();
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private IImplementerExt createMethodImplementer(MethodNode methodNode) {
		final String className = JavaUtils.getQualifiedName(methodNode.getClassNode());

		IMethodImplementHelper fMethodImplementHelper = 
				new EclipseMethodImplementHelper(fFileInfoProvider, className, methodNode);

		if (methodNode.getRunOnAndroid()) {
			return AndroidMethodImplementerExt.createImplementer(methodNode, fMethodImplementHelper);
		} else {
			return new JavaMethodImplementer(methodNode, fMethodImplementHelper);
		}
	}

	protected String methodDefinitionContent(MethodNode node){
		String methodSignature = "public void " + node.getName() + "(" + getMethodArgs(node) +")"; 

		String methodBody =	
				" {\n"+ 
						"\t" + "// TODO Auto-generated method stub" + "\n" + 
						"\t" + createLoggingInstruction(node) + "\n"+ 
						"}";

		return methodSignature + methodBody;
	}

	private String createLoggingInstruction(MethodNode methodNode) {
		String result = "";

		if (methodNode.getRunOnAndroid()) {
			result = "android.util.Log.d(\"ecFeed\", \"" + methodNode.getName() + "(";
		} else {
			result = "System.out.println(\"" + methodNode.getName() + "(";
		}

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		if(parameters.size() == 0) {
			return result + ")\");";
		}

		result +=  "\" + ";
		for(int index = 0; index < parameters.size(); ++index) {
			result += parameters.get(index).getName();
			if(index != parameters.size() - 1) {
				result += " + \", \"";
			}
			result += " + ";
		}

		return result + "\")\");"; 
	}

	private String getMethodArgs(MethodNode node) {
		List<AbstractParameterNode> parameters = node.getParameters();

		if(parameters.size() == 0) {
			return new String();
		}

		String args = "";

		for(int i = 0; i < parameters.size(); ++i) {
			AbstractParameterNode param = parameters.get(i);
			args += JavaUtils.getLocalName(param.getType()) + " " + param.getName();
			if(i != parameters.size() - 1){
				args += ", ";
			}
		}
		return args;
	}

	protected String enumDefinitionContent(AbstractParameterNode node, Set<String> fields){
		String fieldsDefinition = "";
		if(fields != null && fields.size() > 0){
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
					if(model.getName().equals(node.getName()) && model.getParametersTypes().equals(node.getParametersTypes())){
						return hasImplementableNode(node.getChildren());
					}
				}
			}
			return true;
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private boolean classDefinitionImplementable(ClassNode node) {
		try{
			return getJavaProject().findType(node.getName()) == null;
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	private boolean parameterDefinitionImplementable(AbstractParameterNode parameter) {
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

	@SuppressWarnings("deprecation")
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

	private List<ChoiceNode> unimplementedChoices(List<ChoiceNode> choices){
		List<ChoiceNode> unimplemented = new ArrayList<>();
		for(ChoiceNode choice : choices){
			if(implementable(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				unimplemented.add(choice);
			}
		}
		return unimplemented;
	}

	private AbstractParameterNode getParameter(List<ChoiceNode> nodes) {
		if(nodes.size() == 0){
			return null;
		}
		AbstractParameterNode parameter = nodes.get(0).getParameter();
		for(ChoiceNode node : nodes){
			if(node.getParameter() != parameter){
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

	private ImplementerExt createImplementer(ClassNode classNode) {
		String baseRunner = classNode.getAndroidBaseRunner(); 

		IProjectHelper projectHelper = new EclipseProjectHelper(fFileInfoProvider);
		IClassImplementHelper classImplementHelper = new EclipseClassImplementHelper(fFileInfoProvider);
		IInstallationDirFileHelper installationDirFileHelper = new EclipseInstallationDirFileHelper(); 

		return new ImplementerExt(baseRunner, projectHelper, classImplementHelper, installationDirFileHelper); 
	}
}
