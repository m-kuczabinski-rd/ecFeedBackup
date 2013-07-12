package com.testify.ecfeed.editor.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.testify.ecfeed.editor.rules.CDataRule;
import com.testify.ecfeed.editors.ColorManager;
import com.testify.ecfeed.editors.ColorConstants;

public class CDataScanner extends RuleBasedScanner {
	public IToken ESCAPED_CHAR;
	public IToken CDATA;
	
	public CDataScanner(ColorManager colorManager)
	{
		
		CDATA = new Token(new TextAttribute(colorManager.getColor(ColorConstants.CDATA)));

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
