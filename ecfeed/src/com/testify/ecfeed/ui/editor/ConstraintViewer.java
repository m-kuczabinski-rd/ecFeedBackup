package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.ConditionStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;

public class ConstraintViewer extends TreeViewerSection {

	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	
	private final String STATEMENT_FALSE = new StaticStatement(false).getLeftHandName();
	private final String STATEMENT_TRUE = new StaticStatement(true).getLeftHandName();;
	private final String STATEMENT_AND = new StatementArray(Operator.AND).getLeftHandName();
	private final String STATEMENT_OR = new StatementArray(Operator.OR).getLeftHandName();
	
	private final String[] FIXED_STATEMENTS = {STATEMENT_FALSE, STATEMENT_TRUE, STATEMENT_OR, STATEMENT_AND};

	private StatementViewerLabelProvider fStatementLabelProvider;

	private BasicStatement fSelectedStatement;
	private ConstraintNode fSelectedConstraint;

	private Combo fStatementCombo;
	private Combo fRelationCombo;
	private Combo fConditionCombo;
	
	private Button fAddStatementButton;
	private Button fRemoveStatementButton;
	
	private boolean fStatementEditListenersEnabled;

	private Composite fStatementEditComposite;

	private class AddStatementAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			BasicStatement newStatement = new StaticStatement(true); 
			fSelectedStatement.addStatement(newStatement);
			modelUpdated();
			//modelUpdated must be called before to refresh viewer before selecting the newly added statement
			getTreeViewer().expandToLevel(newStatement, 1);
			getTreeViewer().setSelection(new StructuredSelection(newStatement));
		}
	}
	
	private class RemoveStatementAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			BasicStatement parentStatement = fSelectedStatement.getParent();
			if(parentStatement != null){
				parentStatement.removeChild(fSelectedStatement);
				getViewer().setSelection(new StructuredSelection(parentStatement));
				modelUpdated();
			}
		}
	}
	
	private class ModifyRelationListener implements ModifyListener{
		@Override
		public void modifyText(ModifyEvent e) {
			if(fStatementEditListenersEnabled == false){
				return;
			}
			ConditionStatement statement = (ConditionStatement)fSelectedStatement;
			if(statement.getRelation().toString().equals(fRelationCombo.getText()) == false){
				statement.setRelation(Relation.getRelation(fRelationCombo.getText()));
				modelUpdated();
			}
		}
	}
	
	private class ModifyConditionListener implements ModifyListener{
		@Override
		public void modifyText(ModifyEvent e) {
			if(fStatementEditListenersEnabled == false){
				return;
			}
			ConditionStatement statement = (ConditionStatement)fSelectedStatement;
			if(statement.getConditionName().equals(fConditionCombo.getText()) == false){
				String conditionText = fConditionCombo.getText();
				PartitionNode partition = statement.getCategory().getPartition(conditionText);
				if(partition != null){//text in the combo is a partition name
					statement.setCondition(partition);
				}
				else{//text in the combo is a label
					statement.setCondition(conditionText);
				}
				modelUpdated();
			}
		}
	}
	
	private class StatementModifyListener implements ModifyListener{
		@Override
		public void modifyText(ModifyEvent e) {
			if(fStatementEditListenersEnabled == false){
				return;
			}
			if(fSelectedStatement.getLeftHandName().equals(fStatementCombo.getText()) == false){
				BasicStatement statement = createStatementFromCombo();
				if(statement != null){
					replaceSelectedStatement(statement);
				}
			} 	
		}

		private BasicStatement createStatementFromCombo() {
			BasicStatement statement = null;
			if(fStatementCombo.getText().equals(STATEMENT_TRUE)){
				statement = new StaticStatement(true);
			}
			else if(fStatementCombo.getText().equals(STATEMENT_FALSE)){
				statement = new StaticStatement(false);
			}
			else if (fStatementCombo.getText().equals(STATEMENT_AND)){
				statement = new StatementArray(Operator.AND);
			}
			else if(fStatementCombo.getText().equals(STATEMENT_OR)){
				statement = new StatementArray(Operator.OR);
			}
			else{
				MethodNode method = fSelectedConstraint.getMethod();
				CategoryNode category = method.getCategory(fStatementCombo.getText());
				Relation relation = Relation.EQUAL; 
				if(category != null){
					PartitionNode condition = category.getPartitions().get(0);
					statement = new ConditionStatement(category, relation, condition);
				}
			}
			return statement;
		}
	}
	
	private class StatementSelectionListener implements ISelectionChangedListener{
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			Object selectedElement = ((StructuredSelection)event.getSelection()).getFirstElement();
			if(selectedElement instanceof BasicStatement){
				fSelectedStatement = (BasicStatement)selectedElement;
				enableSideButtons(fSelectedStatement);
				refreshStatementEditPart(fSelectedStatement);
				fStatementEditComposite.layout();
			}
		}

		private void enableSideButtons(BasicStatement statement) {
			boolean enableAddStatementButton = (statement instanceof StatementArray 
					|| statement.getParent() != null);
			boolean enableRemoveStatementButton = (statement.getParent() != null);
			fAddStatementButton.setEnabled(enableAddStatementButton);
			fRemoveStatementButton.setEnabled(enableRemoveStatementButton);
		}

		private void refreshStatementEditPart(BasicStatement statement) {
			fStatementEditListenersEnabled = false;
			refreshStatementCombo(statement);
			if(statement instanceof ConditionStatement){
				refreshRelationCombo((ConditionStatement)statement);
				refreshConditionCombo((ConditionStatement)statement);
			}
			else{
				fRelationCombo.setVisible(false);
				fConditionCombo.setVisible(false);
			}
			fStatementEditListenersEnabled = true;
		}

		private void refreshStatementCombo(BasicStatement statement) {
			fStatementCombo.setText(statement.getLeftHandName());
		}

		private void refreshRelationCombo(ConditionStatement statement) {
			fRelationCombo.setVisible(true);
			fRelationCombo.setText(statement.getRelation().toString());
		}

		private void refreshConditionCombo(ConditionStatement statement) {
			fConditionCombo.setVisible(true);
			List<String> items = new ArrayList<String>();
			items.addAll(statement.getCategory().getAllPartitionNames());
			items.addAll(statement.getCategory().getAllPartitionLabels());
			fConditionCombo.setItems(items.toArray(new String[]{}));
			fConditionCombo.setText(statement.getConditionName());
		}
	}
	
	public ConstraintViewer(BasicDetailsPage page, FormToolkit toolkit) {
		super(page.getMainComposite(), toolkit, STYLE, page);
		getSection().setText("Constraint editor");
		fAddStatementButton = addButton("Add statement", new AddStatementAdapter());
		fRemoveStatementButton = addButton("Remove statement", new RemoveStatementAdapter());
		createStatementEditComposite();
		getViewer().addSelectionChangedListener(new StatementSelectionListener());
	}

	private void createStatementEditComposite(){
		fStatementEditComposite = getToolkit().createComposite(getClientComposite());
		fStatementEditComposite.setLayout(new GridLayout(3, false));
		fStatementEditComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		createStatementCombo();
		createRelationCombo();
		createConditionCombo();
	}


	private void createStatementCombo() {
		fStatementCombo = new ComboViewer(fStatementEditComposite).getCombo();
		fStatementCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fStatementCombo.addModifyListener(new StatementModifyListener());
	}

	private void createRelationCombo() {
		fRelationCombo = new ComboViewer(fStatementEditComposite, SWT.READ_ONLY).getCombo();
		fRelationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		fRelationCombo.setItems(new String[]{Relation.EQUAL.toString(), Relation.NOT.toString()});
		fRelationCombo.addModifyListener(new ModifyRelationListener());
	}

	private void createConditionCombo() {
		fConditionCombo = new ComboViewer(fStatementEditComposite).getCombo();
		fConditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fConditionCombo.addModifyListener(new ModifyConditionListener());
	}
	
	private void replaceSelectedStatement(BasicStatement newStatement) {
		Constraint constraint = fSelectedConstraint.getConstraint();
		if(fSelectedStatement == constraint.getPremise()){
			constraint.setPremise(newStatement);
		}
		else if(fSelectedStatement == constraint.getConsequence()){
			constraint.setConsequence(newStatement);
		}
		else if(fSelectedStatement.getParent() != null){
			fSelectedStatement.getParent().replaceChild(fSelectedStatement, newStatement);
			fSelectedStatement = newStatement;
		}
		modelUpdated();
		getViewer().setSelection(new StructuredSelection(newStatement));
	}

	@Override
	protected boolean buttonsBelow(){
		return false;
	}


	@Override
	protected IContentProvider viewerContentProvider() {
		return new StatementViewerContentProvider();
	}

	@Override
	protected IBaseLabelProvider viewerLabelProvider() {
		if(fStatementLabelProvider == null){
			fStatementLabelProvider = new StatementViewerLabelProvider();
		}
		return fStatementLabelProvider;
	}

	public void setInput(ConstraintNode constraintNode){
		super.setInput(constraintNode.getConstraint());
		fSelectedConstraint = constraintNode;
		
		fStatementLabelProvider.setConstraint(constraintNode.getConstraint());

		fStatementEditListenersEnabled = false;
		List<String> items = new ArrayList<String>();
		items.addAll(Arrays.asList(FIXED_STATEMENTS));
		items.addAll(fSelectedConstraint.getMethod().getCategoriesNames());
		fStatementCombo.setItems(items.toArray(new String[]{}));
		if(fSelectedStatement != null){
			fStatementCombo.setText(fSelectedStatement.getLeftHandName());
		}
		fStatementEditListenersEnabled = true;

		getTreeViewer().expandAll();
		if(getSelectedElement() == null){
			getViewer().setSelection(new StructuredSelection(fSelectedConstraint.getConstraint().getPremise()));
		}
	}
}
