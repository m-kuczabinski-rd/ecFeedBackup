package com.testify.ecfeed.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class CDataScanner extends RuleBasedScanner {
	public IToken ESCAPED_CHAR;
	public IToken CDATA;
	
	public CDataScanner(ColorManager colorManager)
	{
		
		CDATA = new Token(new TextAttribute(colorManager.getColor(IXmlColorConstants.CDATA)));

		IRule[] rules = new IRule[2];

		// Add rule to pick up start of c section
		rules[0] = new CDataRule(CDATA, true);
		// Add a rule to pick up end of CDATA sections
		rules[1] = new CDataRule(CDATA, false);

		setRules(rules);
	}
	
	
	
	public IToken nextToken()
	{
		return super.nextToken();
	}

}
