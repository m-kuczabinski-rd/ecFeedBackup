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

package com.testify.ecfeed.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.testify.ecfeed.generators.DoubleParameter;
import com.testify.ecfeed.generators.GeneratorFactory;
import com.testify.ecfeed.generators.IntegerParameter;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.generators.api.IGeneratorParameter;
import com.testify.ecfeed.generators.api.IGeneratorParameter.TYPE;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;

public class GeneratorSetupDialog extends TitleAreaDialog {
	private Combo fTestSuiteCombo;
	private Combo fGeneratorCombo;
	private Button fOkButton;
	private MethodNode fMethod;
	private String fTestSuiteName;
	private CheckboxTreeViewer fCategoriesViewer;
	private CheckboxTreeViewer fConstraintsViewer;
	private List<List<PartitionNode>> fAlgorithmInput;
	private Collection<IConstraint<PartitionNode>> fConstraints;
	private IGenerator<PartitionNode> fSelectedGenerator;
	private Map<String, Object> fParameters;
	private Composite fParametersComposite;
	private Composite fMainContainer;
	private GeneratorFactory<PartitionNode> fGeneratorFactory; 
	private int fContent;
	
	private final String fTitle;
	private final String fMessage;
	
	public final static int CONSTRAINTS_COMPOSITE = 1;
	public final static int PARTITIONS_COMPOSITE = 1 << 1;
	public final static int TEST_SUITE_NAME_COMPOSITE = 1 << 2;
	public final static int GENERATOR_SELECTION_COMPOSITE = 1 << 3;

	private class CategoriesContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
		private final Object[] EMPTY_ARRAY = new Object[]{};
		
		@Override
		public Object[] getElements(Object input){
			if(input instanceof MethodNode){
				return ((MethodNode)input).getCategories().toArray();
			}
			return null;
		}
		
		public Object[] getChildren(Object element){
			if(element instanceof CategoryNode){
				return ((CategoryNode)element).getPartitions().toArray();
			}
			if(element instanceof PartitionNode){
				return ((PartitionNode)element).getPartitions().toArray();
			}
			return EMPTY_ARRAY;
		}
		
		@Override
		public Object getParent(Object element){
			if(element instanceof GenericNode){
				return ((GenericNode)element).getParent();
			}
			return null;
		}
		
		@Override
		public boolean hasChildren(Object element){
			return getChildren(element).length > 0;
		}
	}
	
	private class ConstraintsViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
		private final Object[] EMPTY_ARRAY = new Object[]{};
		
		@Override
		public Object[] getElements(Object input){
			if(input instanceof MethodNode){
				return fMethod.getConstraintsNames().toArray();
			}
			return EMPTY_ARRAY;
		}
		
		public Object[] getChildren(Object element){
			if(element instanceof String){
				Object[] result = fMethod.getConstraints((String)element).toArray(); 
				return result;
			}
			return EMPTY_ARRAY;
		}
		
		@Override
		public Object getParent(Object element){
			if(element instanceof ConstraintNode){
				return ((ConstraintNode)element).getName();
			}
			return null;
		}
		
		@Override
		public boolean hasChildren(Object element){
			return getChildren(element).length > 0;
		}
	}
	
	public GeneratorSetupDialog(Shell parentShell, MethodNode method, int content, String title, String message) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		fMethod = method;
		fGeneratorFactory = new GeneratorFactory<PartitionNode>();
		fContent = content;
		fTitle = title;
		fMessage = message;
	}
	
	protected  List<List<PartitionNode>> algorithmInput(){
		return fAlgorithmInput;
	}

	protected  Collection<IConstraint<PartitionNode>> constraints(){
		return fConstraints;
	}

	protected  String testSuiteName(){
		return fTestSuiteName;
	}

	protected  IGenerator<PartitionNode> selectedGenerator() {
		return fSelectedGenerator;
	}

	protected  Map<String, Object> generatorParameters() {
		return fParameters;
	}

	@Override
	public Point getInitialSize(){
		return new Point(600, 800);
	}

	@Override
	public void okPressed(){
		saveAlgorithmInput();
		saveConstraints();
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(fTitle);
		setMessage(fMessage);
		Composite area = (Composite) super.createDialogArea(parent);
		fMainContainer = new Composite(area, SWT.NONE);
		fMainContainer.setLayout(new GridLayout(1, false));
		fMainContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if((fContent & CONSTRAINTS_COMPOSITE) > 0){
			createConstraintsComposite(fMainContainer);
		}
		
		if((fContent & PARTITIONS_COMPOSITE) > 0){
			createPartitionsComposite(fMainContainer);
		}
		
		if((fContent & TEST_SUITE_NAME_COMPOSITE) > 0){
			createTestSuiteComposite(fMainContainer);
		}
		
		if((fContent & GENERATOR_SELECTION_COMPOSITE) > 0){
			createGeneratorSelectionComposite(fMainContainer);
		}		
		return area;
	}

	private void createConstraintsComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label selectConstraintsLabel = new Label(composite, SWT.NONE);
		selectConstraintsLabel.setText(Messages.DIALOG_GENERATE_TEST_SUITES_SELECT_CONSTRAINTS_LABEL);
		
		createConstraintsViewer(composite);
		
		createConstraintsButtons(composite);
	}

	private void createConstraintsViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.CHECK|SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fConstraintsViewer = new CheckboxTreeViewer(tree);
		fConstraintsViewer.setContentProvider(new ConstraintsViewerContentProvider());
		fConstraintsViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof String){
					return (String)element;
				}
				if(element instanceof Constraint){
					return ((Constraint)element).toString();
				}
				return null;
			}
		});
		fConstraintsViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fConstraintsViewer.setInput(fMethod);
		fConstraintsViewer.addCheckStateListener(new TreeCheckStateListener(fConstraintsViewer));
	}

	private void createConstraintsButtons(Composite parent) {
		Composite buttonsComposite = new Composite(parent, SWT.NONE);
		buttonsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button checkAllButton = new Button(buttonsComposite, SWT.NONE);
		checkAllButton.setText("Check all");
		checkAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				for(String name : fMethod.getConstraintsNames()){
					fConstraintsViewer.setSubtreeChecked(name, true);
				}
			}
		});
		
		Button uncheckAllButton = new Button(buttonsComposite, SWT.NONE);
		uncheckAllButton.setText("Uncheck all");
		uncheckAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				for(String name : fMethod.getConstraintsNames()){
					fConstraintsViewer.setSubtreeChecked(name, false);
				}
			}
		});
	}

	private void createPartitionsComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label selectPartitionsLabel = new Label(composite, SWT.WRAP);
		selectPartitionsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		selectPartitionsLabel.setText(Messages.DIALOG_GENERATE_TEST_SUITES_SELECT_PARTITIONS_LABEL + "cycki");
		
		createPartitionsViewer(composite);
	}

	private void createPartitionsViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.CHECK|SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fCategoriesViewer = new CheckboxTreeViewer(tree);
		fCategoriesViewer.setContentProvider(new CategoriesContentProvider());
		fCategoriesViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof GenericNode){
					return ((GenericNode)element).getName();
				}
				return null;
			}
		});
		fCategoriesViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fCategoriesViewer.setInput(fMethod);
		for(CategoryNode category : fMethod.getCategories()){
			fCategoriesViewer.setSubtreeChecked(category, true);
		}
		fCategoriesViewer.addCheckStateListener(new TreeCheckStateListener(fCategoriesViewer));
		fCategoriesViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				for(CategoryNode category : fMethod.getCategories()){
					if(fCategoriesViewer.getChecked(category) == false){
						setOkButton(false);
						return;
					}
					setOkButton(true);
				}
			}
		});
	}

	private void createTestSuiteComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label testSuiteLabel = new Label(composite, SWT.NONE);
		testSuiteLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		testSuiteLabel.setText("Test suite");
		
		ComboViewer testSuiteViewer = new ComboViewer(composite, SWT.NONE);
		fTestSuiteCombo = testSuiteViewer.getCombo();
		fTestSuiteCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fTestSuiteCombo.setItems(fMethod.getTestSuites().toArray(new String[]{}));
		fTestSuiteCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validateTestSuiteName();
			}
		});
		fTestSuiteCombo.setText(Constants.DEFAULT_TEST_SUITE_NAME);
	}

	private void validateTestSuiteName() {
		if(!TestCaseNode.validateTestSuiteName(fTestSuiteCombo.getText())){
			setErrorMessage(Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
			setOkButton(false);
		}
		else{
			setErrorMessage(null);
			setOkButton(true);
			fTestSuiteName = fTestSuiteCombo.getText();
		}
	}

	private void setOkButton(boolean enabled) {
		if(fOkButton != null && !fOkButton.isDisposed()){
			fOkButton.setEnabled(enabled);
		}
	}

	private void createGeneratorSelectionComposite(Composite container) {
		Composite generatorComposite = new Composite(container, SWT.NONE);
		generatorComposite.setLayout(new GridLayout(2, false));
		generatorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label generatorLabel = new Label(generatorComposite, SWT.NONE);
		generatorLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		generatorLabel.setText("Generator");
		
		createGeneratorViewer(generatorComposite);
	}

	private void createGeneratorViewer(final Composite parent) {
		ComboViewer generatorViewer = new ComboViewer(parent, SWT.READ_ONLY);
		fGeneratorCombo = generatorViewer.getCombo();
		fGeneratorCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fGeneratorCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					fSelectedGenerator = fGeneratorFactory.getGenerator(fGeneratorCombo.getText());
					createParametersComposite(parent, fSelectedGenerator.parameters());
					fMainContainer.layout();
				} catch (GeneratorException exception) {
					exception.printStackTrace();
					fGeneratorCombo.setText("");
				}
			}
		});
		if(fGeneratorFactory.availableGenerators().size() > 0){
			String[] availableGenerators = fGeneratorFactory.availableGenerators().toArray(new String[]{}); 
			fGeneratorCombo.setItems(availableGenerators);
			fGeneratorCombo.setText(availableGenerators[0]);
			setOkButton(true);
		}
	}

	private void createParametersComposite(Composite parent, List<IGeneratorParameter> parameters) {
		fParameters = new HashMap<String, Object>();
		if(fParametersComposite != null && !fParametersComposite.isDisposed()){
			fParametersComposite.dispose();
		}
		fParametersComposite = new Composite(parent, SWT.NONE);
		fParametersComposite.setLayout(new GridLayout(2, false));
		fParametersComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		for(IGeneratorParameter parameter : parameters){
			createParameterEdit(fParametersComposite, parameter);
		}
		parent.layout();
	}

	private void createParameterEdit(Composite parent, IGeneratorParameter definition) {
		fParameters.put(definition.getName(), definition.defaultValue());
		if(definition.getType() == TYPE.BOOLEAN){
			createBooleanParameterEdit(parent, definition);
		}
		else{
			new Label(parent, SWT.LEFT).setText(definition.getName());
			if(definition.allowedValues() != null){
				createComboParameterEdit(parent, definition);
			}
			else{
				switch(definition.getType()){
				case INTEGER:
					createIntegerParameterEdit(parent, (IntegerParameter)definition);
					break;
				case DOUBLE:
					createDoubleParameterEdit(parent, (DoubleParameter)definition);
					break;
				case STRING:
					createStringParameterEdit(parent, definition);
					break;
				default:
					break;
				}
			}
		}
	}

	private void createBooleanParameterEdit(Composite parent,
			final IGeneratorParameter definition) {
		final Button checkButton = new Button(parent, SWT.CHECK);
		checkButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		checkButton.setText(definition.getName());
		checkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				fParameters.put(definition.getName(), checkButton.getSelection());
			}
		});
		checkButton.pack();
	}

	private void createComboParameterEdit(Composite parent,
			final IGeneratorParameter definition){
		final Combo combo = new Combo(parent, SWT.CENTER|SWT.READ_ONLY);
		ModifyListener listener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				switch(definition.getType()){
				case INTEGER:
					fParameters.put(definition.getName(), Integer.parseInt(combo.getText()));
					break;
				case DOUBLE:
					fParameters.put(definition.getName(), Double.parseDouble(combo.getText()));
					break;
				case STRING:
					fParameters.put(definition.getName(), combo.getText());
					break;
				default:
					break;
				}
			}
		};
		combo.addModifyListener(listener);
		combo.setItems(allowedValuesItems(definition));
		combo.setText(definition.defaultValue().toString());
	}
	
	private String[] allowedValuesItems(IGeneratorParameter definition) {
		List<String> values = new ArrayList<String>();
		for(Object value : definition.allowedValues()){
			values.add(value.toString());
		}
		return values.toArray(new String[]{});
	}

	private void createIntegerParameterEdit(Composite parent,
			final IntegerParameter definition) {
		final Spinner spinner = new Spinner(parent, SWT.BORDER|SWT.RIGHT);
		spinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fParameters.put(definition.getName(), spinner.getSelection());
			}
		});
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		spinner.setValues((int)definition.defaultValue(), definition.getMin(), definition.getMax(), 0, 1, 1);
	}

	private void createDoubleParameterEdit(Composite parent,
			final DoubleParameter definition) {
		final Spinner spinner = new Spinner(parent, SWT.BORDER);
		final int FLOAT_DECIMAL_PLACES = 3;
		spinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				int selection = spinner.getSelection();
				int digits = spinner.getDigits();
				fParameters.put(definition.getName(), selection/(Math.pow(10, digits)));
			}
		});
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		int factor = (int) Math.pow(10, FLOAT_DECIMAL_PLACES);
		int defaultValue = (int)Math.round((double)definition.defaultValue() * factor);
		int minValue = (int)Math.round((double)definition.getMin() * factor);
		int maxValue = (int)Math.round((double)definition.getMax());
		spinner.setValues(defaultValue, minValue, maxValue, FLOAT_DECIMAL_PLACES, 1, 100);
	}

	private void createStringParameterEdit(Composite parent,
			final IGeneratorParameter definition) {
		final Text text = new Text(parent, SWT.NONE);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fParameters.put(definition.getName(), text.getText());
			}
		});
		text.setText((String)definition.defaultValue());
	}

	private void saveConstraints() {
		Object[] checkedObjects = fConstraintsViewer.getCheckedElements();
		List<IConstraint<PartitionNode>> constraints = new ArrayList<IConstraint<PartitionNode>>();
		for(Object obj : checkedObjects){
			if(obj instanceof Constraint){
				constraints.add((Constraint)obj);
			}
		}
		
		fConstraints = constraints;
	}

	private void saveAlgorithmInput() {
		List<CategoryNode> categories = fMethod.getCategories();
		fAlgorithmInput = new ArrayList<List<PartitionNode>>();
		for(int i = 0; i < categories.size(); i++){
			List<PartitionNode> partitions = new ArrayList<PartitionNode>();
			if(categories.get(i) instanceof ExpectedValueCategoryNode){
				ExpectedValueCategoryNode category = (ExpectedValueCategoryNode)categories.get(i);
				partitions.add(category.getDefaultValuePartition());
			}
			else{
				for(PartitionNode partition : categories.get(i).getLeafPartitions()){
					if(fCategoriesViewer.getChecked(partition)){
						partitions.add(partition);
					}
				}
			}
			fAlgorithmInput.add(partitions);
		}
	}

}
