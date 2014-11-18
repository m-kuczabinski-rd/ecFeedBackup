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

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IImplementationStatusResolver;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.generators.DoubleParameter;
import com.testify.ecfeed.generators.GeneratorFactory;
import com.testify.ecfeed.generators.IntegerParameter;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.generators.api.IGeneratorParameter;
import com.testify.ecfeed.generators.api.IGeneratorParameter.TYPE;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.DecomposedNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;

public class GeneratorSetupDialog extends TitleAreaDialog {
	private Combo fTestSuiteCombo;
	private Combo fGeneratorCombo;
	private Button fOkButton;
	private MethodNode fMethod;
	private String fTestSuiteName;
	private CheckboxTreeViewer fParametersViewer;
	private CheckboxTreeViewer fConstraintsViewer;
	private List<List<ChoiceNode>> fAlgorithmInput;
	private Collection<Constraint> fConstraints;
	private IGenerator<ChoiceNode> fSelectedGenerator;
	private Map<String, Object> fParameters;
	private Composite fParametersComposite;
	private Composite fMainContainer;
	private GeneratorFactory<ChoiceNode> fGeneratorFactory; 
	private int fContent;
	private boolean fGenerateExecutableContent;
	private IImplementationStatusResolver fStatusResolver;
	
	private final String fTitle;
	private final String fMessage;
	
	public final static int CONSTRAINTS_COMPOSITE = 1;
	public final static int PARTITIONS_COMPOSITE = 1 << 1;
	public final static int TEST_SUITE_NAME_COMPOSITE = 1 << 2;
	public final static int GENERATOR_SELECTION_COMPOSITE = 1 << 3;
	
	private class PartitionTreeCheckStateListener extends TreeCheckStateListener{

		public PartitionTreeCheckStateListener(CheckboxTreeViewer treeViewer) {
			super(treeViewer);
		}

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			super.checkStateChanged(event);
			if(event.getElement() instanceof ParameterNode && ((ParameterNode)event.getElement()).isExpected()){
				fParametersViewer.setChecked(event.getElement(), true);
			}
			else{
				updateOkButton();
			}
		}
	}
	
	private class ParametersContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
		@Override
		public Object[] getElements(Object input){
			if(input instanceof MethodNode){
				return ((MethodNode)input).getParameters().toArray();
			}
			return null;
		}
		
		public Object[] getChildren(Object element){
			List<Object> children = new ArrayList<Object>();
			if(element instanceof ParameterNode && ((ParameterNode)element).isExpected()){
			}
			else if(element instanceof DecomposedNode){
				DecomposedNode parent = (DecomposedNode)element;
				if(fGenerateExecutableContent == false){
					children.addAll(parent.getPartitions());
				}
				else{
					for(ChoiceNode child : parent.getPartitions()){
						if(fStatusResolver.getImplementationStatus(child) != EImplementationStatus.NOT_IMPLEMENTED){
							children.add(child);
						}
					}
				}
			}
			return children.toArray();
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
	
	public GeneratorSetupDialog(Shell parentShell, MethodNode method, int content, String title, String message, boolean generateExecutables) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		fMethod = method;
		fGeneratorFactory = new GeneratorFactory<ChoiceNode>();
		fContent = content;
		fTitle = title;
		fMessage = message;
		fGenerateExecutableContent = generateExecutables;
		fStatusResolver = new EclipseImplementationStatusResolver();
	}
	
	protected  List<List<ChoiceNode>> algorithmInput(){
		return fAlgorithmInput;
	}

	protected  Collection<Constraint> constraints(){
		return fConstraints;
	}

	protected  String testSuiteName(){
		return fTestSuiteName;
	}

	protected  IGenerator<ChoiceNode> selectedGenerator() {
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
		if(fGenerateExecutableContent){
			for(ParameterNode parameter: fMethod.getParameters()){
				EImplementationStatus parameterStatus = fStatusResolver.getImplementationStatus(parameter);
				if((parameter.getPartitions().isEmpty() && (parameter.isExpected() == false || JavaUtils.isUserType(parameter.getType())))||
						parameterStatus == EImplementationStatus.NOT_IMPLEMENTED){
					setOkButton(false);
					break;
				}
			}
		} else {
			for(ParameterNode parameter: fMethod.getParameters() ){
				if(parameter.getPartitions().isEmpty() && (parameter.isExpected() == false || JavaUtils.isUserType(parameter.getType()))){
					setOkButton(false);
					break;
				}
			}
		}
		
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
		fConstraintsViewer.expandAll();
		for(String constraint : fMethod.getConstraintsNames()){
			fConstraintsViewer.setSubtreeChecked(constraint, true);
		}
		fConstraintsViewer.collapseAll();
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
		selectPartitionsLabel.setText(Messages.DIALOG_GENERATE_TEST_SUITES_SELECT_CHOICES_LABEL);
		
		createPartitionsViewer(composite);
	}

	private void createPartitionsViewer(Composite parent) {
		final Tree tree = new Tree(parent, SWT.CHECK|SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fParametersViewer = new CheckboxTreeViewer(tree);
		fParametersViewer.setContentProvider(new ParametersContentProvider());
		fParametersViewer.setLabelProvider(new NodeNameColumnLabelProvider());
		fParametersViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fParametersViewer.setInput(fMethod);
		fParametersViewer.addCheckStateListener(new PartitionTreeCheckStateListener(fParametersViewer));
		for(ParameterNode parameter : fMethod.getParameters()){
			fParametersViewer.expandAll();
			fParametersViewer.setSubtreeChecked(parameter, true);
			fParametersViewer.collapseAll();
		}
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
				updateOkButton();
			}
		});
		fTestSuiteCombo.setText(Constants.DEFAULT_NEW_TEST_SUITE_NAME);
	}

	private void updateOkButton() {
		boolean okEnabled = true;
		if((okEnabled = validateTestSuiteName()) == false){
			setErrorMessage(Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
		}
		if((okEnabled &= validateGeneratorInput(fGenerateExecutableContent)) == false){
			if(fGenerateExecutableContent){
				setErrorMessage(Messages.DIALOG_GENERATOR_EXECUTABLE_INPUT_PROBLEM_MESSAGE);
			}
			else{
				setErrorMessage(Messages.DIALOG_GENERATOR_INPUT_PROBLEM_MESSAGE);
			}
		}
		if(okEnabled){
			setErrorMessage(null);
		}
		setOkButton(okEnabled);
	}

	private boolean validateGeneratorInput(boolean onlyExecutable) {
		for(ParameterNode parameter : fMethod.getParameters()){
			boolean leafChecked = false;
			if(parameter.isExpected()){
				if(fParametersViewer.getChecked(parameter) == false){
					return false;
				}
				continue;
			}
			for(ChoiceNode leaf : parameter.getLeafPartitions()){
				leafChecked |= fParametersViewer.getChecked(leaf);
				EImplementationStatus status = fStatusResolver.getImplementationStatus(leaf);
				if(status != EImplementationStatus.IMPLEMENTED && onlyExecutable){
					return false;
				}
			}
			if(leafChecked == false){
				return false;
			}
		}
		return true;
	}

	private boolean validateTestSuiteName() {
		boolean testSuiteValid = true;
		if(fTestSuiteCombo != null && fTestSuiteCombo.isDisposed() == false){
			testSuiteValid = JavaUtils.isValidTestCaseName(fTestSuiteCombo.getText());
			if(testSuiteValid){
				fTestSuiteName = fTestSuiteCombo.getText();
			}
		}
		return testSuiteValid;
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
		final GeneratorFactory<ChoiceNode> generatorFactory = new GeneratorFactory<>();
		ComboViewer generatorViewer = new ComboViewer(parent, SWT.READ_ONLY);
		fGeneratorCombo = generatorViewer.getCombo();
		fGeneratorCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fGeneratorCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					fSelectedGenerator = generatorFactory.getGenerator(fGeneratorCombo.getText());
					createParametersComposite(parent, fSelectedGenerator.parameters());
					fMainContainer.layout();
				} catch (GeneratorException exception) {
					exception.printStackTrace();
					fGeneratorCombo.setText("");
				}
			}
		});
		if(fGeneratorFactory.availableGenerators().size() > 0){
			String[] availableGenerators = generatorFactory.availableGenerators().toArray(new String[] {});
			for(String generator : availableGenerators){
				fGeneratorCombo.add(generator);
			}
			fGeneratorCombo.select(0);
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
		combo.setItems(allowedValuesItems(definition));
		combo.setText(definition.defaultValue().toString());
		combo.addModifyListener(listener);
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
		List<Constraint> constraints = new ArrayList<Constraint>();
		for(Object obj : checkedObjects){
			if(obj instanceof Constraint){
				constraints.add((Constraint)obj);
			}
		}
		
		fConstraints = constraints;
	}

	private void saveAlgorithmInput() {
		List<ParameterNode> parameters = fMethod.getParameters();
		fAlgorithmInput = new ArrayList<List<ChoiceNode>>();
		for(int i = 0; i < parameters.size(); i++){
			List<ChoiceNode> partitions = new ArrayList<ChoiceNode>();
			if(parameters.get(i).isExpected()){
				partitions.add(expectedValuePartition(parameters.get(i)));
			}
			else{
				for(ChoiceNode partition : parameters.get(i).getLeafPartitions()){
					if(fParametersViewer.getChecked(partition)){
						partitions.add(partition);
					}
				}
			}
			fAlgorithmInput.add(partitions);
		}
	}

	private ChoiceNode expectedValuePartition(ParameterNode c){
		ChoiceNode p = new ChoiceNode("", c.getDefaultValue());
		p.setParent(c);
		return p;
	}
}
