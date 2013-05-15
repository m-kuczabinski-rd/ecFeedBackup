package com.testify.ecfeed.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

import com.testify.ecfeed.editors.ColorManager;
import com.testify.ecfeed.editors.IXmlColorConstants;
import com.testify.ecfeed.editors.XmlWhitespaceDetector;

public class XmlTagScanner extends RuleBasedScanner {

	public XmlTagScanner(ColorManager manager) {
		IToken string =	new Token(new TextAttribute(manager.getColor(IXmlColorConstants.STRING)));

		IRule[] rules = new IRule[3];

		// Add rule for double quotes
		rules[0] = new SingleLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[1] = new SingleLineRule("'", "'", string, '\\');
		// Add generic whitespace rule.
		rules[2] = new WhitespaceRule(new XmlWhitespaceDetector());

		setRules(rules);
	}
}
