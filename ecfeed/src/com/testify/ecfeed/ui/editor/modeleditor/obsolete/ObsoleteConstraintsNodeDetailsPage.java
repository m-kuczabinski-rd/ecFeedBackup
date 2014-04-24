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

package com.testify.ecfeed.ui.editor.modeleditor.obsolete;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.ConditionStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.ModelUtils;

public class ObsoleteConstraintsNodeDetailsPage extends ObsoleteGenericNodeDetailsPage {

	private static final String STATEMENT_FALSE = "FALSE";
	private static final String STATEMENT_TRUE = "TRUE";
	private static final String STATEMENT_AND = "AND";
	private static final String STATEMENT_OR = "OR";
	private static final String STATEMENT_EMPTY = "";
	
	
	private Section fMainSection;
	private ConstraintNode fSelectedConstraint;
	private Combo fConstraintNameCombo;
	private Constraint fConstraint;
	private TreeViewer fConstraintViewer;
	private Button fAddStatementButton;
	private Button fRemoveStatementButton;
	private BasicStatement fSelectedStatement;
	private Combo fStatementEditCombo;
	private Combo fRelationCombo;
	private Combo fConditionCombo;
	private ModifyListener fStatementComboListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			statementComboModified(fStatementEditCombo.getText());
		}
	};
	private Composite fStatementEditComposite;
	private Composite fMainComposite;
	
	public ObsoleteConstraintsNodeDetailsPage(ObsoleteModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = fToolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);

		fMainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		fToolkit.paintBordersFor(fMainComposite);
		fMainSection.setClient(fMainComposite);
		fMainComposite.setLayout(new GridLayout(1, false));

		createConstraintNameComposite(fMainComposite);
		
		createViewerComposite(fMainComposite);
	}

	private void createConstraintNameComposite(Composite container) {
		Composite constraintNameComposite = new Composite(container, SWT.NONE);
		constraintNameComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		getToolkit().adapt(constraintNameComposite);
		getToolkit().paintBordersFor(constraintNameComposite);
		constraintNameComposite.setLayout(new GridLayout(3, false));
		
		Label constraintNameLabel = new Label(constraintNameComposite, SWT.NONE);
		constraintNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		getToolkit().adapt(constraintNameLabel, true, true);
		constraintNameLabel.setText("Constraint name");
		
		ComboViewer comboViewer = new ComboViewer(constraintNameComposite, SWT.NONE);
		fConstraintNameCombo = comboViewer.getCombo();
		fConstraintNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getToolkit().paintBordersFor(fConstraintNameCombo);
		fConstraintNameCombo.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				renameConstraint();
			}
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		fConstraintNameCombo.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					renameConstraint();
				}
			}
		});
		getToolkit().adapt(fConstraintNameCombo, true, true);
		
		createButton(constraintNameComposite, "Change", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				renameConstraint();
			}
		});
	}

	private void renameConstraint() {
		String newName = fConstraintNameCombo.getText();
		if(ModelUtils.validateConstraintName(newName)){
			fSelectedConstraint.setName(newName);
			updateModel(fSelectedConstraint);
		}
		else{
			MessageDialog.openError(getActiveShell(), 
					Messages.DIALOG_CONSTRAINT_NAME_PROBLEM_TITLE, 
					Messages.DIALOG_CONSTRAINT_NAME_PROBLEM_MESSAGE);
			fConstraintNameCombo.setText(fSelectedConstraint.getName());
		}
	}

	private void createViewerComposite(Composite parent) {
		Composite statementComposite = fToolkit.createComposite(parent);
		statementComposite.setLayout(new GridLayout(2, false));
		statementComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createStatementViewer(statementComposite);
		createStatementViewerButtons(statementComposite);
	}

	private void createStatementViewer(Composite parent) {
		fSelectedStatement = null;
		fConstraintViewer = new TreeViewer(parent, SWT.BORDER);
		fConstraintViewer.getTree().setLayoutData(VIEWERS_GRID_DATA);
		fConstraintViewer.setContentProvider(new ObsoleteStatementViewerContentProvider());
		fConstraintViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedElement = ((StructuredSelection)event.getSelection()).getFirstElement();
				if(selectedElement instanceof BasicStatement){
					fSelectedStatement = (BasicStatement)selectedElement;
					boolean enableAddStatementButton = (fSelectedStatement instanceof StatementArray 
							|| fSelectedStatement.getParent() != null);
					boolean enableRemoveStatementButton = (fSelectedStatement.getParent() != null);

					fAddStatementButton.setEnabled(enableAddStatementButton);
					fRemoveStatementButton.setEnabled(enableRemoveStatementButton);
				}
				
				if(fStatementEditComposite!= null && !fStatementEditComposite.isDisposed()){
					fStatementEditComposite.dispose();
				}
				createStatementEditComposite(fMainComposite, fSelectedStatement);
			}
		});
	}
	
	private void createStatementViewerButtons(Composite parent) {
		Composite buttonsComposite = fToolkit.createComposite(parent);
		buttonsComposite.setLayout(new GridLayout(1, false));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true, 1, 1));

		fAddStatementButton = createButton(buttonsComposite, "Add Statement", new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				BasicStatement newStatement = new StaticStatement(true); 
				fSelectedStatement.addStatement(newStatement);
				fConstraintViewer.expandToLevel(newStatement, 1);
				updateModel(fSelectedConstraint);
				fConstraintViewer.setSelection(new StructuredSelection(newStatement));
			}
		});
		fAddStatementButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		fRemoveStatementButton = createButton(buttonsComposite, "Remove Selected", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				BasicStatement parentStatement = fSelectedStatement.getParent();
				if(parentStatement != null){
					parentStatement.removeChild(fSelectedStatement);
					fConstraintViewer.setSelection(new StructuredSelection(parentStatement));
					updateModel(fSelectedConstraint);
				}
			}
		});
		fRemoveStatementButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}
	
	private void createStatementEditComposite(Composite parent, BasicStatement editedStatement){
		fStatementEditComposite = fToolkit.createComposite(parent);
		fStatementEditComposite.setLayout(new GridLayout(3, false));
		fStatementEditComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		createStatementCombo(fStatementEditComposite, editedStatement);
		if(editedStatement instanceof ConditionStatement){
			ConditionStatement conditionStatement = (ConditionStatement)editedStatement;
			createRelationCombo(fStatementEditComposite, conditionStatement);
			createConditionCombo(fStatementEditComposite, conditionStatement);
		}
		fMainComposite.layout();
	}
	
	private void createStatementCombo(Composite parent, BasicStatement editedStatement) {
		fStatementEditCombo = new ComboViewer(parent, SWT.READ_ONLY).getCombo();
		fStatementEditCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		fToolkit.paintBordersFor(fStatementEditCombo);
		fToolkit.adapt(fStatementEditCombo, true, true);

		ArrayList<String> items = new ArrayList<String>(Arrays.asList(new String[]{
				STATEMENT_AND, 
				STATEMENT_OR,
				STATEMENT_TRUE,
				STATEMENT_FALSE}));
		ArrayList<String> categories = fSelectedConstraint.getMethod().getOrdinaryCategoriesNames();
		items.addAll(categories);
		fStatementEditCombo.setItems(items.toArray(new String[]{}));
		String comboText = getStatementComboText(editedStatement);
		fStatementEditCombo.setText(comboText);
		fStatementEditCombo.addModifyListener(fStatementComboListener);
	}

	private String getStatementComboText(BasicStatement statement) {
		if(statement instanceof StaticStatement){
			return ((StaticStatement)statement).getValue()?STATEMENT_TRUE:STATEMENT_FALSE;
		}
		else if(statement instanceof StatementArray){
			switch(((StatementArray)statement).getOperator()){
			case AND:
				return STATEMENT_AND;
			case OR:
				return STATEMENT_OR;
			}
		}
		else if(statement instanceof ConditionStatement){
			ConditionStatement conditionStatement = (ConditionStatement)statement;
			return conditionStatement.getCategory().getName();
		}
		return STATEMENT_EMPTY;
	}

	private void createRelationCombo(Composite parent, ConditionStatement statement) {
		fRelationCombo = new ComboViewer(parent, SWT.READ_ONLY).getCombo();
		fRelationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fToolkit.paintBordersFor(fRelationCombo);
		fToolkit.adapt(fRelationCombo, true, true);
		fRelationCombo.setItems(new String[]{
				Relation.EQUAL.toString(),
				Relation.NOT.toString(),
		});
		fRelationCombo.setText(statement.getRelation().toString());
		fRelationCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(fSelectedStatement instanceof ConditionStatement){
					ConditionStatement statement = (ConditionStatement)fSelectedStatement;
					statement.setRelation(Relation.getRelation(fRelationCombo.getText()));
					updateModel(fSelectedConstraint);
				}
			}
		});
	}

	private void createConditionCombo(Composite parent, ConditionStatement statement) {
		fConditionCombo = new ComboViewer(parent, SWT.READ_ONLY).getCombo();
		fConditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fToolkit.paintBordersFor(fConditionCombo);
		fToolkit.adapt(fConditionCombo, true, true);

		CategoryNode parentCategory = statement.getCategory();
		Set<String> items = new LinkedHashSet<String>(parentCategory.getAllPartitionNames());
		items.addAll(parentCategory.getAllPartitionLabels());
		fConditionCombo.setItems(items.toArray(new String[]{}));

		String text = statement.getConditionName();
		fConditionCombo.setText(text);
		
		fConditionCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ConditionStatement conditionStatement = (ConditionStatement)fSelectedStatement;
				String newText = fConditionCombo.getText();
				CategoryNode category = conditionStatement.getCategory();
				Relation relation = conditionStatement.getRelation();
				Object condition = getNewCondition(category, newText);
				ConditionStatement newStatement = null;
				if(condition instanceof String){
					newStatement = new ConditionStatement(category, relation, (String)condition);
				}
				else if(condition instanceof PartitionNode){
					newStatement = new ConditionStatement(category, relation, (PartitionNode)condition);
				}
				
				replaceSelectedStatement(newStatement);
				updateModel(fSelectedConstraint);
			}

			private Object getNewCondition(CategoryNode category, String newText) {
				if(category.getAllPartitionNames().contains(newText)){
					if(category.getAllPartitionLabels().contains(newText) == false){
						return category.getPartition(newText);
					}
				}
				return newText;
			}
		});
	}

	private void statementComboModified(String newValue){
		if(fSelectedStatement instanceof StatementArray && 
				(newValue.equals(STATEMENT_AND) || newValue.equals(STATEMENT_OR))){
			Operator operator = newValue.equals(STATEMENT_AND)?Operator.AND:Operator.OR;
			((StatementArray)fSelectedStatement).setOperator(operator);
			updateModel(fSelectedConstraint);
		}
		else if(fSelectedStatement instanceof StaticStatement &&
				(newValue.equals(STATEMENT_FALSE) || newValue.equals(STATEMENT_TRUE))){
			boolean value = newValue.equals(STATEMENT_TRUE); 
			((StaticStatement)fSelectedStatement).setValue(value);
			updateModel(fSelectedConstraint);
		}
		
		else{
			BasicStatement newStatement = createNewStatement(newValue);
			replaceSelectedStatement(newStatement);
			updateModel(fSelectedConstraint);
		}
	}

	private void replaceSelectedStatement(BasicStatement newStatement) {
		if(fSelectedStatement == fConstraint.getPremise()){
			fConstraint.setPremise(newStatement);
		}
		else if(fSelectedStatement == fConstraint.getConsequence()){
			fConstraint.setConsequence(newStatement);
		}
		else if(fSelectedStatement.getParent() != null){
			fSelectedStatement.getParent().replaceChild(fSelectedStatement, newStatement);
			fSelectedStatement = newStatement;
		}
		fConstraintViewer.setSelection(new StructuredSelection(newStatement));
	}
	
	private BasicStatement createNewStatement(String newValue) {
		if(newValue.equals(STATEMENT_TRUE)){
			return new StaticStatement(true);
		}
		else if(newValue.equals(STATEMENT_FALSE)){
			return new StaticStatement(false);
		}
		else if(newValue.equals(STATEMENT_AND)){
			return new StatementArray(Operator.AND);
		}
		else if(newValue.equals(STATEMENT_OR)){
			return new StatementArray(Operator.OR);
		}
		else{
			CategoryNode category = fSelectedConstraint.getMethod().getCategory(newValue);
			if(category != null){
				return new ConditionStatement(category, Relation.EQUAL, category.getPartitions().get(0));
			}
		}
		return null;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedConstraint = (ConstraintNode)fSelectedNode;
		refresh();
	}
	
	@Override
	public void refresh(){
		if(fSelectedConstraint == null || fSelectedConstraint.getParent() == null){
			return;
		}
		fConstraint = fSelectedConstraint.getConstraint();
		fMainSection.setText(fSelectedConstraint.toString());
		fConstraintNameCombo.setItems(fSelectedConstraint.getMethod().
				getConstraintsNames().toArray(new String[]{}));
		fConstraintNameCombo.setText(fSelectedConstraint.getName());
		fConstraintViewer.setLabelProvider(new ObsoleteStatementViewerLabelProvider(fConstraint));
		fConstraintViewer.setInput(fConstraint);
		fConstraintViewer.expandAll();
	}
}
