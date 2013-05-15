package com.testify.ecfeed.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CDataRule implements IRule {

	IToken fToken;
	StringBuffer fBuffer = new StringBuffer();
	int fCharsRead = 0;

	private String fMatchString;	
	private static final String START_MATCH_STRING  = "<![CDATA[";
	private static final String END_MATCH_STRING = "]]>";

	
	public CDataRule(IToken token, boolean start)
	{
		super();
		this.fToken = token;
		this.fMatchString = start?START_MATCH_STRING:END_MATCH_STRING;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{

		fBuffer.setLength(0);

		fCharsRead = 0;
		int c = read(scanner);

		if (c == fMatchString.charAt(0))
		{
			do
			{
				c = read(scanner);
			}
			while (isOK((char) c));

			if (fCharsRead == fMatchString.length())
			{
				return fToken;
			}
			else
			{
				rewind(scanner);
				return Token.UNDEFINED;
			}

		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	private void rewind(ICharacterScanner scanner)
	{
		int rewindLength = fCharsRead;
		while (rewindLength > 0)
		{
			scanner.unread();
			rewindLength--;
		}
	}

	private int read(ICharacterScanner scanner)
	{
		int c = scanner.read();
		fBuffer.append((char) c);
		fCharsRead++;
		return c;
	}

	private boolean isOK(char c)
	{
		if (fCharsRead >= fMatchString.length())
			return false;
		if (fMatchString.charAt(fCharsRead - 1) == c)
			return true;
		else
			return false;
	}

}
