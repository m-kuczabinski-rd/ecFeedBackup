package com.testify.ecfeed.parsers;

import java.io.InputStream;

import com.testify.ecfeed.model.RootNode;

public interface IModelParser {
	public RootNode parseModel(InputStream istream) throws ParserException;
}
