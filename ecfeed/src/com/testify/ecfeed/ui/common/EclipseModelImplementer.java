package com.testify.ecfeed.ui.common;

import java.util.AbstractMap;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.java.JavaModelImplementer;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class EclipseModelImplementer extends JavaModelImplementer {

	IProjectNameProvider fProjectNameProvider;
	
	public EclipseModelImplementer(IProjectNameProvider projectNameProvider) {
		super(new EclipseImplementationStatusResolver());
		fProjectNameProvider = projectNameProvider;
	}

	private class NodeImplementer implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			for(ClassNode classNode : node.getClasses()){
				if(implementable(classNode) && getImplementationStatus(classNode) != EImplementationStatus.IMPLEMENTED){
					implement(classNode);
				}
			}
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(classDefinitionImplemented(node) == false){
				implementClassDefinition(node);
			}
			for(MethodNode method : node.getMethods()){
				if(implementable(method) && getImplementationStatus(method) != EImplementationStatus.IMPLEMENTED){
					implement(method);
				}
			}
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(methodDefinitionImplemented(node) == false){
				implementMethodDefinition(node);
			}
			for(CategoryNode parameter : node.getCategories()){
				if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
					implement(parameter);
				}
			}
			return null;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			if(parameterDefinitionImplemented(node) == false){
				implementParameterDefinition(node);
			}
			for(PartitionNode partition : node.getPartitions()){
				if(implementable(partition) && getImplementationStatus(partition) != EImplementationStatus.IMPLEMENTED){
					implement(partition);
				}
			}
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			for(PartitionNode partition : node.getTestData()){
				if(implementable(partition) && getImplementationStatus(partition) != EImplementationStatus.IMPLEMENTED){
					implement(partition);
				}
			}
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			if(node.isAbstract()){
				for(PartitionNode partition : node.getPartitions()){
					if(implementable(partition) && getImplementationStatus(partition) != EImplementationStatus.IMPLEMENTED){
						implement(partition);
					}
				}
			}
			else{
				if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
					implementPartitionDefinition(node);
				}
			}
			return null;
		}
		
	}
	
	@Override
	public void implement(GenericNode node) {
		//TODO Unit tests. Need to be implemented as a separate eclipse plugin to have access to workspace resources
		try{
			refreshWorkspace();
			if(implementable(node) && getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED){
				node.accept(new NodeImplementer());
				refreshWorkspace();
			}
		}catch(Exception e){}
	}

	private void refreshWorkspace() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IWorkspaceRoot.DEPTH_INFINITE, null);
		} catch (CoreException e) {}
	}

	private void implementClassDefinition(ClassNode node) {
		String qualifiedName = JavaUtils.getQualifiedName(node);
		AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(qualifiedName);
		CompilationUnit testUnit = unitPair.getValue();
		if (testUnit != null) {
			implementClassDefinition(testUnit, qualifiedName, true);
			writeCompilationUnit(testUnit, unitPair.getKey(), 0);
		}
	}

	private boolean classDefinitionImplemented(ClassNode node) {
		return (getImplementationStatus(node) != EImplementationStatus.NOT_IMPLEMENTED);
	}

	private boolean methodDefinitionImplemented(MethodNode node) {
		return (getImplementationStatus(node) != EImplementationStatus.NOT_IMPLEMENTED);
	}

	private void implementMethodDefinition(MethodNode node) {
		AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(node.getClassNode().getQualifiedName());
		CompilationUnit classUnit = unitPair.getValue();
		TypeDeclaration type = null;
		if (classUnit != null) {
			type = (TypeDeclaration)getTypeInstance(classUnit, node.getClassNode().getQualifiedName(), true);
			int methods = type.getMethods().length;
			if (type != null) {
				implementMethodDefinition(classUnit, type, node);
			}
			writeCompilationUnit(classUnit, unitPair.getKey(), type.getMethods().length - methods);
		}
	}

	private boolean parameterDefinitionImplemented(CategoryNode node) {
		return (getImplementationStatus(node) != EImplementationStatus.NOT_IMPLEMENTED);
	}


	private void implementParameterDefinition(CategoryNode node) {
		String type = node.getType();
		if (implementable(node) && JavaUtils.isUserType(type)) {
			AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(type);
			CompilationUnit categoryUnit = unitPair.getValue();
			if (categoryUnit != null) {
				implementClassDefinition(categoryUnit, type, false);
				writeCompilationUnit(categoryUnit, unitPair.getKey(), 0);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void implementPartitionDefinition(PartitionNode node) {
		String type = node.getCategory().getType();
		String value = node.getValueString();
		
		if(JavaUtils.isUserType(type)){
			AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnitInstance(type);
			CompilationUnit categoryUnit = unitPair.getValue();
			EnumDeclaration categoryType = null;
			if (categoryUnit != null) {
				categoryType = (EnumDeclaration)getTypeInstance(categoryUnit, type, false);
				if (categoryType != null) {
					if (getImplementationStatus(node) != EImplementationStatus.IMPLEMENTED) {
						EnumConstantDeclaration constant = categoryUnit.getAST().newEnumConstantDeclaration();
						constant.setName(categoryUnit.getAST().newSimpleName(value));
						categoryType.enumConstants().add(constant);

					}
				}
				writeCompilationUnit(categoryUnit, unitPair.getKey(), 0);
			}
		}
	}
	
	private AbstractTypeDeclaration getTypeInstance(CompilationUnit unit, String classQualifiedName, boolean classType) {
		AbstractTypeDeclaration type = null;

		String className = JavaUtils.getLocalName(classQualifiedName);
		for (Object object : unit.types()) {
			AbstractTypeDeclaration declaration = (AbstractTypeDeclaration)object;
			if (declaration.getName().toString().equals(className)) {
				type = declaration;
				break;
			}
		}

		if (type == null) {
			type = implementClassDefinition(unit, classQualifiedName, classType);
		}

		return type;
	}

	private AbstractMap.SimpleEntry<IPath,CompilationUnit> getCompilationUnitInstance(String classQualifiedName) {
		AbstractMap.SimpleEntry<IPath,CompilationUnit> unitPair = getCompilationUnit(classQualifiedName);
		if ((unitPair.getKey() == null) || (unitPair.getValue() == null)) {
			unitPair = createCompilationUnit(fProjectNameProvider.getProjectName(), classQualifiedName);
		}
		return unitPair;
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

	@SuppressWarnings("unchecked")
	private AbstractTypeDeclaration implementClassDefinition(CompilationUnit unit, String classQualifiedName, boolean testClass) {
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

	@SuppressWarnings("unchecked")
	private void implementMethodDefinition(CompilationUnit unit, TypeDeclaration type, MethodNode method) {
		MethodDeclaration methodDeclaration = unit.getAST().newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		methodDeclaration.modifiers().add(unit.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		methodDeclaration.setName(unit.getAST().newSimpleName(method.getName()));
		methodDeclaration.setReturnType2(unit.getAST().newPrimitiveType(PrimitiveType.VOID));

		for(CategoryNode parameter : method.getCategories()){
			String parameterName = parameter.getName();
			String parameterType = parameter.getType();
			SingleVariableDeclaration variableDeclaration = unit.getAST().newSingleVariableDeclaration();
			if (JavaUtils.isPrimitive(parameterType) && JavaUtils.isString(parameterType) == false) {
				variableDeclaration.setType(getPrimitiveType(unit, parameterType));
			} else if(JavaUtils.isString(parameterType)){
				variableDeclaration.setType(unit.getAST().newSimpleType(unit.getAST().newSimpleName(JavaUtils.getLocalName(parameterType))));
			}else{
				variableDeclaration.setType(unit.getAST().newSimpleType(unit.getAST().newSimpleName(JavaUtils.getLocalName(parameterType))));
				if (!isTypeImported(unit, parameterType)) {
					String packageName = JavaUtils.getPackageName(parameterType);
					String simpleName = JavaUtils.getLocalName(parameterType);
					QualifiedName importName = unit.getAST().newQualifiedName(unit.getAST().newName(packageName), unit.getAST().newSimpleName(simpleName));
					ImportDeclaration importDeclaration = unit.getAST().newImportDeclaration();
					importDeclaration.setName(importName);
					unit.imports().add(importDeclaration);
				}
			}
			variableDeclaration.setName(unit.getAST().newSimpleName(parameterName));
			methodDeclaration.parameters().add(variableDeclaration);
		}

		methodDeclaration.setBody(implementMethodBody(unit, method));
		type.bodyDeclarations().add(methodDeclaration);
	}

	@SuppressWarnings("unchecked")
	private Block implementMethodBody(CompilationUnit unit, MethodNode method) {
		Block block = unit.getAST().newBlock();
		MethodInvocation printlnInvocation = unit.getAST().newMethodInvocation();
		QualifiedName name = unit.getAST().newQualifiedName(unit.getAST().newSimpleName("System"), unit.getAST().newSimpleName("out"));
		printlnInvocation.setExpression(name);
		printlnInvocation.setName(unit.getAST().newSimpleName("println"));

		Expression expression = null;
		if (method.getCategories().size() > 0) {
			StringLiteral literal = unit.getAST().newStringLiteral();
			literal.setLiteralValue(method.getName() + "(");
			expression = literal;
			for (int k = 0; k < method.getCategories().size(); ++k) {
				Expression argExpression = null;
				if (k < method.getCategories().size() - 1) {
					InfixExpression plusExpression = unit.getAST().newInfixExpression();
					plusExpression.setOperator(InfixExpression.Operator.PLUS);
					literal = unit.getAST().newStringLiteral();
					literal.setLiteralValue(", ");
					plusExpression.setLeftOperand(unit.getAST().newSimpleName(method.getCategories().get(k).getName()));
					plusExpression.setRightOperand(literal);
					argExpression = plusExpression;
				} else {
					argExpression = unit.getAST().newSimpleName(method.getCategories().get(k).getName());
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
			literal.setLiteralValue(method.getName() + "(");
			plusExpression.setLeftOperand(literal);
			literal = unit.getAST().newStringLiteral();
			literal.setLiteralValue(")");
			plusExpression.setRightOperand(literal);
			expression = plusExpression;
		}

		printlnInvocation.arguments().add(expression);
		ExpressionStatement expressionStatement = unit.getAST().newExpressionStatement(printlnInvocation);
		block.statements().add(expressionStatement);
		return block;
	}

	
	private boolean isTypeImported(CompilationUnit unit, String typeName) {
		boolean imported = false;
		for (Object element : unit.imports()) {
			ImportDeclaration type = (ImportDeclaration)element;
			if (typeName.equals(type.getName().getFullyQualifiedName())) {
				imported = true;
				break;
			}
		}
		return imported;
	}

	private Type getPrimitiveType(CompilationUnit unit, String typeName) {
		switch(typeName) {
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_BOOLEAN:
			return unit.getAST().newPrimitiveType(PrimitiveType.BOOLEAN);
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_BYTE:
			return unit.getAST().newPrimitiveType(PrimitiveType.BYTE);
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_CHAR:
			return unit.getAST().newPrimitiveType(PrimitiveType.CHAR);
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_DOUBLE:
			return unit.getAST().newPrimitiveType(PrimitiveType.DOUBLE);
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_FLOAT:
			return unit.getAST().newPrimitiveType(PrimitiveType.FLOAT);
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_INT:
			return unit.getAST().newPrimitiveType(PrimitiveType.INT);
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_LONG:
			return unit.getAST().newPrimitiveType(PrimitiveType.LONG);
		case com.testify.ecfeed.adapter.java.Constants.TYPE_NAME_SHORT:
			return unit.getAST().newPrimitiveType(PrimitiveType.SHORT);
		default:
			return null;
		}
	}

}
