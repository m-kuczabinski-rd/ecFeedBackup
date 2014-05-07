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
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.Constants;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedCategoryNode;
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

	private Button fStatementButton;
	private ControlMenuListener fStatementSelectionListener;

	private Combo fRelationCombo;
	private Button fConditionButton;
	private ControlMenuListener fConditionSelectionListener;
	private Text fConditionText;
	private ControlMenuListener fConditionBoolMenuListener;
	
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
			refreshStatementMenu(statement);
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
				fConditionButton.setVisible(false);
				fConditionText.setVisible(false);
				fConditionLayout.topControl = null;
			}
			fStatementEditListenersEnabled = true;
		}

		private void refreshStatementMenu(BasicStatement statement) {
			List<String> items = new ArrayList<String>();			
			fStatementSelectionListener.clearData();
			fStatementSelectionListener.addData(Arrays.asList(FIXED_STATEMENTS), "");
			if(fSelectedStatement == fSelectedConstraint.getConstraint().getConsequence()){
				items.addAll(fSelectedConstraint.getMethod().getCategoriesNames());
			}
			else{
				items.addAll(fSelectedConstraint.getMethod().getOrdinaryCategoriesNames());
			}
			fStatementSelectionListener.addData(items, "---ARGUMENTS---");
			fStatementSelectionListener.createMenu();
			fStatementButton.setText(statement.getLeftHandName());
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
			fConditionLayout.topControl = fConditionButton;
			fConditionButton.setVisible(true);
			
			fConditionSelectionListener.clearData();
			fConditionSelectionListener.addData(statement.getCategory().getAllPartitionNames(), "");
			fConditionSelectionListener.addData(statement.getCategory().getAllPartitionLabels(), "---LABELS---");
			fConditionButton.setText(statement.getConditionName());
			fConditionSelectionListener.createMenu();

			fConditionText.setVisible(false);
		}

		private void refreshConditionComposite(ExpectedValueStatement statement) {
			fConditionLayout.topControl = fConditionText;
			fConditionButton.setVisible(false);
			fConditionText.setVisible(true);
			fConditionBoolMenuListener.setEnabled(statement.getCategory().getType().equals(Constants.TYPE_NAME_BOOLEAN));
			fConditionText.setEditable(!statement.getCategory().getType().equals(Constants.TYPE_NAME_BOOLEAN));		
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

	public void conditionButtonModified(String value, int index){
		if(fStatementEditListenersEnabled == false){
			return;
		}
		PartitionedCategoryStatement statement = (PartitionedCategoryStatement)fSelectedStatement;
		String conditionText = fConditionButton.getText();
		if(statement.getConditionName().equals(fConditionButton.getText()) == false){
			PartitionNode partition = statement.getCategory().getPartition(conditionText);
			if(partition != null){// text in the combo is a partition name
				statement.setCondition(partition);
			} else{// text in the combo is a label
				statement.setCondition(conditionText);
			}
		} else{
			if((index > statement.getCategory().getAllPartitionNames().size()) && statement.getConditionValue() instanceof PartitionNode){
				// text in the combo is a label
				statement.setCondition(conditionText);
			} else if((index < statement.getCategory().getAllPartitionNames().size()) && statement.getConditionValue() instanceof String){

			} else
				return;
		}
		modelUpdated();
	}

	public void statementButtonModified(String value, int index){
		if(fStatementEditListenersEnabled == false){
			return;
		}
		String buttonText = fStatementButton.getText();
		if(fSelectedStatement.getLeftHandName().equals(buttonText) == false){
			BasicStatement statement = createStatementFromCombo();
			if(statement != null){
				replaceSelectedStatement(statement);
			}
		} else{
			if(index < FIXED_STATEMENTS.length){
				if(fSelectedStatement instanceof StatementArray) return;
			}
			if(index >= FIXED_STATEMENTS.length){
				if(fSelectedStatement instanceof PartitionedCategoryStatement) return;
			}		
			
			BasicStatement statement = createStatementFromCombo();
			if(statement != null){
				replaceSelectedStatement(statement);
			}
		}
	}

	private BasicStatement createStatementFromCombo(){
		BasicStatement statement = null;
		if(fStatementSelectionListener.getSelectedItemIndex() < FIXED_STATEMENTS.length){
			if(fStatementButton.getText().equals(STATEMENT_TRUE)){
				statement = new StaticStatement(true);
			} else if(fStatementButton.getText().equals(STATEMENT_FALSE)){
				statement = new StaticStatement(false);
			} else if(fStatementButton.getText().equals(STATEMENT_AND)){
				if(fSelectedStatement instanceof StatementArray && fSelectedStatement.getLeftHandName() == STATEMENT_OR){
					StatementArray statementArray = (StatementArray)fSelectedStatement;
					statementArray.setOperator(Operator.AND);
					statement = fSelectedStatement;
				} else
					statement = new StatementArray(Operator.AND);
			} else if(fStatementButton.getText().equals(STATEMENT_OR)){
				if(fSelectedStatement instanceof StatementArray && fSelectedStatement.getLeftHandName() == STATEMENT_AND){
					StatementArray statementArray = (StatementArray)fSelectedStatement;
					statementArray.setOperator(Operator.OR);
					statement = fSelectedStatement;
				} else
					statement = new StatementArray(Operator.OR);
			}
			if(statement != null) return statement;
		} else {	
			MethodNode method = fSelectedConstraint.getMethod();
			Relation relation = Relation.EQUAL;
			String categoryName = fStatementButton.getText();
	
			PartitionedCategoryNode partitionedCategory = method.getPartitionedCategory(categoryName);
			ExpectedCategoryNode expectedCategory = method.getExpectedCategory(categoryName);
			if(partitionedCategory != null){
				PartitionNode condition = partitionedCategory.getPartitions().get(0);
				statement = new PartitionedCategoryStatement(partitionedCategory, relation, condition);
			} else if(expectedCategory != null){
				PartitionNode condition = new PartitionNode("expected", expectedCategory.getDefaultValue());
				condition.setParent(expectedCategory);
				statement = new ExpectedValueStatement(expectedCategory, condition);
			}
		}
		return statement;
	}

	private void createStatementEditComposite(){
		fStatementEditComposite = getToolkit().createComposite(getClientComposite());
		fStatementEditComposite.setLayout(new GridLayout(3, false));
		fStatementEditComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		createStatementButton();
		createRelationCombo();
		createConditionComposite();
	}

	private void createStatementButton() {
		fStatementButton = new Button(fStatementEditComposite, SWT.PUSH);
		fStatementButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fStatementSelectionListener = new ControlMenuListener(fStatementEditComposite, fStatementButton){
			@Override
			public void menuItemSelected(int index, SelectionEvent e){
				MenuItem item = (MenuItem)e.getSource();
				fStatementButton.setText(item.getText());
				statementButtonModified(item.getText(), index);
			}

		};
		fStatementButton.addListener(SWT.Selection, fStatementSelectionListener);
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
		
		fConditionButton = new Button(conditionComposite, SWT.PUSH);
		fConditionButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fConditionSelectionListener = new ControlMenuListener(conditionComposite, fConditionButton){
			@Override
			public void menuItemSelected(int index, SelectionEvent e){
				MenuItem item = (MenuItem)e.getSource();
				fConditionButton.setText(item.getText());
				conditionButtonModified(item.getText(), index);
			}

		};
		fConditionButton.addListener(SWT.Selection, fConditionSelectionListener);
		
		fConditionText = getToolkit().createText(conditionComposite, "", SWT.BORDER);
		fConditionBoolMenuListener = new ControlMenuListener(conditionComposite, fConditionButton){
			{
				ArrayList<String> boolList = new ArrayList<>();
				boolList.add(STATEMENT_TRUE);
				boolList.add(STATEMENT_FALSE);
				addData(boolList, "");
				createMenu();
			}
			@Override
			public void menuItemSelected(int index, SelectionEvent e){
				ExpectedValueStatement statement = (ExpectedValueStatement)fSelectedStatement;
				MenuItem item = (MenuItem)e.getSource();
				if(!fConditionText.getText().equals(item.getText())){
					fConditionText.setText(item.getText());
					statement.getCondition().setValue(item.getText());
					modelUpdated();
				}
			}

		};
		fConditionText.addListener(SWT.MouseDown, fConditionBoolMenuListener);
		fConditionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fConditionText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					ExpectedValueStatement statement = (ExpectedValueStatement)fSelectedStatement;
					AbstractCategoryNode category = statement.getCategory();
					Object newValue = category.getPartitionValueFromString(fConditionText.getText());
					if(newValue != null && !newValue.equals(statement.getCondition().getValue())){
						statement.getCondition().setValue(newValue);
						fConditionText.setText(statement.getCondition().getValueString());
						modelUpdated();
					}
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
