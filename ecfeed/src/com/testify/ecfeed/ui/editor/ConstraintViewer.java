package com.testify.ecfeed.ui.editor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.IRelationalStatement;
import com.testify.ecfeed.model.IStatementVisitor;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.ChoicesParentStatement.ICondition;
import com.testify.ecfeed.model.ChoicesParentStatement.LabelCondition;
import com.testify.ecfeed.model.ChoicesParentStatement.ChoiceCondition;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.ui.editor.actions.ModelModifyingAction;
import com.testify.ecfeed.ui.modelif.AbstractStatementInterface;
import com.testify.ecfeed.ui.modelif.ParameterInterface;
import com.testify.ecfeed.ui.modelif.ConstraintInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.StatementInterfaceFactory;

public class ConstraintViewer extends TreeViewerSection {

	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private Button fAddStatementButton;
	private Button fRemoveStatementButton;

	private Constraint fCurrentConstraint;
	private AbstractStatementInterface fStatementIf;
	private AbstractStatement fSelectedStatement;

	private StatementEditor fStatementEditor;

	private class StatementViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider {
		public final Object[] EMPTY_ARRAY = {};

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof Constraint){
				Constraint constraint = (Constraint)inputElement;
				return new Object[]{constraint.getPremise(), constraint.getConsequence()};
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof AbstractStatement){
				return ((AbstractStatement)parentElement).getChildren().toArray();
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element) {
			if(element instanceof AbstractStatement){
				return ((AbstractStatement)element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof StatementArray){
				StatementArray statementArray = (StatementArray)element;
				List<AbstractStatement> children = statementArray.getChildren();
				return (children.size() > 0);
			}
			return false;
		}
	}

	private class StatementViewerLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element){
			if(element instanceof StatementArray){
				return ((StatementArray)element).getOperator().toString();
			}
			else if(element instanceof AbstractStatement){
				return ((AbstractStatement)element).toString();
			}
			return null;
		}

		@Override
		public Image getImage(Object element){
			if(fCurrentConstraint != null){
				if(element == fCurrentConstraint.getPremise()){
					return getImage("premise_statement.gif");
				}
				else if(element == fCurrentConstraint.getConsequence()){
					return getImage("consequence_statement.gif");
				}
			}
			return null;
		}

		private Image getImage(String file) {
		    Bundle bundle = FrameworkUtil.getBundle(StatementViewerLabelProvider.class);
		    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		    ImageDescriptor image = ImageDescriptor.createFromURL(url);
		    return image.createImage();
		  }

	}

	private class StatementEditor extends Composite{

		private final String STATEMENT_FALSE = new StaticStatement(false).getLeftOperandName();
		private final String STATEMENT_TRUE = new StaticStatement(true).getLeftOperandName();
		private final String STATEMENT_AND = new StatementArray(EStatementOperator.AND).getLeftOperandName();
		private final String STATEMENT_OR = new StatementArray(EStatementOperator.OR).getLeftOperandName();
		private final String[] FIXED_STATEMENTS = {STATEMENT_FALSE, STATEMENT_TRUE, STATEMENT_OR, STATEMENT_AND};

		private final int STATEMENT_COMBO_WIDTH = 3;
		private final int RELATION_COMBO_WIDTH = 1;
		private final int CONDITION_COMBO_WIDTH = 4;
		private final int TOTAL_EDITOR_WIDTH = STATEMENT_COMBO_WIDTH + RELATION_COMBO_WIDTH + CONDITION_COMBO_WIDTH;

		private Combo fStatementCombo;
		private Combo fRelationCombo;
		private Combo fConditionCombo;
		private Composite fRightOperandComposite;

		private ConstraintNode fConstraint;
		private ConstraintInterface fConstraintIf;

		private class AvailableConditionsProvider implements IStatementVisitor{

			@Override
			public Object visit(StaticStatement statement) throws Exception {
				return new String[]{};
			}

			@Override
			public Object visit(StatementArray statement) throws Exception {
				return new String[]{};
			}

			@Override
			public Object visit(ExpectedValueStatement statement)
					throws Exception {
				MethodParameterNode parameter  = statement.getParameter();
				List<String> values = ParameterInterface.getSpecialValues(parameter.getType());
				if(values.isEmpty()){
					for(ChoiceNode p : parameter.getLeafChoices()){
						values.add(p.getValueString());
					}
				}
				return values.toArray(new String[]{});
			}

			@Override
			public Object visit(ChoicesParentStatement statement)
					throws Exception {
				List<String> result = new ArrayList<String>();
				MethodParameterNode parameter = statement.getParameter();

				Set<ChoiceNode> allChoices = parameter.getAllChoices();
				Set<String> allLabels = parameter.getLeafLabels();
				for(ChoiceNode choice : allChoices){
					ICondition condition = new ChoicesParentStatement(parameter, EStatementRelation.EQUAL, choice).getCondition();
					result.add(condition.toString());
				}
				for(String label : allLabels){
					ICondition condition = new ChoicesParentStatement(parameter, EStatementRelation.EQUAL, label).getCondition();
					result.add(condition.toString());
				}
				return result.toArray(new String[]{});
			}

			@Override
			public Object visit(LabelCondition condition) throws Exception {
				return new String[]{};
			}

			@Override
			public Object visit(ChoiceCondition condition) throws Exception {
				return new String[]{};
			}
		}

		private class CurrentConditionProvider implements IStatementVisitor{

			@Override
			public Object visit(StaticStatement statement) throws Exception {
				return "";
			}

			@Override
			public Object visit(StatementArray statement) throws Exception {
				return "";
			}

			@Override
			public Object visit(ExpectedValueStatement statement)
					throws Exception {
				return statement.getCondition().getValueString();
			}

			@Override
			public Object visit(ChoicesParentStatement statement)
					throws Exception {
				return statement.getCondition().toString();
			}

			@Override
			public Object visit(LabelCondition condition) throws Exception {
				return "";
			}

			@Override
			public Object visit(ChoiceCondition condition) throws Exception {
				return "";
			}

		}

		private class ConditionComboListener implements SelectionListener{

			@Override
			public void widgetSelected(SelectionEvent e){
				applyNewValue();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				applyNewValue();
			}

			protected void applyNewValue(){
				fStatementIf.setConditionValue(fConditionCombo.getText());
				fConditionCombo.setText(fStatementIf.getConditionValue());
			}

		}

		private class StatementComboListener implements SelectionListener{
			@Override
			public void widgetSelected(SelectionEvent e) {
				EStatementOperator operator = EStatementOperator.getOperator(fStatementCombo.getText());
				if(fStatementIf.getOperator() != null && operator != null){
					if(operator != fStatementIf.getOperator()){
						fStatementIf.setOperator(operator);
					}
				}
				else{
					AbstractStatement statement = buildStatement();
					if(statement != null){
						AbstractStatementInterface parentIf = fStatementIf.getParentInterface();
						boolean result = false;
						if(parentIf != null){
							result = parentIf.replaceChild(fSelectedStatement, statement);
						}
						else{
							result = fConstraintIf.replaceStatement(fSelectedStatement, statement);
						}
						if(result){
							getViewer().setSelection(new StructuredSelection(statement));

						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			private AbstractStatement buildStatement() {
				String statementText = fStatementCombo.getText();
				if(statementText.equals(STATEMENT_TRUE) || statementText.equals(STATEMENT_FALSE)){
					return new StaticStatement(Boolean.parseBoolean(statementText));
				}
				if(statementText.equals(STATEMENT_AND) || statementText.equals(STATEMENT_OR)){
					return new StatementArray(EStatementOperator.getOperator(statementText));
				}
				MethodParameterNode parameter = fConstraint.getMethod().getParameter(statementText);
				EStatementRelation relation = EStatementRelation.EQUAL;
				if(parameter != null && parameter.isExpected()){
					ChoiceNode condition = new ChoiceNode("expected", parameter.getDefaultValue());
					condition.setParent(parameter);
					return new ExpectedValueStatement(parameter, condition);
				}
				else if(parameter != null && parameter.getChoices().size() > 0){
					ChoiceNode condition = parameter.getChoices().get(0);
					return new ChoicesParentStatement(parameter, relation, condition);
				}

				return null;
			}
		}

		private class RelationComboListener implements SelectionListener{

			@Override
			public void widgetSelected(SelectionEvent e) {
				EStatementRelation relation = EStatementRelation.getRelation(fRelationCombo.getText());
				fStatementIf.setRelation(relation);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		}

		private class EditorBuilder implements IStatementVisitor{
			@Override
			public Object visit(StaticStatement statement) throws Exception {
				fRelationCombo.setVisible(false);
				fRightOperandComposite.setVisible(false);
				StatementEditor.this.redraw();
				return null;
			}

			@Override
			public Object visit(StatementArray statement) throws Exception {
				fRelationCombo.setVisible(false);
				fRightOperandComposite.setVisible(false);
				StatementEditor.this.redraw();
				return null;
			}

			@Override
			public Object visit(ExpectedValueStatement statement) throws Exception {
				disposeRightOperandComposite();
				if(ParameterInterface.hasLimitedValuesSet(statement.getParameter())){
					fRightOperandComposite = fConditionCombo = new ComboViewer(StatementEditor.this).getCombo();
				}
				else{
					fRightOperandComposite = fConditionCombo = new ComboViewer(StatementEditor.this, SWT.BORDER).getCombo();
				}
				prepareRelationalStatementEditor(statement, availableConditions(statement), statement.getCondition().getValueString());

				StatementEditor.this.layout();
				return null;
			}

			@Override
			public Object visit(ChoicesParentStatement statement) throws Exception {
				disposeRightOperandComposite();
				fRightOperandComposite = fConditionCombo = new ComboViewer(StatementEditor.this).getCombo();
				prepareRelationalStatementEditor(statement, availableConditions(statement), statement.getCondition().toString());

				StatementEditor.this.layout();
				return null;
			}

			@Override
			public Object visit(LabelCondition condition) throws Exception {
				return null;
			}

			@Override
			public Object visit(ChoiceCondition condition) throws Exception {
				return null;
			}

			private void disposeRightOperandComposite() {
				if(fRightOperandComposite != null && fRightOperandComposite.isDisposed() == false){
					fRightOperandComposite.dispose();
				}

			}

			private void prepareRelationalStatementEditor(IRelationalStatement statement, String[] items, String item) {
				fRelationCombo.setVisible(true);
				fRelationCombo.setItems(availableRelations(statement));
				fRelationCombo.setText(statement.getRelation().toString());

				fConditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, CONDITION_COMBO_WIDTH, 1));
				fConditionCombo.setItems(items);
				fConditionCombo.setText(item);
				fConditionCombo.addSelectionListener(new ConditionComboListener());
				StatementEditor.this.layout();
			}

			private String[] availableRelations(IRelationalStatement statement){
				List<String> relations = new ArrayList<String>();
				for(EStatementRelation r : statement.getAvailableRelations()){
					relations.add(r.toString());
				}
				return relations.toArray(new String[]{});
			}
		}

		public StatementEditor(Composite parent) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout(TOTAL_EDITOR_WIDTH, true));
			setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			fConstraintIf = new ConstraintInterface(ConstraintViewer.this);

			createStatementCombo(this);
			createRelationCombo(this);
		}

		public void setInput(AbstractStatement statement){
			fStatementIf = StatementInterfaceFactory.getInterface(statement, ConstraintViewer.this);
			fStatementCombo.setItems(statementComboItems(statement));
			fStatementCombo.setText(statement.getLeftOperandName());
			try{
				statement.accept(new EditorBuilder());
			}catch(Exception e){}
		}

		public void setConstraint(ConstraintNode constraintNode) {
			fConstraint = constraintNode;
			fConstraintIf.setTarget(constraintNode);
		}

		public void refreshConditionCombo(){
			try {
				if(fConditionCombo != null){
					String[] items = availableConditions(fSelectedStatement);
					String currentConditionText = (String)fSelectedStatement.accept(new CurrentConditionProvider());
					fConditionCombo.setItems(items);
					fConditionCombo.setText(currentConditionText);
				}
			} catch (Exception e) {
			}
		}

		private String[] availableConditions(AbstractStatement statement) {
			try {
				return (String[]) statement.accept(new AvailableConditionsProvider());
			} catch (Exception e) {
			}
			return new String[]{};
		}

		private void createStatementCombo(Composite parent) {
			fStatementCombo = new ComboViewer(parent).getCombo();
			fStatementCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, STATEMENT_COMBO_WIDTH, 1));
			fStatementCombo.addSelectionListener(new StatementComboListener());
		}

		private void createRelationCombo(Composite parent) {
			fRelationCombo = new ComboViewer(parent).getCombo();
			fRelationCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, RELATION_COMBO_WIDTH, 1));
			fRelationCombo.addSelectionListener(new RelationComboListener());
		}

		private String[] statementComboItems(AbstractStatement statement){
			List<String> items = new ArrayList<String>();
			items.addAll(Arrays.asList(FIXED_STATEMENTS));
			boolean consequence = fConstraint.getConstraint().getConsequence() == statement;
			for(MethodParameterNode c : fConstraint.getMethod().getParameters()){
				if(c.isExpected()){
					if(consequence){
						items.add(c.getName());
					}
				}
				else{
					if(c.getChoices().size() > 0){
						items.add(c.getName());
					}
				}
			}
			return items.toArray(new String[]{});
		}
	}

	private class AddStatementAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			AbstractStatement statement = fStatementIf.addNewStatement();
			if(statement != null){
			//modelUpdated must be called before to refresh viewer before selecting the newly added statement
				getTreeViewer().expandToLevel(statement, 1);
				getTreeViewer().setSelection(new StructuredSelection(statement));
			}
		}
	}

	public class DeleteStatementAction extends ModelModifyingAction {
		public DeleteStatementAction(IModelUpdateContext updateContext) {
			super(GlobalActions.DELETE.getId(), GlobalActions.DELETE.getName(), getTreeViewer(), ConstraintViewer.this);
		}

		@Override
		public boolean isEnabled(){
			return fSelectedStatement.getParent() != null;
		}

		@Override
		public void run(){
			AbstractStatement parent = fSelectedStatement.getParent();
			if(parent != null && fStatementIf.getParentInterface().removeChild(fSelectedStatement)){
				getViewer().setSelection(new StructuredSelection(parent));
			}
		}
	}

	private class StatementSelectionListener implements ISelectionChangedListener{

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			AbstractStatement statement = (AbstractStatement)((StructuredSelection)event.getSelection()).getFirstElement();
			if(statement != null){
				fSelectedStatement = statement;
				fStatementEditor.setInput(statement);
				updateSideButtons(statement);
			}
		}

		private void updateSideButtons(AbstractStatement selectedStatement) {
			boolean enableAddStatementButton = (selectedStatement instanceof StatementArray || selectedStatement.getParent() != null);
			boolean enableRemoveStatementButton = (selectedStatement.getParent() != null);
			fAddStatementButton.setEnabled(enableAddStatementButton);
			fRemoveStatementButton.setEnabled(enableRemoveStatementButton);
		}

	}

	public ConstraintViewer(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, STYLE);
		getSection().setText("Constraint editor");
		fAddStatementButton = addButton("Add statement", new AddStatementAdapter());
		fRemoveStatementButton = addButton("Remove statement", new ActionSelectionAdapter(new DeleteStatementAction(updateContext)));
		getViewer().addSelectionChangedListener(new StatementSelectionListener());

		fStatementEditor = new StatementEditor(getClientComposite());
		addKeyListener(SWT.DEL, SWT.NONE, new DeleteStatementAction(updateContext));
	}

	@Override
	protected IContentProvider viewerContentProvider() {
		return new StatementViewerContentProvider();
	}

	@Override
	protected IBaseLabelProvider viewerLabelProvider() {
		return new StatementViewerLabelProvider();
	}

	@Override
	protected int buttonsPosition(){
		return BUTTONS_ASIDE;
	}

	@Override
	protected int viewerStyle(){
		return VIEWER_STYLE;
	}

	public void setInput(ConstraintNode constraintNode){
		//Update the statement provider before setting input to get the correct images
		fCurrentConstraint = constraintNode.getConstraint();
		super.setInput(constraintNode.getConstraint());
		fStatementEditor.refreshConditionCombo();
		fStatementEditor.setConstraint(constraintNode);

		getTreeViewer().expandAll();
		if(getSelectedElement() == null){
			getViewer().setSelection(new StructuredSelection(constraintNode.getConstraint().getPremise()));
		}
	}
}
