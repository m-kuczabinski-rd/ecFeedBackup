package com.testify.ecfeed.ui.editor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.IRelationalStatement;
import com.testify.ecfeed.model.constraint.IStatementVisitor;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.PartitionCondition;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.BasicStatementInterface;
import com.testify.ecfeed.ui.modelif.CategoryInterface;
import com.testify.ecfeed.ui.modelif.ConstraintInterface;
import com.testify.ecfeed.ui.modelif.StatementInterfaceFactory;

public class ConstraintViewer extends TreeViewerSection {

	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private Button fAddStatementButton;
	private Button fRemoveStatementButton;
	
	private Constraint fCurrentConstraint;
	private BasicStatementInterface fStatementIf;
	private BasicStatement fSelectedStatement;

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
			if(parentElement instanceof BasicStatement){
				return ((BasicStatement)parentElement).getChildren().toArray();
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element) {
			if(element instanceof BasicStatement){
				return ((BasicStatement)element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof StatementArray){
				StatementArray statementArray = (StatementArray)element;
				List<BasicStatement> children = statementArray.getChildren();
				return (children.size() > 0);
			}
			return false;
		}
	}
	
	private class StatementViewerLabelProvider extends LabelProvider {
		
		public String getText(Object element){
			if(element instanceof StatementArray){
				return ((StatementArray)element).getOperator().toString();
			}
			else if(element instanceof BasicStatement){
				return ((BasicStatement)element).toString();
			}
			return null;
		}
		
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
		private final String STATEMENT_AND = new StatementArray(Operator.AND).getLeftOperandName();
		private final String STATEMENT_OR = new StatementArray(Operator.OR).getLeftOperandName();
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
		private StatementInterfaceFactory fInterfaceFactory;
		private ConstraintInterface fConstraintIf;
		
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
				fStatementIf.setConditionValue(fConditionCombo.getText(), ConstraintViewer.this, getUpdateListener());
				fConditionCombo.setText(fStatementIf.getConditionValue());
			}

		}
		
		private class StatementComboListener implements SelectionListener{
			@Override
			public void widgetSelected(SelectionEvent e) {
				Operator operator = Operator.getOperator(fStatementCombo.getText());
				if(operator != null && operator != fStatementIf.getOperator()){
					fStatementIf.setOperator(operator, ConstraintViewer.this, getUpdateListener());
				}
				BasicStatement statement = buildStatement();
				if(statement != null){
					BasicStatementInterface parentIf = fStatementIf.getParentInterface();
					boolean result = false;
					if(parentIf != null){
						result = parentIf.replaceChild(fSelectedStatement, statement, ConstraintViewer.this, getUpdateListener());
					}
					else{
						result = fConstraintIf.replaceStatement(fSelectedStatement, statement, ConstraintViewer.this, getUpdateListener());
					}
					if(result){
						getViewer().setSelection(new StructuredSelection(statement));

					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("widgetDefaultSelected(" + e + ")");
				widgetSelected(e);
			}

			private BasicStatement buildStatement() {
				String statementText = fStatementCombo.getText();
				if(statementText.equals(STATEMENT_TRUE) || statementText.equals(STATEMENT_FALSE)){
					return new StaticStatement(Boolean.parseBoolean(statementText));
				}
				if(statementText.equals(STATEMENT_AND) || statementText.equals(STATEMENT_OR)){
					return new StatementArray(Operator.getOperator(statementText));
				}
				CategoryNode category = fConstraint.getMethod().getCategory(statementText);
				Relation relation = Relation.EQUAL;
				if(category != null && category.isExpected()){
					PartitionNode condition = new PartitionNode("expected", category.getDefaultValue());
					condition.setParent(category);
					return new ExpectedValueStatement(category, condition);
				}
				else if(category != null && category.getPartitions().size() > 0){
					PartitionNode condition = category.getPartitions().get(0);
					return new PartitionedCategoryStatement(category, relation, condition);
				}
			
				return null;
			}
		}

		private class RelationComboListener implements SelectionListener{

			@Override
			public void widgetSelected(SelectionEvent e) {
				Relation relation = Relation.getRelation(fRelationCombo.getText());
				fStatementIf.setRelation(relation, ConstraintViewer.this, getUpdateListener());
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
				if(CategoryInterface.hasLimitedValuesSet(statement.getCategory())){
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
			public Object visit(PartitionedCategoryStatement statement) throws Exception {
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
			public Object visit(PartitionCondition condition) throws Exception {
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
				for(Relation r : statement.getAvailableRelations()){
					relations.add(r.toString());
				}
				return relations.toArray(new String[]{});
			}

			private String[] availableConditions(ExpectedValueStatement statement) {
				CategoryNode category  = statement.getCategory();
				List<String> values = CategoryInterface.getSpecialValues(category.getType());
				if(values.isEmpty()){
					for(PartitionNode p : category.getLeafPartitions()){
						values.add(p.getValueString());
					}
				}
				return values.toArray(new String[]{});
			}

			private String[] availableConditions(PartitionedCategoryStatement statement) {
				List<String> result = new ArrayList<String>();
				CategoryNode category = statement.getCategory();
				result.addAll(category.getAllPartitionNames());
				result.addAll(category.getLeafLabels());
				return result.toArray(new String[]{});
			}
		}

		public StatementEditor(Composite parent, ModelOperationManager operationManager) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout(TOTAL_EDITOR_WIDTH, true));
			setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			fInterfaceFactory = new StatementInterfaceFactory(operationManager);
			fConstraintIf = new ConstraintInterface(operationManager);
			
			createStatementCombo(this);
			createRelationCombo(this);
		}
		
		public void setInput(BasicStatement statement){
			fStatementIf = fInterfaceFactory.getInterface(statement);
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

		private String[] statementComboItems(BasicStatement statement){
			List<String> items = new ArrayList<String>();
			items.addAll(Arrays.asList(FIXED_STATEMENTS));
			boolean consequence = fConstraint.getConstraint().getConsequence() == statement;
			for(CategoryNode c : fConstraint.getMethod().getCategories()){
				if(c.isExpected()){
					if(consequence){
						items.add(c.getName());
					}
				}
				else{
					if(c.getPartitions().size() > 0){
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
			BasicStatement statement = fStatementIf.addNewStatement(ConstraintViewer.this, getUpdateListener());
			if(statement != null){
			//modelUpdated must be called before to refresh viewer before selecting the newly added statement
				getTreeViewer().expandToLevel(statement, 1);
				getTreeViewer().setSelection(new StructuredSelection(statement));
			}
		}
	}
	
	private class RemoveStatementAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			if(fStatementIf.getParentInterface() != null){
				BasicStatement parent = fSelectedStatement.getParent();
				if(parent != null && fStatementIf.getParentInterface().removeChild(fSelectedStatement, ConstraintViewer.this, getUpdateListener())){
					getViewer().setSelection(new StructuredSelection(parent));
				}
			}
		}
	}

	private class StatementSelectionListener implements ISelectionChangedListener{

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			BasicStatement statement = (BasicStatement)((StructuredSelection)event.getSelection()).getFirstElement();
			if(statement != null){
				fSelectedStatement = statement;
				fStatementEditor.setInput(statement);
				updateSideButtons(statement);
			}
		}

		private void updateSideButtons(BasicStatement selectedStatement) {
			boolean enableAddStatementButton = (selectedStatement instanceof StatementArray || selectedStatement.getParent() != null);
			boolean enableRemoveStatementButton = (selectedStatement.getParent() != null);
			fAddStatementButton.setEnabled(enableAddStatementButton);
			fRemoveStatementButton.setEnabled(enableRemoveStatementButton);
		}

	}

	public ConstraintViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		getSection().setText("Constraint editor");
		fAddStatementButton = addButton("Add statement", new AddStatementAdapter());
		fRemoveStatementButton = addButton("Remove statement", new RemoveStatementAdapter());
		getViewer().addSelectionChangedListener(new StatementSelectionListener());

		fStatementEditor = new StatementEditor(getClientComposite(), parent.getOperationManager());
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

	public void setInput(ConstraintNode constraintNode){
		//Update the statement provider before setting input to get the correct images
		fCurrentConstraint = constraintNode.getConstraint();
		super.setInput(constraintNode.getConstraint());
		fStatementEditor.setConstraint(constraintNode);

		getTreeViewer().expandAll();
		if(getSelectedElement() == null){
			getViewer().setSelection(new StructuredSelection(constraintNode.getConstraint().getPremise()));
		}
	}
}
