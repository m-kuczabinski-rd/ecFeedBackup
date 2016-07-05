/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.serialization.export;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringHolder;

public class ExportTemplateParser {
	private int fMethodParametersCount;
	private String fHeaderTemplate;
	private String fTestCaseTemplate;
	private String fFooterTemplate;

	public static final String HEADER_MARKER = "[Header]";
	public static final String TEST_CASE_MARKER = "[TestCase]";
	public static final String FOOTER_MARKER = "[Footer]";

	public ExportTemplateParser(int methodParametersCount) {
		fMethodParametersCount = methodParametersCount;
	}

	public void createSubTemplates(String template) {
		if (template == null) {
			ExceptionHelper.reportRuntimeException("Template text must not be empty.");
		}

		Map<String, String> templateMap = parseTemplate(template);

		fHeaderTemplate = createUserHeaderTemplate(templateMap);
		fTestCaseTemplate = createUserTestCaseTemplate(templateMap);
		fFooterTemplate = createUserFooterTemplate(templateMap);
		return;
	}

	public String createInitialTemplate() {
		return StringHelper.appendNewline(HEADER_MARKER)
				+ StringHelper.appendNewline(createDefaultHeaderTemplate(fMethodParametersCount))
				+ StringHelper.appendNewline(TEST_CASE_MARKER)
				+ StringHelper.appendNewline(createDefaultTestCaseTemplate(fMethodParametersCount))
				+ StringHelper.appendNewline(FOOTER_MARKER);
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

	private static String createUserHeaderTemplate(Map<String, String> template) {
		String headerTemplate = template.get(HEADER_MARKER);

		if (headerTemplate == null) {
			return null;
		}

		return StringHelper.removeNewlineAtEnd(headerTemplate.trim());
	}

	private static String createUserTestCaseTemplate(
			Map<String, String> template) {

		String testCaseTemplate = template.get(TEST_CASE_MARKER);

		if (testCaseTemplate == null) {
			return null;
		}
		return StringHelper.removeNewlineAtEnd(testCaseTemplate.trim());
	}

	private static String createUserFooterTemplate(Map<String, String> template) {

		String footerTemplate = template.get(FOOTER_MARKER);

		if (footerTemplate == null) {
			return null;
		}
		return StringHelper.removeNewlineAtEnd(footerTemplate.trim());
	}

	private static String createDefaultHeaderTemplate(int methodParametersCount) {
		final String NAME_TAG = "name";
		return createParameterTemplate(NAME_TAG, methodParametersCount);
	}

	private static String createDefaultTestCaseTemplate(
			int methodParametersCount) {
		final String VALUE_TAG = "value";
		return createParameterTemplate(VALUE_TAG, methodParametersCount);
	}

	public static Map<String, String> parseTemplate(String templateText) {
		Map<String, String> resultMap = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(templateText, StringHelper.newLine());
		StringHolder currentSectionMarker = new StringHolder();

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();

			if (isCommentLine(line)) {
				continue;
			}

			if (setSectionMarker(line, currentSectionMarker)) {
				continue;
			}

			if (currentSectionMarker.isNull()) {
				continue;
			}

			updateResultMap(currentSectionMarker.get(), line, resultMap);
		}
		return resultMap;
	}

	private static boolean isCommentLine(String line) {
		final String COMMENTED_LINE_REGEX = "^\\s*#.*";
		if (line.matches(COMMENTED_LINE_REGEX)) {
			return true;
		}
		return false;
	}

	private static boolean setSectionMarker(String line,
			StringHolder currentMarker) {
		if (!isSectionMarker(line)) {
			return false;
		}
		currentMarker.set(getMarker(line));
		return true;
	}

	private static boolean isSectionMarker(String line) {
		String trimmedLine = line.trim();

		if (trimmedLine.equals(HEADER_MARKER)) {
			return true;
		}

		if (trimmedLine.equals(TEST_CASE_MARKER)) {
			return true;
		}

		if (trimmedLine.equals(FOOTER_MARKER)) {
			return true;
		}

		return false;
	}

	private static void updateResultMap(String marker, String line,
			Map<String, String> result) {
		if (!result.containsKey(marker)) {
			result.put(marker, line);
			return;
		}

		String oldContents = result.get(marker);
		String newContents = oldContents.concat(StringHelper.newLine() + line);
		result.put(marker, newContents);
	}

	private static String getMarker(String line) {
		int sectionTitleStart = line.indexOf('[');
		int sectionTitleStop = line.indexOf(']') + 1;
		return line.substring(sectionTitleStart, sectionTitleStop);
	}

	private static String createParameterTemplate(String parameterTag,
			int methodParametersCount) {
		String template = new String();

		for (int cnt = 1; cnt <= methodParametersCount; ++cnt) {
			if (cnt > 1) {
				template = template + ",";
			}
			String paramDescription = "$" + cnt + "." + parameterTag;
			template = template + paramDescription;
		}

		return template;
	}
}
