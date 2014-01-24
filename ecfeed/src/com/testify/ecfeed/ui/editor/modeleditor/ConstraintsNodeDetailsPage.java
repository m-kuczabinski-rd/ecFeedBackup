/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.modeleditor;

import java.util.Arrays;
import java.util.ArrayList;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.Statement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.ui.common.ModelUtils;

public class ConstraintsNodeDetailsPage extends GenericNodeDetailsPage {

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
	
	public ConstraintsNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
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
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					DialogStrings.DIALOG_CONSTRAINT_NAME_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					DialogStrings.DIALOG_CONSTRAINT_NAME_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
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
		fConstraintViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		fConstraintViewer.setContentProvider(new StatementViewerContentProvider());
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
		if(editedStatement instanceof Statement){
			createRelationCombo(fStatementEditComposite);
			createConditionCombo(fStatementEditComposite);
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
		else if(statement instanceof Statement){
			PartitionNode condition = ((Statement)statement).getCondition();
			CategoryNode category = condition.getCategory();
			return category.getName();
		}
		return STATEMENT_EMPTY;
	}

	private void createRelationCombo(Composite parent) {
		fRelationCombo = new ComboViewer(parent, SWT.READ_ONLY).getCombo();
		fRelationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fToolkit.paintBordersFor(fRelationCombo);
		fToolkit.adapt(fRelationCombo, true, true);
		fRelationCombo.setItems(new String[]{
				Relation.EQUAL.toString(),
				Relation.NOT.toString(),
		});
		if(fSelectedStatement instanceof Statement){
			fRelationCombo.setText(((Statement)fSelectedStatement).getRelation().toString());
		}
		fRelationCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(fSelectedStatement instanceof Statement){
					((Statement)fSelectedStatement).setRelation(Relation.getRelation(fRelationCombo.getText()));
					updateModel(fSelectedConstraint);
				}
			}
		});
	}

	private void createConditionCombo(Composite parent) {
		fConditionCombo = new ComboViewer(parent, SWT.READ_ONLY).getCombo();
		fConditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fToolkit.paintBordersFor(fConditionCombo);
		fToolkit.adapt(fConditionCombo, true, true);

		if(fSelectedStatement instanceof Statement){
			PartitionNode condition = ((Statement)fSelectedStatement).getCondition();
			CategoryNode parentCategory = condition.getCategory();
			fConditionCombo.setItems(parentCategory.getLeafPartitionNames().toArray(new String[]{}));
			fConditionCombo.setText(condition.getName());
		}
		fConditionCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(fSelectedStatement instanceof Statement){
					CategoryNode category = fSelectedConstraint.getMethod().getCategory(fStatementEditCombo.getText());
					((Statement)fSelectedStatement).setCondition(category.getPartition(fConditionCombo.getText()));
					updateModel(fSelectedConstraint);
				}
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
			if(fSelectedStatement == fConstraint.getPremise()){
				fConstraint.setPremise(newStatement);
			}
			else if(fSelectedStatement == fConstraint.getConsequence()){
				fConstraint.setConsequence(newStatement);
			}
			else if(fSelectedStatement.getParent() != null){
				fSelectedStatement.getParent().replaceChild(fSelectedStatement, newStatement);
			}
			updateModel(fSelectedConstraint);
			fConstraintViewer.setSelection(new StructuredSelection(newStatement));
		}
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
				return new Statement(category.getPartitions().get(0), Relation.EQUAL);
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
		fConstraintViewer.setLabelProvider(new StatementViewerLabelProvider(fConstraint));
		fConstraintViewer.setInput(fConstraint);
		fConstraintViewer.expandAll();
	}
}
