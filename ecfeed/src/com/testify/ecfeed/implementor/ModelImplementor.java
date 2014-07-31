/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Mariusz Strozynski (m.strozynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.implementor;

import java.util.AbstractMap;
import java.util.ArrayList;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.Constants;
import com.testify.ecfeed.utils.ModelUtils;

public class ModelImplementor implements IModelImplementor {

	public void implement(ClassNode node) {
		AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(node.getRoot().getName(), node.getQualifiedName());
		CompilationUnit testUnit = unitPair.getValue();
		TypeDeclaration type = null;
		if (testUnit != null) {
			type = (TypeDeclaration)getTypeInstance(testUnit, node.getRoot().getName(), node.getQualifiedName(), ModelUtils.classDefinitionImplemented(node), true);
			int methods = type.getMethods().length;

			if ((type != null) && !ModelUtils.classMethodsImplemented(node)) {
				for (MethodNode method : node.getMethods()) {
					implementMethodNode(testUnit, type, method);
				}
			}
			writeCompilationUnit(testUnit, unitPair.getKey(), type.getMethods().length - methods);
		}
	}

	public void implement(MethodNode node) {
		AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(node.getRoot().getName(), node.getClassNode().getQualifiedName());
		CompilationUnit testUnit = unitPair.getValue();
		TypeDeclaration type = null;
		if (testUnit != null) {
			type = (TypeDeclaration)getTypeInstance(
					testUnit, node.getRoot().getName(), node.getClassNode().getQualifiedName(), ModelUtils.classDefinitionImplemented(node.getClassNode()), true);
			int methods = type.getMethods().length;

			if ((type != null) && !ModelUtils.methodDefinitionImplemented(node)) {
				implementMethodNode(testUnit, type, node);
			}
			writeCompilationUnit(testUnit, unitPair.getKey(), type.getMethods().length - methods);
		}
	}

	public void implement(CategoryNode node) {
		if (node.isExpected()) {
			implement(node.getDefaultValuePartition());
		} else {
			implementCategoryNode(node);
		}
	}

	public void implement(PartitionNode node) {
		if (!ModelUtils.isCategoryImplemented(node.getCategory()) && !ModelUtils.getJavaTypes().contains(node.getCategory().getShortType())) {
			AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(node.getRoot().getName(), node.getCategory().getType());
			CompilationUnit categoryUnit = unitPair.getValue();
			EnumDeclaration categoryType = null;
			if (categoryUnit != null) {
				categoryType = (EnumDeclaration)getTypeInstance(
						categoryUnit, node.getRoot().getName(), node.getCategory().getType(),
						ModelUtils.categoryDefinitionImplemented(node.getCategory()), false);
				if (categoryType != null) {
					if (!ModelUtils.isPartitionImplemented(node)) {
						implementPartitionNode(categoryUnit, categoryType, node);
					}
				}
				writeCompilationUnit(categoryUnit, unitPair.getKey(), 0);
			}
		}
	}

	private void implementMethodNode(CompilationUnit unit, TypeDeclaration type, MethodNode node) {
		if (!ModelUtils.methodDefinitionImplemented(node)) {
			implementMethodDefinition(unit, type, node.getName(), node.getCategoriesNames(), node.getCategoriesTypes());
		}
		if (!ModelUtils.methodCategoriesImplemented(node)) {
			for (CategoryNode category : node.getCategories()) {
				implementCategoryNode(category);
			}
		}
	}

	private void implementCategoryNode(CategoryNode node) {
		if (!ModelUtils.isCategoryImplemented(node) && !ModelUtils.getJavaTypes().contains(node.getShortType())) {
			AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(node.getRoot().getName(), node.getType());
			CompilationUnit categoryUnit = unitPair.getValue();
			EnumDeclaration categoryType = null;
			if (categoryUnit != null) {
				categoryType = (EnumDeclaration)getTypeInstance(
						categoryUnit, node.getRoot().getName(), node.getType(),
						ModelUtils.categoryDefinitionImplemented(node), false);
				if (categoryType != null) {
					for (PartitionNode partition : node.getPartitions()) {
						if (!ModelUtils.isPartitionImplemented(partition)) {
							implementPartitionNode(categoryUnit, categoryType, partition);
						}
					}
				}
				writeCompilationUnit(categoryUnit, unitPair.getKey(), 0);
			}
		}
	}

	private void implementPartitionNode(CompilationUnit unit, EnumDeclaration categoryType, PartitionNode node) {
		if (node.isAbstract()) {
			for (PartitionNode child : node.getPartitions()) {
				if (!ModelUtils.isPartitionImplemented(child)) {
					implementPartitionNode(unit, categoryType, child);
				}
			}
		} else {
			implementEnumConstant(unit, categoryType, node.getValueString());
		}
	}

	private AbstractMap.SimpleEntry<IPath,CompilationUnit> getCompilationUnit(String classQualifiedName) {
		CompilationUnit unit = null;
		IPath unitPath = null;
		try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)){
					IJavaProject javaProject = JavaCore.create(project);
					IType iType = javaProject.findType(classQualifiedName);
					if (iType != null) {
						final ASTParser parser = ASTParser.newParser(AST.JLS4);
						parser.setKind(ASTParser.K_COMPILATION_UNIT);
						parser.setSource(iType.getCompilationUnit());
						unit = (CompilationUnit) parser.createAST(null);
						ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
						IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
						path = path.append(unit.getJavaElement().getPath());
						bufferManager.connect(path, LocationKind.LOCATION, null);
						unit.recordModifications();
						unitPath = path;
						break;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new AbstractMap.SimpleEntry<IPath,CompilationUnit>(unitPath, unit);
	}

	private AbstractMap.SimpleEntry<IPath,CompilationUnit> createCompilationUnit(String projectName, String classQualifiedName) {
		CompilationUnit unit = null;
		IPath unitPath = null;
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IJavaProject javaProject = JavaCore.create(project);
		try {
			for (IPackageFragmentRoot packageFragmentRoot: javaProject.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
					IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
					path = path.append(packageFragmentRoot.getPath());
					String[] data = classQualifiedName.split("\\.");
					for (int i = 0; i < data.length; ++i) {
						String element = data[i];
						if (i == data.length - 1) {
							element += ".java";
						}
						path = path.append(element);
					}
					ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
					bufferManager.connect(path, LocationKind.LOCATION, null);
					ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.LOCATION);
					IDocument document = textFileBuffer.getDocument();
					final ASTParser parser = ASTParser.newParser(AST.JLS4);
					parser.setKind(ASTParser.K_COMPILATION_UNIT);
					parser.setSource(document.get().toCharArray());
					unit = (CompilationUnit) parser.createAST(null);
					unit.recordModifications();
					unitPath = path;
					break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new AbstractMap.SimpleEntry<IPath,CompilationUnit>(unitPath, unit);
	}

	private AbstractMap.SimpleEntry<IPath,CompilationUnit> getCompilationUnitInstance(String modelName, String classQualifiedName) {
		AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnit(classQualifiedName);
		if ((unitPair.getKey() == null) || (unitPair.getValue() == null)) {
			unitPair = createCompilationUnit(getProjectName(modelName), classQualifiedName);
		}
		return unitPair;
	}

	private void writeCompilationUnit(CompilationUnit unit, IPath compilationUnitPath, int addedMethods) {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(compilationUnitPath, LocationKind.LOCATION);
		IDocument document = textFileBuffer.getDocument();
		TextEdit edits = unit.rewrite(document, null);
		try {
			edits.apply(document);
			applyComments(document, addedMethods);
			textFileBuffer.commit(null, false);
			bufferManager.disconnect(compilationUnitPath, LocationKind.LOCATION, null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void applyComments(IDocument document, int addedMethods) {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(document.get().toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		ASTRewrite rewriter = ASTRewrite.create(unit.getAST());
		try {
			TypeDeclaration typeDecl = (TypeDeclaration)unit.types().get(0);
			MethodDeclaration [] methods = typeDecl.getMethods();
			for (int i = methods.length - addedMethods; i < methods.length; ++i) {
				MethodDeclaration methodDecl = methods[i];
				Block block = methodDecl.getBody();
				ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
				Statement placeHolder = (Statement) rewriter.createStringPlaceholder("// TODO Auto-generated method stub", ASTNode.EMPTY_STATEMENT);
				listRewrite.insertFirst(placeHolder, null);
			}
			TextEdit comments = rewriter.rewriteAST(document, null);
			comments.apply(document);
		} catch (Throwable e) {
		}
	}

	private String getProjectName(String modelName) {
		String projectName = null;
		try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
					if (project.exists(new Path(modelName + ".ect"))) {
						projectName = project.getName();
						break;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return projectName;
	}

	@SuppressWarnings("unchecked")
	private AbstractTypeDeclaration implementClassDefinition(CompilationUnit unit, String modelName, String classQualifiedName, boolean testClass) {
		String packageName = classQualifiedName.substring(0, classQualifiedName.lastIndexOf("."));
		PackageDeclaration packageDeclaration = unit.getAST().newPackageDeclaration();
		packageDeclaration.setName(unit.getAST().newName(packageName));
		unit.setPackage(packageDeclaration);
		AbstractTypeDeclaration type = null;
		if (testClass) {
			type = unit.getAST().newTypeDeclaration();
			TypeDeclaration typeDeclaration = (TypeDeclaration)type;
			typeDeclaration.setInterface(false);
		} else {
			type = unit.getAST().newEnumDeclaration();
		}
		type.modifiers().add(unit.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		String className = classQualifiedName.substring(classQualifiedName.lastIndexOf(".") + 1);
		type.setName(unit.getAST().newSimpleName(className));
		unit.types().add(type);
		return type;
	}

	private AbstractTypeDeclaration getTypeInstance(CompilationUnit unit, String modelName, String classQualifiedName, boolean implemented, boolean classType) {
		AbstractTypeDeclaration type = null;
		if (!implemented) {
			type = implementClassDefinition(unit, modelName, classQualifiedName, classType);
		} else {
			String className = classQualifiedName.substring(classQualifiedName.lastIndexOf(".") + 1);
			for (Object object : unit.types()) {
				AbstractTypeDeclaration declaration = (AbstractTypeDeclaration)object;
				if (declaration.getName().toString().equals(className)) {
					type = declaration;
					break;
				}
			}
		}
		return type;
	}

	@SuppressWarnings("unchecked")
	private void implementMethodDefinition(CompilationUnit unit, TypeDeclaration type, String methodName, ArrayList<String> parameters, ArrayList<String> types) {
		MethodDeclaration methodDeclaration = unit.getAST().newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		methodDeclaration.modifiers().add(unit.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		methodDeclaration.setName(unit.getAST().newSimpleName(methodName));
		methodDeclaration.setReturnType2(unit.getAST().newPrimitiveType(PrimitiveType.VOID));

		for (int i = 0; i < parameters.size(); ++i) {
			SingleVariableDeclaration variableDeclaration = unit.getAST().newSingleVariableDeclaration();
			if (ModelUtils.getJavaTypes().contains(types.get(i)) && !types.get(i).equals(Constants.TYPE_NAME_STRING)) {
				variableDeclaration.setType(getPrimitiveType(unit, types.get(i)));
			} else {
				String shortType = types.get(i);
				int lastindex = shortType.lastIndexOf(".");
				if(!(lastindex == -1 || lastindex >= shortType.length())){
					shortType = shortType.substring(lastindex + 1);
				}
				variableDeclaration.setType(unit.getAST().newSimpleType(unit.getAST().newSimpleName(shortType)));
				if (!types.get(i).equals(Constants.TYPE_NAME_STRING)) {
					ImportDeclaration importDeclaration = unit.getAST().newImportDeclaration();
					String[] data = types.get(i).split("\\.");
					QualifiedName importName = unit.getAST().newQualifiedName(unit.getAST().newSimpleName(data[0]), unit.getAST().newSimpleName(data[1]));
					for (int j = 2; j < data.length; ++j) {
						importName.setQualifier(unit.getAST().newName(importName.getFullyQualifiedName()));
						importName.setName(unit.getAST().newSimpleName(data[j]));
					}
					importDeclaration.setName(importName);
					unit.imports().add(importDeclaration);
				}
			}
			variableDeclaration.setName(unit.getAST().newSimpleName(parameters.get(i)));
			methodDeclaration.parameters().add(variableDeclaration);
		}

		methodDeclaration.setBody(implementMethodBody(unit, methodName, parameters));
		type.bodyDeclarations().add(methodDeclaration);
	}

	@SuppressWarnings("unchecked")
	private Block implementMethodBody(CompilationUnit unit, String methodName, ArrayList<String> parameters) {
		Block block = unit.getAST().newBlock();
		MethodInvocation methodInvocation = unit.getAST().newMethodInvocation();
		QualifiedName name = unit.getAST().newQualifiedName(unit.getAST().newSimpleName("System"), unit.getAST().newSimpleName("out"));
		methodInvocation.setExpression(name);
		methodInvocation.setName(unit.getAST().newSimpleName("println"));

		Expression expression = null;
		if (parameters.size() > 0) {
			StringLiteral literal = unit.getAST().newStringLiteral();
			literal.setLiteralValue(methodName + "(");
			expression = literal;
			for (int k = 0; k < parameters.size(); ++k) {
				Expression argExpression = null;
				if (k < parameters.size() - 1) {
					InfixExpression plusExpression = unit.getAST().newInfixExpression();
					plusExpression.setOperator(InfixExpression.Operator.PLUS);
					literal = unit.getAST().newStringLiteral();
					literal.setLiteralValue(", ");
					plusExpression.setLeftOperand(unit.getAST().newSimpleName(parameters.get(k)));
					plusExpression.setRightOperand(literal);
					argExpression = plusExpression;
				} else {
					argExpression = unit.getAST().newSimpleName(parameters.get(k));
				}

				InfixExpression newExpression = unit.getAST().newInfixExpression();
				newExpression.setOperator(InfixExpression.Operator.PLUS);
				newExpression.setLeftOperand(expression);
				newExpression.setRightOperand(argExpression);
				expression = newExpression;
			}
			literal = unit.getAST().newStringLiteral();
			literal.setLiteralValue(")");
			InfixExpression newExpression = unit.getAST().newInfixExpression();
			newExpression.setOperator(InfixExpression.Operator.PLUS);
			newExpression.setLeftOperand(expression);
			newExpression.setRightOperand(literal);
			expression = newExpression;
		} else {
			InfixExpression plusExpression = unit.getAST().newInfixExpression();
			plusExpression.setOperator(InfixExpression.Operator.PLUS);
			StringLiteral literal = unit.getAST().newStringLiteral();
			literal.setLiteralValue(methodName + "(");
			plusExpression.setLeftOperand(literal);
			literal = unit.getAST().newStringLiteral();
			literal.setLiteralValue(")");
			plusExpression.setRightOperand(literal);
			expression = plusExpression;
		}

		methodInvocation.arguments().add(expression);
		ExpressionStatement expressionStatement = unit.getAST().newExpressionStatement(methodInvocation);
		block.statements().add(expressionStatement);
		return block;
	}

	@SuppressWarnings("unchecked")
	private void implementEnumConstant(CompilationUnit unit, EnumDeclaration type, String constantName) {
		boolean add = true;
		for (Object object : type.enumConstants()) {
			EnumConstantDeclaration declaration = (EnumConstantDeclaration)object;
			if (declaration.getName().toString().equals(constantName)) {
				add = false;
				break;
			}
		}
		if (add) {
			EnumConstantDeclaration constant = unit.getAST().newEnumConstantDeclaration();
			constant.setName(unit.getAST().newSimpleName(constantName));
			type.enumConstants().add(constant);
		}
	}

	private Type getPrimitiveType(CompilationUnit unit, String typeName) {
		switch(typeName) {
		case Constants.TYPE_NAME_BOOLEAN:
			return unit.getAST().newPrimitiveType(PrimitiveType.BOOLEAN);
		case Constants.TYPE_NAME_BYTE:
			return unit.getAST().newPrimitiveType(PrimitiveType.BYTE);
		case Constants.TYPE_NAME_CHAR:
			return unit.getAST().newPrimitiveType(PrimitiveType.CHAR);
		case Constants.TYPE_NAME_DOUBLE:
			return unit.getAST().newPrimitiveType(PrimitiveType.DOUBLE);
		case Constants.TYPE_NAME_FLOAT:
			return unit.getAST().newPrimitiveType(PrimitiveType.FLOAT);
		case Constants.TYPE_NAME_INT:
			return unit.getAST().newPrimitiveType(PrimitiveType.INT);
		case Constants.TYPE_NAME_LONG:
			return unit.getAST().newPrimitiveType(PrimitiveType.LONG);
		case Constants.TYPE_NAME_SHORT:
			return unit.getAST().newPrimitiveType(PrimitiveType.SHORT);
		default:
			return null;
		}
	}
}
