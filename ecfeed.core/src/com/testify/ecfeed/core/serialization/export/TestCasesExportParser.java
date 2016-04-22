package com.testify.ecfeed.core.serialization.export;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.testify.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.core.utils.StringHelper;

public class TestCasesExportParser
{
	private String fHeaderTemplate;
	private String fTestCaseTemplate;
	private String fFooterTemplate;

	public void createSubTemplates(boolean isExtendedMode, String template, int methodParametersCount)
	{
		if (isExtendedMode) {
			if (template == null) {
				ExceptionHelper.reportRuntimeException("Template text must not be empty.");
			}

			Map<String, String> templateMap = parseTemplate(template);

			fHeaderTemplate = createUserHeaderTemplate(templateMap);
			fTestCaseTemplate = createUserTestCaseTemplate(templateMap);
			fFooterTemplate = createUserFooterTemplate(templateMap);
			return;
		} 

		fHeaderTemplate = createDefaultHeaderTemplate(methodParametersCount);
		fTestCaseTemplate = createDefaultTestCaseTemplate(methodParametersCount);
		fFooterTemplate = createDefaultFooterTemplate();
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
		return createParameterTemplate(paramCount, NAME_TAG);
	}

	public static String createDefaultTestCaseTemplate(int paramCount) {
		final String VALUE_TAG = "value";
		return createParameterTemplate(paramCount, VALUE_TAG);
	}

	public static String createDefaultFooterTemplate() {
		return new String();
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
