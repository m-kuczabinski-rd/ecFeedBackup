package com.testify.ecfeed.editor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class XmlWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
