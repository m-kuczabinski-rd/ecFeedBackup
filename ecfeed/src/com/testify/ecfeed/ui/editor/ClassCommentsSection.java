package com.testify.ecfeed.ui.editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.editor.actions.NamedAction;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ClassCommentsSection extends JavaDocCommentsSection {

	private class ImportJavaDocAction extends NamedAction{

		public ImportJavaDocAction() {
			super("javadoc.import", "Import Javadoc");
			setToolTipText("Import element's comments from source's javadoc");
			setImageDescriptor(getIconDescription("implement.png"));
		}

		@Override
		public void run(){
			String comments = JavaDocAnalyser.removeJavadocFormating(JavaDocAnalyser.importJavadoc(getTarget()));
			if(comments != null){
				getTargetIf().setComments(comments);
				getTabFolder().setSelection(getTabFolder().indexOf(getCommentsItem()));
			}
		}
	}

	private class ExportJavaDocAction extends NamedAction{

		public ExportJavaDocAction() {
			super("javadoc.export", "Export Javadoc");
			setToolTipText("Export element's comments to source's javadoc");
			setImageDescriptor(getIconDescription("class_node.png"));
		}

		@Override
		public void run(){
			String comments = JavaDocAnalyser.removeJavadocFormating(JavaDocAnalyser.importJavadoc(getTarget()));
			if(comments != null){
				JavaDocAnalyser.exportJavadoc(getTarget());
				refresh();
				getTabFolder().setSelection(getTabFolder().indexOf(getJavaDocItem()));
			}
		}
	}

	public ClassCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
		getToolBarManager().add(new ImportJavaDocAction());
	}

	@Override
	public void refresh(){
		super.refresh();
		getJavaDocText().setText(JavaDocAnalyser.importJavadoc(getTarget()));
	}

	public void setInput(ClassNode input){
		super.setInput(input);
		refresh();
	}

	@Override
	protected List<Action> toolBarActions(){
		return Arrays.asList(new Action[]{new ImportJavaDocAction(), new ExportJavaDocAction()});
	}
}
