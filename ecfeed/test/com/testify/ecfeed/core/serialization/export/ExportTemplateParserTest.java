/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.core.serialization.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.ecfeed.core.serialization.export.ExportTemplateParser;
import com.ecfeed.core.utils.StringHelper;

public class ExportTemplateParserTest {

	@Test
	public void ShouldNotThrowWhenEmpty() {

		String templateText = new String();

		Map<String, String> result = null;

		try {
			result = ExportTemplateParser.parseTemplate(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertTrue(result.isEmpty());
	}

	@Test
	public void ShouldParseForTwoParamsTemplate() {

		String templateText = 
				StringHelper.appendNewline(ExportTemplateParser.HEADER_MARKER)
				+ StringHelper.appendNewline("$1.name,$2.name") 
				+ StringHelper.appendNewline(ExportTemplateParser.TEST_CASE_MARKER) 
				+ StringHelper.appendNewline("$1.value,$2.value")
				+ StringHelper.appendNewline(ExportTemplateParser.FOOTER_MARKER);

		Map<String, String> result = null;

		try {
			result = ExportTemplateParser.parseTemplate(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertEquals("$1.name,$2.name", result.get(ExportTemplateParser.HEADER_MARKER));
		assertEquals("$1.value,$2.value", result.get(ExportTemplateParser.TEST_CASE_MARKER));
	}

	@Test
	public void ShouldParseMultiLineSectionsTemplate() {

		String templateText = 
				StringHelper.appendNewline(ExportTemplateParser.HEADER_MARKER)
				+ StringHelper.appendNewline("HEADER")
				+ StringHelper.appendNewline("$1.name,$2.name") 

				+ StringHelper.appendNewline(ExportTemplateParser.TEST_CASE_MARKER)
				+ StringHelper.appendNewline("TEST CASE")
				+ StringHelper.appendNewline("$1.value,$2.value")

				+ StringHelper.appendNewline(ExportTemplateParser.FOOTER_MARKER)
				+ StringHelper.appendNewline("FOOTER 1")
				+ StringHelper.appendNewline("FOOTER 2");

		Map<String, String> resultMap = null;

		try {
			resultMap = ExportTemplateParser.parseTemplate(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		String header = resultMap.get(ExportTemplateParser.HEADER_MARKER);
		String expectedHeader = "HEADER" + StringHelper.newLine() + "$1.name,$2.name";
		assertEquals(expectedHeader, header);

		String testCase = resultMap.get(ExportTemplateParser.TEST_CASE_MARKER); 
		String expectedTestCase = "TEST CASE" + StringHelper.newLine() + "$1.value,$2.value";

		assertEquals(expectedTestCase, testCase);
	}	

	@Test
	public void ShouldNotThrowWhenOnlyInvalidMarker() {

		String templateText = "[xxx]";

		Map<String, String> result = null;

		try {
			result = ExportTemplateParser.parseTemplate(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertTrue(result.isEmpty());
	}

	@Test
	public void ShouldIgnoreInvalidMarker() {

		String templateText = "[Xxx]" + "\n" + "$1.name\n"
				+ ExportTemplateParser.TEST_CASE_MARKER + "\n" + "$1.value\n"
				+ ExportTemplateParser.FOOTER_MARKER;

		Map<String, String> result = null;

		try {
			result = ExportTemplateParser.parseTemplate(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertEquals(1, result.size());
		assertEquals("$1.value",
				result.get(ExportTemplateParser.TEST_CASE_MARKER));
	}

}
