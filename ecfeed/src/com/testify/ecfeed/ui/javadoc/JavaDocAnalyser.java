package com.testify.ecfeed.ui.javadoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;

public class JavaDocAnalyser {

	private final static String EMPTY_STRING = "";
	private final static String FIRST_COMMENT_LINE_REGEX = "\\s*/\\*\\*";
	private final static String LAST_COMMENT_LINE_REGEX = "\\s*\\*/";
	private final static String COMMENT_LINE_PREFIX_REGEX = "\\s*\\*\\s?";
	private final static String FIRST_COMMENT_LINE = "/**\n";
	private final static String LAST_COMMENT_LINE = "*/";
	private final static String COMMENT_LINE_PREXIF = "* ";

	private static class JavadocFetcher implements IModelVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			String methodDoc = getJavadoc(JavaModelAnalyser.getIMethod(node.getMethod()));

			String searchedTag = "@param " + node.getName();
			int startIndex = methodDoc.indexOf(searchedTag);
			int endIndex = methodDoc.indexOf("@", startIndex + 1);
			if(endIndex == -1){
				endIndex = methodDoc.length() - 1;
			}

			while(methodDoc.charAt(startIndex) != '\n' && methodDoc.charAt(startIndex) != '*'){
				--startIndex;
			}
			while(methodDoc.charAt(endIndex) != '\n'){
				--endIndex;
			}
			methodDoc = methodDoc.substring(startIndex, endIndex);

			return methodDoc;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return getJavadoc(JavaModelAnalyser.getIType(node.getName()));
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return getJavadoc(JavaModelAnalyser.getIMethod(node));
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			try{
				if(node.isAbstract() == false && JavaUtils.isUserType(node.getParameter().getType())){
					IType type = JavaModelAnalyser.getIType(node.getParameter().getType());
					for(IField field : type.getFields()){
						if(field.isEnumConstant() && field.getElementName().equals(node.getValueString())){
							return getJavadoc(field);
						}
					}
				}
			}catch(JavaModelException e){}
			return EMPTY_STRING;
		}

	}

	private static class JavadocImporter implements IModelVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			String methodDoc = getJavadoc(JavaModelAnalyser.getIMethod(node.getMethod()));

			String searchedTag = "@param " + node.getName();
			int startIndex = methodDoc.indexOf(searchedTag);
			int endIndex = methodDoc.indexOf("@", startIndex + 1);
			if(endIndex == -1){
				endIndex = methodDoc.length() - 1;
			}

			while(methodDoc.charAt(startIndex) != '\n' && methodDoc.charAt(startIndex) != '*'){
				--startIndex;
			}
			while(methodDoc.charAt(endIndex) != '\n'){
				--endIndex;
			}
			methodDoc = methodDoc.substring(startIndex, endIndex);

			return methodDoc;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return removeTrailingWhitespaces(removeJavadocFormating(getJavadoc(node)));
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			String javadoc = getJavadoc(JavaModelAnalyser.getIMethod(node));
			javadoc = removeJavadocFormating(javadoc);
			javadoc = getDescriptionBlock(javadoc);
			javadoc = removeTrailingWhitespaces(javadoc);
			return javadoc;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			String javadoc = getJavadoc(node);
			if(javadoc != null){
				javadoc = removeJavadocFormating(javadoc);
				javadoc = removeTrailingWhitespaces(javadoc);
				return javadoc;
			}
			return EMPTY_STRING;
		}

	}

	private static class JavadocExporter implements IModelVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			IType type = JavaModelAnalyser.getIType(node.getName());
			ISourceRange currentJavaDocRange = type.getJavadocRange();
			TextEdit edit = null;
			if(currentJavaDocRange != null){
				String javadoc = addJavadocFormatting(node.getDescription());
				edit = new ReplaceEdit(currentJavaDocRange.getOffset(), currentJavaDocRange.getLength(), javadoc);
			}
			else if(type.getSourceRange().getOffset() >= 0){
				String indent = getIndent(type);
				String javadoc = addJavadocFormatting(node.getDescription(), indent);
				edit = new InsertEdit(type.getSourceRange().getOffset(), javadoc + "\n" + indent);
			}
			if(edit != null){
				type.getCompilationUnit().applyTextEdit(edit, null);
				type.getCompilationUnit().save(null, false);
			}
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return null;
		}

		private String getIndent(IType type) {
			try{
				ISourceRange range = type.getSourceRange();
				String source = type.getCompilationUnit().getSource();
				int begin = range.getOffset();
				while(begin >= 0 && source.charAt(begin) != '\n'){
					--begin;
				}
				return source.substring(begin + 1, range.getOffset());
			}catch(JavaModelException e){}
			return null;
		}
	}

	public static String getTypeJavadoc(AbstractParameterNode node){
		if(JavaUtils.isUserType(node.getType())){
			return getJavadoc(JavaModelAnalyser.getIType(node.getType()));
		}
		return EMPTY_STRING;
	}

	public static String addJavadocFormatting(String input){
		return addJavadocFormatting(input, EMPTY_STRING);
	}

	public static String addJavadocFormatting(String input, String indent){
		BufferedReader reader = new BufferedReader(new StringReader(input));
		try{
			String output = FIRST_COMMENT_LINE;
			String line;
			while((line = reader.readLine()) != null){
				output += indent + COMMENT_LINE_PREXIF + line + "\n";
			}
			output += indent + LAST_COMMENT_LINE;
			return output;
		}catch(IOException e){}
		return null;
	}

	public static String getJavadoc(AbstractNode node){
		try{
			return (String)node.accept(new JavadocFetcher());
		}catch (Exception e){}
		return null;
	}

	public static String importJavadoc(AbstractNode node) {
		try{
			return (String)node.accept(new JavadocImporter());
		}catch (Exception e){}
		return null;
	}

	public static String importTypeJavadoc(AbstractParameterNode node) {
		String javadoc = getTypeJavadoc(node);
		javadoc = removeJavadocFormating(javadoc);
		javadoc = removeTrailingWhitespaces(javadoc);
		return javadoc;
	}

	public static void exportJavadoc(AbstractNode node){
		try{
			node.accept(new JavadocExporter());
		}catch(Exception e){}
	}

	private static String removeJavadocFormating(String input){
		BufferedReader reader = new BufferedReader(new StringReader(input));
		try{
			String output = "";
			String line;
			while((line = reader.readLine()) != null){
				if(line.matches(FIRST_COMMENT_LINE_REGEX) == false && line.matches(LAST_COMMENT_LINE_REGEX) == false){
					line = line.replaceAll(COMMENT_LINE_PREFIX_REGEX, "");
					output+= line + "\n";
				}
			}
			return output;
		}catch(IOException e){}
		return input;
	}

	private static String getDescriptionBlock(String input){
		input = removeJavadocFormating(input);
		BufferedReader reader = new BufferedReader(new StringReader(input));
		try{
			String output = "";
			String line;
			while((line = reader.readLine()) != null){
				if(line.matches("\\s*@.*")){
					break;
				}
				output+= line + "\n";
			}
			return output;
		}catch(IOException e){}
		return input;
	}

	private static String removeTrailingWhitespaces(String input){
		return input.replaceAll("\\s*\\z", EMPTY_STRING);
	}

	private static String getJavadoc(IMember member){
		if(member != null){
			try {
				ICompilationUnit unit = member.getCompilationUnit();
				ISourceRange range = member.getJavadocRange();
				if(unit != null && range != null){
					String raw = unit.getSource().substring(range.getOffset(), range.getOffset() + range.getLength());
					String trimmed = raw.replaceAll("\\n\\s*\\*", EMPTY_STRING + "\n*");
					return trimmed;
				}
			} catch (JavaModelException e) {}
		}
		return EMPTY_STRING;
	}
}
