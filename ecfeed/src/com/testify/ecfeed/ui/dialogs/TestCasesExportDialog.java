package com.testify.ecfeed.ui.dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.core.serialization.export.TestCasesExportParser;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;

public class TestCasesExportDialog extends TitleAreaDialog {

	private int fMethodParametersCount;
	private Text fTemplateText;
	private TestCasesExportParser fExportParser;
	private Text fTargetFileText;
	private String fTargetFile;

	public TestCasesExportDialog(Shell parentShell, int methodParametersCount) {
		super(parentShell);
		fMethodParametersCount = methodParametersCount;
		fExportParser = new TestCasesExportParser(); 
	}

	public static boolean isAdvancedMode() {
		return false;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setDialogTitle(this);
		setDialogMessage(this);

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (isAdvancedMode()) {
			createTemplateDefinitionContainer(container);
		}

		createTargetFileContainer(container);
		return area;
	}

	public void setDialogTitle(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_TITLE = "Export test data";
		setTitle(EXPORT_TEST_DATA_TITLE);
	}

	public void setDialogMessage(TitleAreaDialog dialog)	{
		if (isAdvancedMode()) {
			final String EXPORT_TEST_DATA_MESSAGE = "Define template for data export and select target file";
			setMessage(EXPORT_TEST_DATA_MESSAGE);
		} else {
			final String SELECT_TARGET = "Select target export file";
			setMessage(SELECT_TARGET);
		}
	}

	private void createTemplateDefinitionContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTemplateComposite(parent);
	}

	private void createTemplateComposite(Composite parent) {
		Label label = new Label(parent, SWT.NONE);

		final String EXPORT_TEST_DATA_TEMPLATE_LABEL = "Define template for export data.";
		label.setText(EXPORT_TEST_DATA_TEMPLATE_LABEL);

		fTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 300;
		fTemplateText.setLayoutData(gd);

		final String DEFAULT_TEMPLATE_TEXT_FILE = "res/TestCasesExportTemplate.txt";
		fTemplateText.setText(createTemplateText(this.getClass(), DEFAULT_TEMPLATE_TEXT_FILE));
	}

	private void createTargetFileContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label targetFileLabel = new Label(parent, SWT.NONE);

		final String EXPORT_TEST_DATA_TARGET_FILE_LABEL = "Target file";
		targetFileLabel.setText(EXPORT_TEST_DATA_TARGET_FILE_LABEL);

		Composite targetFileContainer = new Composite(parent, SWT.NONE);
		targetFileContainer.setLayout(new GridLayout(2, false));
		targetFileContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		fTargetFileText = new Text(targetFileContainer, SWT.BORDER);
		fTargetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Button browseButton = new Button(targetFileContainer, SWT.NONE);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new BrowseAdapter());
	}

	@Override
	protected void okPressed(){
		String template = null;

		if (fTemplateText != null) {
			template = fTemplateText.getText();
		}

		fExportParser.createSubTemplates(isAdvancedMode(), template, fMethodParametersCount);
		fTargetFile = fTargetFileText.getText();

		super.okPressed();
	}

	public String getHeaderTemplate(){
		return fExportParser.getHeaderTemplate();
	}

	public String getTestCaseTemplate(){
		return fExportParser.getTestCaseTemplate();
	}

	public String getFooterTemplate(){
		return fExportParser.getFooterTemplate();
	}

	public String getTargetFile(){
		return fTargetFile;
	}

	public static String createTemplateText(
			@SuppressWarnings("rawtypes") Class theClass, 
			String templateFilePath) {
		Bundle bundle = FrameworkUtil.getBundle(theClass);
		URL url = FileLocator.find(bundle, new Path(templateFilePath), null);
		BufferedReader in;
		String templateText = "";
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				templateText += inputLine + "\n";
			}
			in.close();
		} catch (IOException e) {
			ExceptionCatchDialog.display("Can not read template", e.getMessage());
		}

		return templateText;
	}	

	class BrowseAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			FileDialog dialog = new FileDialog(getParentShell());
			fTargetFileText.setText(dialog.open());
		}
	}
}
