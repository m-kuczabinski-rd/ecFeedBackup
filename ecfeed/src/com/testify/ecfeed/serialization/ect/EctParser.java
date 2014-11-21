/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.serialization.ect;

import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.serialization.IModelParser;
import com.testify.ecfeed.serialization.ParserException;

public class EctParser implements IModelParser {
	
	Builder fBuilder = new Builder();
	XomAnalyser fXomParser = new XomAnalyser();

	@Override
	public RootNode parseModel(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseRoot(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public ClassNode parseClass(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseClass(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public MethodNode parseMethod(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseMethod(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public ParameterNode parseParameter(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseParameter(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public ChoiceNode parseChoice(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseChoice(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public TestCaseNode parseTestCase(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseTestCase(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public ConstraintNode parseConstraint(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseConstraint(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public AbstractStatement parseStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseStatement(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public StaticStatement parseStaticStatement(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseStaticStatement(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}
	
	@Override
	public ChoicesParentStatement parseChoicesParentStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseChoiceStatement(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}
	
	@Override
	public ExpectedValueStatement parseExpectedValueStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseExpectedValueStatement(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}
	
	@Override
	public StatementArray parseStatementArray(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fXomParser.parseStatementArray(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}
}
