/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.editor.sourceviewer;

import org.eclipse.jface.text.rules.*;

public class XmlPartitionScanner extends RuleBasedPartitionScanner {
	public final static String XML_START_TAG = "__xml_start_tag";
	public final static String XML_PI = "__xml_pi";
	public final static String XML_END_TAG = "__xml_end_tag";

	public class StartTagRule extends MultiLineRule {

		public StartTagRule(IToken token)
		{
			this(token, false);
		}	
		
		protected StartTagRule(IToken token, boolean endAsWell)
		{
			super("<", endAsWell ? "/>" : ">", token);
		}

		protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
		{
			int c = scanner.read();
			if (sequence[0] == '<')
			{
				if (c == '?')
				{
					// processing instruction - abort
					scanner.unread();
					return false;
				}
				if (c == '!')
				{
					scanner.unread();
					// comment - abort
					return false;
				}
			}
			else if (sequence[0] == '>')
			{
				scanner.unread();
			}
			return super.sequenceDetected(scanner, sequence, eofAllowed);
		}
	}

	
	public XmlPartitionScanner() {

		IToken xmlPi = new Token(XML_PI);
		IToken startTag = new Token(XML_START_TAG);
		IToken endTag = new Token(XML_END_TAG);

		IPredicateRule[] rules = new IPredicateRule[3];

	    rules[0] = new MultiLineRule("<?", "?>", xmlPi);
	    rules[1] = new MultiLineRule("</", ">", endTag);
	    rules[2] = new StartTagRule(startTag);

		setPredicateRules(rules);
	}
}
