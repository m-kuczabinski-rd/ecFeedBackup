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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.TextEdit;

import com.testify.ecfeed.adapter.AbstractJavaModelImplementer;
import com.testify.ecfeed.adapter.CachedImplementationStatusResolver;
import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.utils.SystemLogger;

public class EclipseModelImplementer extends AbstractJavaModelImplementer {

	private final IFileInfoProvider fFileInfoProvider;

	public EclipseModelImplementer(IFileInfoProvider fileInfoProvider) {
		super(new EclipseImplementationStatusResolver(fileInfoProvider));
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public boolean implement(AbstractNode node){
		refreshWorkspace();
		boolean result = super.implement(node);
		CachedImplementationStatusResolver.clearCache(node);
		refreshWorkspace();
		return result;
	}

	@Override
	protected boolean implement(AbstractParameterNode node) throws CoreException{
		if(parameterDefinitionImplemented(node) == false){
			implementParameterDefinition(node, node.getLeafChoiceValues());
		}
		else{
			List<ChoiceNode> unimplemented = unimplementedChoices(node.getLeafChoices());
			implementChoicesDefinitions(unimplemented);
			for(ChoiceNode choice : unimplemented){
				CachedImplementationStatusResolver.clearCache(choice);
			}
		}
		return true;
	}

	@Override
	protected boolean implement(ChoiceNode node) throws CoreException{
		AbstractParameterNode parameter = node.getParameter();
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
				implementChoicesDefinitions(unimplementedChoices(node.getLeafChoices()));
			}
			else{
				if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
					implementChoicesDefinitions(Arrays.asList(new ChoiceNode[]{node}));
				}
			}
		}
		return true;
	}

	@Override
	protected void implementAndroidCode(ClassNode classNode) {
		try {
			(new EclipseEctImplementerForClassNode(fFileInfoProvider, classNode)).implementContent();
		} catch (Exception e) {
			MessageDialog.openError(
					Display.getDefault().getActiveShell(), 
					Messages.CAN_NOT_IMPLEMENT_ANDROID_CODE, 
					e.getMessage());
		}
	}

	@Override
	protected void implementClassDefinition(ClassNode node) throws CoreException {
		String packageName = JavaUtils.getPackageName(node.getName());
		String className = JavaUtils.getLocalName(node.getName());
		String unitName = className + ".java";
		IPackageFragment packageFragment = 
				EclipsePackageFragmentGetter.getPackageFragment(packageName, fFileInfoProvider);
		ICompilationUnit unit = packageFragment.getCompilationUnit(unitName);
		unit.createType(classDefinitionContent(node), null, false, null);
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	@Override
	protected void implementMethodDefinition(MethodNode node) throws CoreException {
		if(classDefinitionImplemented(node.getClassNode()) == false){
			implementClassDefinition(node.getClassNode());
		}
		IType classType = getJavaProject().findType(JavaUtils.getQualifiedName(node.getClassNode()));
		if(classType != null){
			classType.createMethod(methodDefinitionContent(node), null, false, null);
			for(AbstractParameterNode parameter : node.getParameters()){
				String type = parameter.getType();
				if(JavaUtils.isUserType(type)){
					String packageName = JavaUtils.getPackageName(type);
					if(packageName.equals(JavaUtils.getPackageName(node.getClassNode())) == false){
						classType.getCompilationUnit().createImport(type, null, null);
					}
				}
			}
		}
		ICompilationUnit unit = classType.getCompilationUnit();
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
	}

	@Override
	protected void implementParameterDefinition(AbstractParameterNode node) throws CoreException {
		implementParameterDefinition(node, null);
	}

	protected void implementParameterDefinition(AbstractParameterNode node, Set<String> fields) throws CoreException {
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
	protected void implementChoiceDefinition(ChoiceNode node) throws CoreException {
		if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
			implementChoicesDefinitions(Arrays.asList(new ChoiceNode[]{node}));
		}
	}

	@SuppressWarnings("unchecked")
	protected void implementChoicesDefinitions(List<ChoiceNode> nodes) throws CoreException {
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
		EnumDeclaration enumDeclaration = getEnumDeclaration(unit, typeName);
		if(enumDeclaration != null){
			for(ChoiceNode node : nodes){
				EnumConstantDeclaration constant = unit.getAST().newEnumConstantDeclaration();
				constant.setName(unit.getAST().newSimpleName(node.getValueString()));
				enumDeclaration.enumConstants().add(constant);
			}
			saveChanges(unit, enumType.getResource().getLocation());
		}
		enumType.getResource().refreshLocal(IResource.DEPTH_ONE, null);
		iUnit.becomeWorkingCopy(null);
		iUnit.commitWorkingCopy(true, null);
		refreshWorkspace();
	}

	@Override
	protected boolean implementable(ClassNode node){
		if(!androidCodeImplemented(node)) {
			return true;
		}
		if(classDefinitionImplemented(node)){
			return hasImplementableNode(node.getMethods());
		}
		return classDefinitionImplementable(node);
	}

	@Override
	protected boolean implementable(MethodNode node){
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
	protected boolean androidCodeImplemented(ClassNode classNode) {

		return (new EclipseEctImplementerForClassNode(fFileInfoProvider, classNode)).contentImplemented();
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
	protected boolean methodDefinitionImplemented(MethodNode node) {
		try{
			IType type = getJavaProject().findType(node.getClassNode().getName());
			if(type == null){
				return false;
			}
			EclipseModelBuilder builder = new EclipseModelBuilder();
			for(IMethod method : type.getMethods()){
				MethodNode model = builder.buildMethodModel(method);
				if(model != null && model.getName().equals(node.getName()) && model.getParametersTypes().equals(node.getParametersTypes())){
					return true;
				}
			}
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	@Override
	protected boolean parameterDefinitionImplemented(AbstractParameterNode node) {
		try{
			IType type = getJavaProject().findType(node.getType());
			return (type != null) && type.isEnum();
		}catch(CoreException e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	protected String classDefinitionContent(ClassNode node){
		return "public class " + JavaUtils.getLocalName(node) + "{\n\n}";
	}

	protected String methodDefinitionContent(MethodNode node){
		String args = "";
		String comment = "// TODO Auto-generated method stub";
		String content = "System.out.println(\"" + node.getName() + "(";

		if(node.getParameters().size() > 0){
			content +=  "\" + ";
			for(int i = 0; i < node.getParameters().size(); ++i){
				AbstractParameterNode parameter = node.getParameters().get(i);
				args += JavaUtils.getLocalName(parameter.getType()) + " " + parameter.getName();
				content += node.getParameters().get(i).getName();
				if(i != node.getParameters().size() - 1){
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
}
