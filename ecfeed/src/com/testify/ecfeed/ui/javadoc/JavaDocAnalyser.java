package com.testify.ecfeed.ui.javadoc;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;

public class JavaDocAnalyser {

	private final static String EMPTY_STRING = "";

	public static String getJavadoc(ClassNode node){
		return getJavadoc(JavaModelAnalyser.getIType(node.getName()));
	}

	public static String getJavaDoc(MethodNode node){
		return(getJavadoc(JavaModelAnalyser.getIMethod(node)));
	}

	public static String getJavaDoc(MethodParameterNode node){
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

	public static String getTypeJavaDoc(AbstractParameterNode node){
		if(JavaUtils.isUserType(node.getType())){
			return getJavadoc(JavaModelAnalyser.getIType(node.getType()));
		}
		return EMPTY_STRING;
	}

	public static String getJavaDoc(ChoiceNode node){
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
