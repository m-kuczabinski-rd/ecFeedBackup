package com.testify.ecfeed.ui.editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceCommentsSection extends JavaDocCommentsSection {

	public ChoiceCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected List<Action> toolBarActions(){
		return Arrays.asList(new Action[]{new ImportJavaDocAction(), new ExportJavaDocAction()});
	}
}
