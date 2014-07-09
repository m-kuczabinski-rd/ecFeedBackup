package com.testify.ecfeed.serialization.ect;

import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.serialization.IModelParser;
import com.testify.ecfeed.serialization.ParserException;

public class EctParser implements IModelParser {
	
	Builder fBuilder = new Builder();
	XomAnalyser fParser = new XomAnalyser();

	@Override
	public RootNode parseModel(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fParser.parseRoot(document.getRootElement());
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
			return fParser.parseClass(document.getRootElement());
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
			return fParser.parseMethod(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public CategoryNode parseCategory(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fParser.parseCategory(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public PartitionNode parsePartition(InputStream istream) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fParser.parsePartition(document.getRootElement());
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
			return fParser.parseTestCase(document.getRootElement(), method);
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
			return fParser.parseConstraint(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}

	@Override
	public BasicStatement parseStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fParser.parseStatement(document.getRootElement(), method);
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
			return fParser.parseStaticStatement(document.getRootElement());
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}
	
	@Override
	public PartitionedCategoryStatement parsePartitionedCategoryStatement(InputStream istream, MethodNode method) throws ParserException {
		try {
			Document document = fBuilder.build(istream);
			return fParser.parsePartitionStatement(document.getRootElement(), method);
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
			return fParser.parseExpectedValueStatement(document.getRootElement(), method);
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
			return fParser.parseStatementArray(document.getRootElement(), method);
		} catch (ParsingException e) {
			throw new ParserException(Messages.PARSING_EXCEPTION(e));
		} catch (IOException e) {
			throw new ParserException(Messages.IO_EXCEPTION(e));
		}
	}
}
