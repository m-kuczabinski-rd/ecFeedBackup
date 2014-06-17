/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.IRelationalStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
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
	private Text fConditionText;
	
	private Button fAddStatementButton;
	private Button fRemoveStatementButton;
	
	private boolean fStatementEditListenersEnabled;

	private Composite fStatementEditComposite;

	private StackLayout fConditionLayout;

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
			IRelationalStatement statement = (IRelationalStatement)fSelectedStatement;
			if(statement.getRelation().toString().equals(fRelationCombo.getText()) == false){
				statement.setRelation(Relation.getRelation(fRelationCombo.getText()));
				modelUpdated();
			}
		}
	}
	
	private class ModifyConditionComboListener implements ModifyListener{
		@Override
		public void modifyText(ModifyEvent e) {
			if(fStatementEditListenersEnabled == false){
				return;
			}
			PartitionedCategoryStatement statement = (PartitionedCategoryStatement)fSelectedStatement;
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
				if(fSelectedStatement instanceof StatementArray && fSelectedStatement.getLeftHandName() == STATEMENT_OR){
					StatementArray statementArray = (StatementArray) fSelectedStatement;
					statementArray.setOperator(Operator.AND);	
					statement = fSelectedStatement;
				} else
				statement = new StatementArray(Operator.AND);
			}
			else if(fStatementCombo.getText().equals(STATEMENT_OR)){
				if(fSelectedStatement instanceof StatementArray && fSelectedStatement.getLeftHandName() == STATEMENT_AND){
					StatementArray statementArray = (StatementArray) fSelectedStatement;
					statementArray.setOperator(Operator.OR);	
					statement = fSelectedStatement;
				} else
				statement = new StatementArray(Operator.OR);
			}
			else{
				MethodNode method = fSelectedConstraint.getMethod();
				Relation relation = Relation.EQUAL; 
				String categoryName = fStatementCombo.getText();

				CategoryNode category = method.getCategory(categoryName);
				
				if(!category.isExpected()){
					PartitionNode condition = category.getPartitions().get(0);
					statement = new PartitionedCategoryStatement(category, relation, condition);
				}
				else{
					PartitionNode condition = new PartitionNode("expected", category.getDefaultValueString());
					condition.setParent(category);
					statement = new ExpectedValueStatement(category, condition);
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
			if(statement instanceof IRelationalStatement){
				refreshRelationCombo((IRelationalStatement)statement);
				if(statement instanceof PartitionedCategoryStatement){
					refreshConditionComposite((PartitionedCategoryStatement)statement);
				}
				else if(statement instanceof ExpectedValueStatement){
					refreshConditionComposite((ExpectedValueStatement)statement);
				}
			}
			else{
				fRelationCombo.setVisible(false);
				fConditionCombo.setVisible(false);
				fConditionText.setVisible(false);
				fConditionLayout.topControl = null;
			}
			fStatementEditListenersEnabled = true;
		}

		private void refreshStatementCombo(BasicStatement statement) {
			List<String> items = new ArrayList<String>();
			items.addAll(Arrays.asList(FIXED_STATEMENTS));
			if(fSelectedStatement == fSelectedConstraint.getConstraint().getConsequence()){
				items.addAll(fSelectedConstraint.getMethod().getCategoriesNames());
			}
			else{
				items.addAll(fSelectedConstraint.getMethod().getCategoriesNames(false));
			}
			fStatementCombo.setItems(items.toArray(new String[]{}));

			fStatementCombo.setText(statement.getLeftHandName());
		}

		private void refreshRelationCombo(IRelationalStatement statement) {
			fRelationCombo.setVisible(true);
			List<String> items = new ArrayList<String>();
			for(Relation relation : statement.getAvailableRelations()){
				items.add(relation.toString());
			}
			fRelationCombo.setItems(items.toArray(new String[]{}));
			fRelationCombo.setText(statement.getRelation().toString());
		}

		private void refreshConditionComposite(PartitionedCategoryStatement statement) {
			List<String> items = new ArrayList<String>();
			items.addAll(statement.getCategory().getAllPartitionNames());
			items.addAll(statement.getCategory().getAllPartitionLabels());

			fConditionLayout.topControl = fConditionCombo;
			fConditionCombo.setVisible(true);
			fConditionText.setVisible(false);
			fConditionCombo.setItems(items.toArray(new String[]{}));
			fConditionCombo.setText(statement.getConditionName());
		}

		private void refreshConditionComposite(ExpectedValueStatement statement) {
			fConditionLayout.topControl = fConditionText;
			fConditionCombo.setVisible(false);
			fConditionText.setVisible(true);
			fConditionText.setText(statement.getCondition().getValueString());
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
		createConditionComposite();
	}


	private void createStatementCombo() {
		fStatementCombo = new ComboViewer(fStatementEditComposite).getCombo();
		fStatementCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fStatementCombo.addModifyListener(new StatementModifyListener());
	}

	private void createRelationCombo() {
		fRelationCombo = new ComboViewer(fStatementEditComposite, SWT.READ_ONLY).getCombo();
		fRelationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		fRelationCombo.addModifyListener(new ModifyRelationListener());
	}

	private void createConditionComposite() {
		Composite conditionComposite = getToolkit().createComposite(fStatementEditComposite);
		fConditionLayout = new StackLayout();
		conditionComposite.setLayout(fConditionLayout);
		conditionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fConditionCombo = new ComboViewer(conditionComposite).getCombo();
		fConditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fConditionCombo.addModifyListener(new ModifyConditionComboListener());
		
		fConditionText = getToolkit().createText(conditionComposite, "", SWT.BORDER);
		fConditionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fConditionText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					ExpectedValueStatement statement = (ExpectedValueStatement)fSelectedStatement;
					if(!fConditionText.getText().equals(statement.getCondition().getValueString())){
						statement.getCondition().setValueString(fConditionText.getText());
						modelUpdated();
					}
					fConditionText.setText(statement.getCondition().getValueString());

				}
			}
		});
		getToolkit().paintBordersFor(conditionComposite);
		getToolkit().paintBordersFor(fStatementEditComposite);
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
	protected int buttonsPosition(){
		return BUTTONS_ASIDE;
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

		getTreeViewer().expandAll();
		if(getSelectedElement() == null){
			getViewer().setSelection(new StructuredSelection(fSelectedConstraint.getConstraint().getPremise()));
		}
	}
}
