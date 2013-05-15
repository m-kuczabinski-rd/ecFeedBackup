package com.testify.ecfeed.editors;

import org.eclipse.jface.text.rules.*;

public class XmlPartitionScanner extends RuleBasedPartitionScanner {
	public final static String XML_START_TAG = "__xml_start_tag";
	public final static String XML_PI = "__xml_pi";
	public final static String XML_DOCTYPE = "__xml_doctype";
	public final static String XML_END_TAG = "__xml_end_tag";
	public final static String XML_TEXT = "__xml_text";
	public final static String XML_CDATA = "__xml_cdata";
	public final static String XML_COMMENT = "__xml_comment";

	public XmlPartitionScanner() {

		IToken xmlComment = new Token(XML_COMMENT);
		IToken xmlPi = new Token(XML_PI);
		IToken startTag = new Token(XML_START_TAG);
		IToken endTag = new Token(XML_END_TAG);
		IToken docType = new Token(XML_DOCTYPE);
		IToken text = new Token(XML_TEXT);

		IPredicateRule[] rules = new IPredicateRule[7];

	    rules[0] = new NonMatchingRule();
	    rules[1] = new MultiLineRule("<!--", "-->", xmlComment);
	    rules[2] = new MultiLineRule("<?", "?>", xmlPi);
	    rules[3] = new MultiLineRule("</", ">", endTag);
	    rules[4] = new MultiLineRule("<", ">", startTag);
	    rules[5] = new MultiLineRule("<!DOCTYPE", ">", docType);
	    rules[6] = new XmlTextPredicateRule(text);

		setPredicateRules(rules);
	}
}
