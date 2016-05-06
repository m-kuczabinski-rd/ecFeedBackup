package com.testify.ecfeed.core.serialization.export;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.testify.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.core.utils.StringHelper;

public class TestCasesExportParser {
	private int fMethodParametersCount;
	private String fHeaderTemplate;
	private String fTestCaseTemplate;
	private String fFooterTemplate;

	private static final String HEADER_TEMPLATE_MARKER = "[Header]";
	private static final String TEST_CASE_TEMPLATE_MARKER = "[TestCase]";
	private static final String FOOTER_TEMPLATE_MARKER = "[Footer]";

	public TestCasesExportParser(int methodParametersCount) {
		fMethodParametersCount = methodParametersCount;
	}

	public void createSubTemplates(String template) {
		if (template == null) {
			ExceptionHelper
					.reportRuntimeException("Template text must not be empty.");
		}

		Map<String, String> templateMap = parseTemplate(template);

		fHeaderTemplate = createUserHeaderTemplate(templateMap);
		fTestCaseTemplate = createUserTestCaseTemplate(templateMap);
		fFooterTemplate = createUserFooterTemplate(templateMap);
		return;
	}

	public String createInitialTemplate() {
		return StringHelper.appendNewline(HEADER_TEMPLATE_MARKER)
				+ StringHelper.appendNewline(createDefaultHeaderTemplate())
				+ StringHelper.appendNewline(TEST_CASE_TEMPLATE_MARKER)
				+ StringHelper.appendNewline(createDefaultTestCaseTemplate())
				+ StringHelper.appendNewline(FOOTER_TEMPLATE_MARKER);
	}

	public String getHeaderTemplate() {
		return fHeaderTemplate;
	}

	public String getTestCaseTemplate() {
		return fTestCaseTemplate;
	}

	public String getFooterTemplate() {
		return fFooterTemplate;
	}

	public static String createUserHeaderTemplate(Map<String, String> template) {
		return StringHelper.removeLastNewline(template
				.get(HEADER_TEMPLATE_MARKER.toLowerCase()));
	}

	public static String createUserTestCaseTemplate(Map<String, String> template) {
		return StringHelper.removeLastNewline(template
				.get(TEST_CASE_TEMPLATE_MARKER.toLowerCase()));
	}

	public static String createUserFooterTemplate(Map<String, String> template) {
		return StringHelper.removeLastNewline(template
				.get(FOOTER_TEMPLATE_MARKER.toLowerCase()));
	}

	private String createDefaultHeaderTemplate() {
		final String NAME_TAG = "name";
		return createParameterTemplate(NAME_TAG);
	}

	private String createDefaultTestCaseTemplate() {
		final String VALUE_TAG = "value";
		return createParameterTemplate(VALUE_TAG);
	}

	public static Map<String, String> parseTemplate(String templateText) {

		final String SECTION_HEADER_REGEX = "\\s*\\[([^]]*)\\]\\s*";
		final String COMMENTED_LINE_REGEX = "^\\s*#.*";

		Map<String, String> result = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(templateText, "\n");
		String currentSection = "";
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			if (line.matches(SECTION_HEADER_REGEX)) {
				int sectionTitleStart = line.indexOf('[');
				int sectionTitleStop = line.indexOf(']') + 1;
				currentSection = line.toLowerCase().substring(
						sectionTitleStart, sectionTitleStop);
				if (result.containsKey(currentSection) == false) {
					result.put(currentSection, "");
				}
			} else if (line.matches(COMMENTED_LINE_REGEX) == false) {
				result.put(currentSection,
						result.get(currentSection).concat(line + "\n"));
			}
		}
		return result;
	}

	public String createParameterTemplate(String parameterTag) {
		String template = new String();

		for (int cnt = 1; cnt <= fMethodParametersCount; ++cnt) {
			if (cnt > 1) {
				template = template + ",";
			}
			String paramDescription = "$" + cnt + "." + parameterTag;
			template = template + paramDescription;
		}

		return template;
	}
}
