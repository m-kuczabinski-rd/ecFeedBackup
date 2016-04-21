package com.testify.ecfeed.ui.dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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

import com.testify.ecfeed.core.utils.StringHelper;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;

public class TestCasesExportDialog extends TitleAreaDialog {

	private int fMethodParametersCount;
	private String fHeaderTemplate;
	private String fTestCaseTemplate;
	private String fFooterTemplate;
	private Text fTemplateText;
	private Text fTargetFileText;
	private String fTargetFile;

	public TestCasesExportDialog(Shell parentShell, int methodParametersCount) {
		super(parentShell);
		fMethodParametersCount = methodParametersCount;
	}

	public static boolean isExtendedMode() {
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

		if (isExtendedMode()) {
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
		if (isExtendedMode()) {
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
		fTemplateText.setText(Helper.createTemplateText(this.getClass(), DEFAULT_TEMPLATE_TEXT_FILE));
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
		if (isExtendedMode()) {
			String templateText = fTemplateText.getText();
			Map<String, String> template = Helper.parseTemplate(templateText);

			fHeaderTemplate = Helper.createUserHeaderTemplate(template);
			fTestCaseTemplate = Helper.createUserTestCaseTemplate(template);
			fFooterTemplate = Helper.createUserFooterTemplate(template);
		} else {
			fHeaderTemplate = Helper.createDefaultHeaderTemplate(fMethodParametersCount);
			fTestCaseTemplate = Helper.createDefaultTestCaseTemplate(fMethodParametersCount);
			fFooterTemplate = Helper.createDefaultFooterTemplate();
		}

		fTargetFile = fTargetFileText.getText();
		super.okPressed();
	}

	public String getHeaderTemplate(){
		return fHeaderTemplate;
	}

	public String getTestCaseTemplate(){
		return fTestCaseTemplate;
	}

	public String getFooterTemplate(){
		return fFooterTemplate;
	}

	public String getTargetFile(){
		return fTargetFile;
	}
	
	class BrowseAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			FileDialog dialog = new FileDialog(getParentShell());
			fTargetFileText.setText(dialog.open());
		}
	}
}

class Helper
{
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

	public static Map<String, String> parseTemplate(String templateText) {
		
		final String SECTION_HEADER_REGEX = "\\s*\\[([^]]*)\\]\\s*";
		final String COMMENTED_LINE_REGEX = "^\\s*#.*";
		
		Map<String, String> result = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(templateText, "\n");
		String currentSection = "";
		while(tokenizer.hasMoreTokens()){
			String line = tokenizer.nextToken();
			if(line.matches(SECTION_HEADER_REGEX)){
				int sectionTitleStart = line.indexOf('[');
				int sectionTitleStop = line.indexOf(']') + 1;
				currentSection = line.toLowerCase().substring(sectionTitleStart, sectionTitleStop);
				if(result.containsKey(currentSection) == false){
					result.put(currentSection, "");
				}
			}
			else if(line.matches(COMMENTED_LINE_REGEX) == false){
				result.put(currentSection, result.get(currentSection).concat(line + "\n"));
			}
		}
		return result;
	}
	
	public static String createUserHeaderTemplate(Map<String, String> template)
	{
		final String HEADER_TEMPLATE_MARKER = "[Header]";
		return StringHelper.removeLastNewline(template.get(HEADER_TEMPLATE_MARKER.toLowerCase()));
	}
	
	public static String createUserTestCaseTemplate(Map<String, String> template) {
		final String TEST_CASE_TEMPLATE_MARKER = "[TestCase]";
		return StringHelper.removeLastNewline(template.get(TEST_CASE_TEMPLATE_MARKER.toLowerCase()));
	}
	
	public static String createUserFooterTemplate(Map<String, String> template) {
		final String FOOTER_TEMPLATE_MARKER = "[Footer]";
		return StringHelper.removeLastNewline(template.get(FOOTER_TEMPLATE_MARKER.toLowerCase()));
	}

	public static String createDefaultHeaderTemplate(int paramCount) {
		final String NAME_TAG = "name";
		return Helper.createParameterTemplate(paramCount, NAME_TAG);
	}

	public static String createDefaultTestCaseTemplate(int paramCount) {
		final String VALUE_TAG = "value";
		return Helper.createParameterTemplate(paramCount, VALUE_TAG);
	}

	public static String createDefaultFooterTemplate() {
		return new String();
	}
	
	public static String createParameterTemplate(int parameterCount, String parameterTag) {
		String template = new String();

		for (int cnt = 1; cnt <= parameterCount; ++cnt) {
			if (cnt > 1) {
				template = template + ",";
			}
			String paramDescription = "$" + cnt + "." + parameterTag;
			template = template + paramDescription;
		}

		return template;
	}
	
}