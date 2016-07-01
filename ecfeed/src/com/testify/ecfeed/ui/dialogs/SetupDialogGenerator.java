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

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.IImplementationStatusResolver;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.generators.DoubleParameter;
import com.ecfeed.core.generators.GeneratorFactory;
import com.ecfeed.core.generators.IntegerParameter;
import com.ecfeed.core.generators.NWiseGenerator;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.IGeneratorParameter;
import com.ecfeed.core.generators.api.IGeneratorParameter.TYPE;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.StringHolder;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.NodeNameColumnLabelProvider;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.ui.dialogs.TestCasesExportDialog.FileCompositeVisibility;

public abstract class SetupDialogGenerator extends TitleAreaDialog {
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
	private boolean fGenerateExecutableContent;
	private IImplementationStatusResolver fStatusResolver;
	private IFileInfoProvider fFileInfoProvider;
	private DialogObjectToolkit fDialogObjectToolkit;
	private Text fTargetFileText;
	private int fContent;
	private String fTargetFile;

	String fExportTemplate;

	public final static int CONSTRAINTS_COMPOSITE = 1;
	public final static int CHOICES_COMPOSITE = 1 << 1;
	public final static int TEST_SUITE_NAME_COMPOSITE = 1 << 2;
	public final static int GENERATOR_SELECTION_COMPOSITE = 1 << 3;
	public final static int TEST_CASES_EXPORT_COMPOSITE = 1 << 4;

	public SetupDialogGenerator(Shell parentShell, MethodNode method,
			boolean generateExecutables, IFileInfoProvider fileInfoProvider,
			String initialExportTemplate,
			String targetFile) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE
				| SWT.APPLICATION_MODAL);
		fMethod = method;
		fGeneratorFactory = new GeneratorFactory<ChoiceNode>();
		fGenerateExecutableContent = generateExecutables;
		fStatusResolver = new EclipseImplementationStatusResolver(
				fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fDialogObjectToolkit = DialogObjectToolkit.getInstance();

		fTargetFile = null;
		fExportTemplate = initialExportTemplate;
		fTargetFile = targetFile;
	}

	protected abstract String getDialogTitle();

	protected abstract int getContent();

	public List<List<ChoiceNode>> getAlgorithmInput() {
		return fAlgorithmInput;
	}

	public Collection<Constraint> getConstraints() {
		return fConstraints;
	}

	public String getTestSuiteName() {
		return fTestSuiteName;
	}

	public IGenerator<ChoiceNode> getSelectedGenerator() {
		return fSelectedGenerator;
	}

	public Map<String, Object> getGeneratorParameters() {
		return fParameters;
	}

	@Override
	public Point getInitialSize() {
		return new Point(600, 800);
	}

	@Override
	public void okPressed() {
		if (fTargetFileText != null) {
			fTargetFile = fTargetFileText.getText();
			
			if (!TestCasesExportDialog.canOverwriteFile(fTargetFile)) {
				return;
			}
			
		} else {
			fTargetFile = null;
		}

		saveAlgorithmInput();
		saveConstraints();
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		if (fGenerateExecutableContent) {
			for (MethodParameterNode parameter : fMethod.getMethodParameters()) {
				EImplementationStatus parameterStatus = fStatusResolver
						.getImplementationStatus(parameter);
				if ((parameter.getChoices().isEmpty() && (parameter
						.isExpected() == false || JavaUtils
						.isUserType(parameter.getType())))
						|| parameterStatus == EImplementationStatus.NOT_IMPLEMENTED) {
					setOkButtonStatus(false);
					break;
				}
			}
		} else {
			for (MethodParameterNode parameter : fMethod.getMethodParameters()) {
				if (parameter.getChoices().isEmpty()
						&& (parameter.isExpected() == false || JavaUtils
						.isUserType(parameter.getType()))) {
					setOkButtonStatus(false);
					break;
				}
			}
		}

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

		updateOkButtonAndErrorMsg();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(getDialogTitle());
		setMessage(getDialogMessage());
		Composite area = (Composite) super.createDialogArea(parent);
		fMainContainer = new Composite(area, SWT.NONE);
		fMainContainer.setLayout(new GridLayout(1, false));
		fMainContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

		createContent();
		return area;
	}

	private void createContent() {
		fContent = getContent();

		if (isContentFlagOn(CONSTRAINTS_COMPOSITE)) {
			createConstraintsComposite(fMainContainer);
		}
		if (isContentFlagOn(CHOICES_COMPOSITE)) {
			createChoicesComposite(fMainContainer);
		}
		if (isContentFlagOn(TEST_SUITE_NAME_COMPOSITE)) {
			createTestSuiteComposite(fMainContainer);
		}
		if (isContentFlagOn(GENERATOR_SELECTION_COMPOSITE)) {
			createGeneratorSelectionComposite(fMainContainer);
		}
		if (isContentFlagOn(TEST_CASES_EXPORT_COMPOSITE)) {
			createTestCasesExportComposite(fMainContainer);
		}
	}

	protected String getDialogMessage() {
		final String DIALOG_GENERATE_TEST_SUITE_MESSAGE = "Configure test data generation.";
		return DIALOG_GENERATE_TEST_SUITE_MESSAGE;
	}

	private boolean isContentFlagOn(int flag) {

		if ((fContent & flag) > 0) {
			return true;
		}
		return false;
	}

	private void createConstraintsComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label selectConstraintsLabel = new Label(composite, SWT.NONE);
		selectConstraintsLabel
		.setText(Messages.DIALOG_GENERATE_TEST_SUITES_SELECT_CONSTRAINTS_LABEL);

		createConstraintsViewer(composite);

		createConstraintsButtons(composite);
	}

	private void createConstraintsViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.CHECK | SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fConstraintsViewer = new CheckboxTreeViewer(tree);
		fConstraintsViewer
		.setContentProvider(new ConstraintsViewerContentProvider());
		fConstraintsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					return (String) element;
				}
				if (element instanceof Constraint) {
					return ((Constraint) element).toString();
				}
				return null;
			}
		});
		fConstraintsViewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		fConstraintsViewer.setInput(fMethod);
		fConstraintsViewer.addCheckStateListener(new TreeCheckStateListener(
				fConstraintsViewer));
		fConstraintsViewer.expandAll();
		for (String constraint : fMethod.getConstraintsNames()) {
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
			public void widgetSelected(SelectionEvent e) {
				for (String name : fMethod.getConstraintsNames()) {
					fConstraintsViewer.setSubtreeChecked(name, true);
				}
			}
		});

		Button uncheckAllButton = new Button(buttonsComposite, SWT.NONE);
		uncheckAllButton.setText("Uncheck all");
		uncheckAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (String name : fMethod.getConstraintsNames()) {
					fConstraintsViewer.setSubtreeChecked(name, false);
				}
			}
		});
	}

	private void createChoicesComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label selectChoicesLabel = new Label(composite, SWT.WRAP);
		selectChoicesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));
		selectChoicesLabel
		.setText(Messages.DIALOG_GENERATE_TEST_SUITES_SELECT_CHOICES_LABEL);

		createChoicesViewer(composite);
	}

	private void createChoicesViewer(Composite parent) {
		final Tree tree = new Tree(parent, SWT.CHECK | SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fParametersViewer = new CheckboxTreeViewer(tree);
		fParametersViewer.setContentProvider(new ParametersContentProvider());
		fParametersViewer.setLabelProvider(new NodeNameColumnLabelProvider());
		fParametersViewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		fParametersViewer.setInput(fMethod);
		fParametersViewer
		.addCheckStateListener(new ChoiceTreeCheckStateListener(
				fParametersViewer));
		for (MethodParameterNode parameter : fMethod.getMethodParameters()) {
			fParametersViewer.expandAll();
			fParametersViewer.setSubtreeChecked(parameter, true);
			fParametersViewer.collapseAll();
		}
	}

	private void createTestSuiteComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label testSuiteLabel = new Label(composite, SWT.NONE);
		testSuiteLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		testSuiteLabel.setText("Test suite");

		ComboViewer testSuiteViewer = new ComboViewer(composite, SWT.NONE);
		fTestSuiteCombo = testSuiteViewer.getCombo();
		fTestSuiteCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		fTestSuiteCombo.setItems(fMethod.getTestSuites().toArray(
				new String[] {}));
		fTestSuiteCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateOkButtonAndErrorMsg();
			}
		});
		fTestSuiteCombo.setText(Constants.DEFAULT_NEW_TEST_SUITE_NAME);
	}

	private void updateOkButtonAndErrorMsg() {
		StringHolder message = new StringHolder();

		if (validateDialogFields(message)) {
			setOkButtonStatus(true);
			setErrorMessage(null);
			return;
		}
		setOkButtonStatus(false);
		setErrorMessage(message.get());
	}

	private boolean validateDialogFields(StringHolder message) {
		if (!validateTestSuiteName(message)) {
			return false;
		}
		if (!validateMethodParameters(fGenerateExecutableContent, message)) {
			return false;
		}
		if (!validateTargetFileText(message)) {
			return false;
		}
		return true;
	}

	private boolean validateTestSuiteName(StringHolder message) {
		boolean testSuiteValid = true;

		if (fTestSuiteCombo != null && fTestSuiteCombo.isDisposed() == false) {
			testSuiteValid = JavaUtils.isValidTestCaseName(fTestSuiteCombo.getText());
			if (testSuiteValid) {
				fTestSuiteName = fTestSuiteCombo.getText();
			}
		}

		if (!testSuiteValid) {
			final String MSG_TEST_SUITE_NAME_INVALID = "Test suite name is invalid.";
			message.set(MSG_TEST_SUITE_NAME_INVALID);
			return false;
		}
		return true;
	}

	private boolean validateMethodParameters(boolean onlyExecutable, StringHolder message) {

		for (MethodParameterNode parameter : fMethod.getMethodParameters()) {
			if (!validateOneParameter(parameter, onlyExecutable, message)) {
				return false;
			}
		}

		return true;
	}

	private boolean validateOneParameter(
			MethodParameterNode parameter, boolean onlyExecutable, StringHolder message) {

		if (parameter.isExpected()) {
			if (!validateExpectedParameter(parameter, message)) {
				return false;
			}
			return true;
		}

		return validateChoices(parameter, onlyExecutable, message);
	}

	private boolean validateExpectedParameter(MethodParameterNode parameter, StringHolder message) {
		if (!fParametersViewer.getChecked(parameter)) {
			final String MSG_EXPECTED_SHOULD_BE_CHECKED = "Expected parameter: %s should be checked.";
			message.set(String.format(MSG_EXPECTED_SHOULD_BE_CHECKED, parameter.getName())); 
			return false;
		}
		return true;
	}

	private boolean validateChoices(
			MethodParameterNode parameter, boolean onlyExecutable, StringHolder message) {
		boolean checkedChoiceFound = false;
		boolean choiceFound = false;

		for (ChoiceNode choice : parameter.getLeafChoices()) {
			choiceFound = true;
			checkedChoiceFound |= fParametersViewer.getChecked(choice);

			if (!validateChoiceImplementationStatus(choice, onlyExecutable, message)) {
				return false;
			}
		}

		if (!choiceFound) {
			final String MSG_NO_CHOICES =  "There are no choices for parameter: %s.";
			message.set(String.format(MSG_NO_CHOICES, parameter.getName()));
			return false;
		}

		if (!checkedChoiceFound) {
			final String MSG_AT_LEAST_ONE_CHOICE =  "At least one choice for parameter: %s must be checked.";
			message.set(String.format(MSG_AT_LEAST_ONE_CHOICE, parameter.getName()));
			return false;
		}

		return true;		
	}

	private boolean validateChoiceImplementationStatus(
			ChoiceNode choice, boolean onlyExecutable, StringHolder message) {
		if (!onlyExecutable) {
			return true;
		}
		if (!fFileInfoProvider.isProjectAvailable()) {
			return true;
		}
		EImplementationStatus status = fStatusResolver.getImplementationStatus(choice);
		if (status == EImplementationStatus.IMPLEMENTED) {
			return true;
		}

		final String MSG_NOT_IMPLEMENTED = "Choice: %s must be implemented.";
		message.set(String.format(MSG_NOT_IMPLEMENTED, choice.getName()));
		return false;
	}

	private boolean validateTargetFileText(StringHolder message) {
		if (!isContentFlagOn(TEST_CASES_EXPORT_COMPOSITE)) {
			return true;
		}
		if (fTargetFileText == null || fTargetFileText.getText().isEmpty()) {
			final String MSG_FILE_EMPTY = "Field: Export target file is empty.";
			message.set(MSG_FILE_EMPTY);
			return false;
		}
		return true;
	}

	private void setOkButtonStatus(boolean enabled) {
		if (fOkButton != null && !fOkButton.isDisposed()) {
			fOkButton.setEnabled(enabled);
		}
	}

	private void createGeneratorSelectionComposite(Composite container) {
		Composite generatorComposite = new Composite(container, SWT.NONE);
		generatorComposite.setLayout(new GridLayout(2, false));
		generatorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		Label generatorLabel = new Label(generatorComposite, SWT.NONE);
		generatorLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		generatorLabel.setText("Generator");

		createGeneratorViewer(generatorComposite);
	}

	private void createTestCasesExportComposite(Composite parentComposite) {
		fDialogObjectToolkit.createRowComposite(parentComposite);
		Composite advancedButtonComposite = fDialogObjectToolkit
				.createRowComposite(parentComposite);

		fDialogObjectToolkit.createButton(advancedButtonComposite,
				"Advanced...", new ExportDefinitionSelectionAdapter());

		final String TARGET_FILE = "Export target file";
		fTargetFileText = 
				fDialogObjectToolkit.createFileSelectionComposite(
						parentComposite, 
						TARGET_FILE, 
						TestCasesExportDialog.getExportFileExtensions(), 
						new ExportFileModifyListener());

		if (fTargetFile != null) {
			fTargetFileText.setText(fTargetFile);
		}
	}

	public String getExportTemplate() {
		return fExportTemplate;
	}

	public String getTargetFile() {
		return fTargetFile;
	}

	private void createGeneratorViewer(final Composite parent) {
		final GeneratorFactory<ChoiceNode> generatorFactory = new GeneratorFactory<>();
		ComboViewer generatorViewer = new ComboViewer(parent, SWT.READ_ONLY);
		fGeneratorCombo = generatorViewer.getCombo();
		fGeneratorCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		fGeneratorCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					fSelectedGenerator = generatorFactory
							.getGenerator(fGeneratorCombo.getText());
					correctionForMethodWithOneParam(fGeneratorCombo.getText());

					createParametersComposite(parent,
							fSelectedGenerator.parameters());
					fMainContainer.layout();
				} catch (GeneratorException exception) {
					exception.printStackTrace();
					fGeneratorCombo.setText("");
				}
			}
		});
		if (fGeneratorFactory.availableGenerators().size() > 0) {
			String[] availableGenerators = generatorFactory
					.availableGenerators().toArray(new String[] {});
			for (String generator : availableGenerators) {
				fGeneratorCombo.add(generator);
			}
			fGeneratorCombo.select(0);
			setOkButtonStatus(true);
		}
	}

	private void correctionForMethodWithOneParam(String generatorName)
			throws GeneratorException {
		if (fMethod.getParameters().size() != 1) {
			return;
		}
		if (!generatorName.equals(GeneratorFactory.GEN_TYPE_N_WISE)) {
			return;
		}

		for (IGeneratorParameter parameter : fSelectedGenerator.parameters()) {
			String parameterName = parameter.getName();
			if (parameterName.equals(NWiseGenerator.N_PARAMETER_NAME)) {
				IntegerParameter intParameter = (IntegerParameter) parameter;
				intParameter.setDefaultValue(1);
				break;
			}
		}
	}

	private void createParametersComposite(Composite parent,
			List<IGeneratorParameter> parameters) {
		fParameters = new HashMap<String, Object>();
		if (fParametersComposite != null && !fParametersComposite.isDisposed()) {
			fParametersComposite.dispose();
		}
		fParametersComposite = new Composite(parent, SWT.NONE);
		fParametersComposite.setLayout(new GridLayout(2, false));
		fParametersComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 2, 1));
		for (IGeneratorParameter parameter : parameters) {
			createParameterEdit(fParametersComposite, parameter);
		}
		parent.layout();
	}

	private void createParameterEdit(Composite parent,
			IGeneratorParameter definition) {
		fParameters.put(definition.getName(), definition.defaultValue());
		if (definition.getType() == TYPE.BOOLEAN) {
			createBooleanParameterEdit(parent, definition);
		} else {
			new Label(parent, SWT.LEFT).setText(definition.getName());
			if (definition.allowedValues() != null) {
				createComboParameterEdit(parent, definition);
			} else {
				switch (definition.getType()) {
				case INTEGER:
					createIntegerParameterEdit(parent,
							(IntegerParameter) definition);
					break;
				case DOUBLE:
					createDoubleParameterEdit(parent,
							(DoubleParameter) definition);
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
		checkButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
				2, 1));
		checkButton.setText(definition.getName());
		checkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fParameters.put(definition.getName(),
						checkButton.getSelection());
			}
		});
		checkButton.pack();
	}

	private void createComboParameterEdit(Composite parent,
			final IGeneratorParameter definition) {
		final Combo combo = new Combo(parent, SWT.CENTER | SWT.READ_ONLY);
		ModifyListener listener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				switch (definition.getType()) {
				case INTEGER:
					fParameters.put(definition.getName(),
							Integer.parseInt(combo.getText()));
					break;
				case DOUBLE:
					fParameters.put(definition.getName(),
							Double.parseDouble(combo.getText()));
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
		for (Object value : definition.allowedValues()) {
			values.add(value.toString());
		}
		return values.toArray(new String[] {});
	}

	private void createIntegerParameterEdit(Composite parent,
			final IntegerParameter definition) {
		final Spinner spinner = new Spinner(parent, SWT.BORDER | SWT.RIGHT);
		spinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fParameters.put(definition.getName(), spinner.getSelection());
			}
		});
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		spinner.setValues((int) definition.defaultValue(), definition.getMin(),
				definition.getMax(), 0, 1, 1);
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
				fParameters.put(definition.getName(),
						selection / (Math.pow(10, digits)));
			}
		});
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		int factor = (int) Math.pow(10, FLOAT_DECIMAL_PLACES);
		int defaultValue = (int) Math.round((double) definition.defaultValue()
				* factor);
		int minValue = (int) Math.round(definition.getMin() * factor);
		int maxValue = (int) Math.round(definition.getMax());
		spinner.setValues(defaultValue, minValue, maxValue,
				FLOAT_DECIMAL_PLACES, 1, 100);
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
		text.setText((String) definition.defaultValue());
	}

	private void saveConstraints() {
		Object[] checkedObjects = fConstraintsViewer.getCheckedElements();
		List<Constraint> constraints = new ArrayList<Constraint>();
		for (Object obj : checkedObjects) {
			if (obj instanceof Constraint) {
				constraints.add((Constraint) obj);
			}
		}

		fConstraints = constraints;
	}

	private void saveAlgorithmInput() {
		List<MethodParameterNode> parameters = fMethod.getMethodParameters();
		fAlgorithmInput = new ArrayList<List<ChoiceNode>>();
		for (int i = 0; i < parameters.size(); i++) {
			List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
			if (parameters.get(i).isExpected()) {
				choices.add(expectedValueChoice(parameters.get(i)));
			} else {
				for (ChoiceNode choice : parameters.get(i).getLeafChoices()) {
					if (fParametersViewer.getChecked(choice)) {
						choices.add(choice);
					}
				}
			}
			fAlgorithmInput.add(choices);
		}
	}

	private ChoiceNode expectedValueChoice(MethodParameterNode c) {
		ChoiceNode p = new ChoiceNode("", c.getDefaultValue());
		p.setParent(c);
		return p;
	}

	private class ChoiceTreeCheckStateListener extends TreeCheckStateListener {

		public ChoiceTreeCheckStateListener(CheckboxTreeViewer treeViewer) {
			super(treeViewer);
		}

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			super.checkStateChanged(event);
			if (event.getElement() instanceof MethodParameterNode
					&& ((MethodParameterNode) event.getElement()).isExpected()) {
				fParametersViewer.setChecked(event.getElement(), true);
			} else {
				updateOkButtonAndErrorMsg();
			}
		}
	}

	private class ParametersContentProvider extends TreeNodeContentProvider
	implements ITreeContentProvider {
		@Override
		public Object[] getElements(Object input) {
			if (input instanceof MethodNode) {
				return ((MethodNode) input).getParameters().toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object element) {
			List<Object> noChildren = new ArrayList<Object>();

			if (isExpectedMethodParameter(element)) {
				return noChildren.toArray();
			}
			if (element instanceof ChoicesParentNode) {
				return getChoices(element).toArray();
			}
			return noChildren.toArray();
		}

		private boolean isExpectedMethodParameter(Object element) {
			if (!(element instanceof MethodParameterNode)) {
				return false;
			}
			if (!((MethodParameterNode) element).isExpected()) {
				return false;
			}
			return true;
		}

		private List<Object> getChoices(Object element) {
			List<Object> children = new ArrayList<Object>();
			ChoicesParentNode parent = (ChoicesParentNode) element;

			if (fGenerateExecutableContent) {
				addImplementedChoices(parent, children);
				return children;
			}

			children.addAll(parent.getChoices());
			return children;
		}

		private void addImplementedChoices(ChoicesParentNode parent, List<Object> children) {
			for (ChoiceNode child : parent.getChoices()) {
				if (fStatusResolver.getImplementationStatus(child) != EImplementationStatus.NOT_IMPLEMENTED) {
					children.add(child);
				}
			}
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof AbstractNode) {
				return ((AbstractNode) element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}

	private class ConstraintsViewerContentProvider extends
	TreeNodeContentProvider implements ITreeContentProvider {
		private final Object[] EMPTY_ARRAY = new Object[] {};

		@Override
		public Object[] getElements(Object input) {
			if (input instanceof MethodNode) {
				return fMethod.getConstraintsNames().toArray();
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object[] getChildren(Object element) {
			if (element instanceof String) {
				Object[] result = fMethod.getConstraints((String) element)
						.toArray();
				return result;
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof ConstraintNode) {
				return ((ConstraintNode) element).getName();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}

	private class ExportDefinitionSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {

			TestCasesExportDialog dialog = new TestCasesExportDialog(
					FileCompositeVisibility.NOT_VISIBLE, fExportTemplate, fTargetFile);

			if (dialog.open() != IDialogConstants.OK_ID) {
				return;
			}

			fExportTemplate = dialog.getTemplate();
		}
	}

	class ExportFileModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			updateOkButtonAndErrorMsg();
		}
	}

}
