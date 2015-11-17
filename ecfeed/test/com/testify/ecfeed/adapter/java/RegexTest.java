package com.testify.ecfeed.adapter.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegexTest {

	boolean matchTextWithRegex(String text, String regex) {
		return text.matches(regex);
	}

	boolean matchTextWithRegexAlphaSp64(String text) {
		return matchTextWithRegex(text, Constants.REGEX_ALPHANUMERIC_WITH_SPACES_64);
	}

	@Test
	public void shouldMatchTextWithoutSpaces(){
		assertTrue(matchTextWithRegexAlphaSp64("abc"));
	}

	@Test
	public void shouldMatchTextWithSpaces(){
		assertTrue(matchTextWithRegexAlphaSp64("a b c "));
	}	

	@Test
	public void shouldNotMatchTextWithInvalidCharacters(){
		assertFalse(matchTextWithRegexAlphaSp64("a^bc"));
	}	

	@Test
	public void shouldNotMatchTextWithLeadingSpace(){
		assertFalse(matchTextWithRegexAlphaSp64(" abc"));
	}	

}
