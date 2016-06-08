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

		String templateText = ExportTemplateParser.HEADER_MARKER + "\n"
				+ "$1.name,$2.name\n" + ExportTemplateParser.TEST_CASE_MARKER
				+ "\n" + "$1.value,$2.value\n"
				+ ExportTemplateParser.FOOTER_MARKER;

		Map<String, String> result = null;

		try {
			result = ExportTemplateParser.parseTemplate(templateText);
		} catch (Exception e) {
			fail("Exception thrown during export.");
		}

		assertEquals("$1.name,$2.name",
				result.get(ExportTemplateParser.HEADER_MARKER));
		assertEquals("$1.value,$2.value",
				result.get(ExportTemplateParser.TEST_CASE_MARKER));
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
