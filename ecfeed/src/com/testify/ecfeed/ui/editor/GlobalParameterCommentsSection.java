package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.GlobalParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class GlobalParameterCommentsSection extends AbstractParameterCommentsSection {

	private GlobalParameterInterface fTargetIf;
	private TabItem fTypeJavadocTab;

	public GlobalParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
		fTypeJavadocTab = addTextTab("Type javadoc", false);
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new GlobalParameterInterface(getUpdateContext());
		}
		return fTargetIf;
	}

	@Override
	public void refresh(){
		super.refresh();
		fTypeJavadocTab.setText(JavaDocAnalyser.importTypeJavaDoc((AbstractParameterNode)getTarget()));
	}

}
