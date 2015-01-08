package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterCommentsSection extends AbstractParameterCommentsSection {

	private MethodParameterInterface fTargetIf;

	private TabItem fParameterJavadocTab;
	private TabItem fTypeJavadocTab;

	public MethodParameterCommentsSection(ISectionContext sectionContext,
			IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fParameterJavadocTab = addTextTab("Parameter javadoc", false);
		fTypeJavadocTab = addTextTab("Type javadoc", false);
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
		getTextFromTabItem(fParameterJavadocTab).setText(JavaDocAnalyser.importJavadoc(getTarget()));
		getTextFromTabItem(fTypeJavadocTab).setText(JavaDocAnalyser.importTypeJavaDoc((AbstractParameterNode)getTarget()));
	}
}
