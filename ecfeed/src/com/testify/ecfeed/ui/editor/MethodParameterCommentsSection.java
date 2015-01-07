package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterCommentsSection extends AbstractParameterCommentsSection {

	private MethodParameterInterface fTargetIf;

	private Text fParameterJavadocText;
	private Text fTypeJavadocText;

	public MethodParameterCommentsSection(ISectionContext sectionContext,
			IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fParameterJavadocText = addTextTab("Parameter javadoc", false);
		fTypeJavadocText = addTextTab("Type javadoc", false);
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new MethodParameterInterface(getUpdateContext());
		}
		return fTargetIf;
	}

	@Override
	public void refresh(){
		super.refresh();
		fParameterJavadocText.setText(JavaDocAnalyser.getJavaDoc((MethodParameterNode)getTarget()));
		fTypeJavadocText.setText(JavaDocAnalyser.getTypeJavaDoc((AbstractParameterNode)getTarget()));
	}
}
