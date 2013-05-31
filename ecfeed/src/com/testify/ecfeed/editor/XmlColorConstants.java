package com.testify.ecfeed.editor;

import org.eclipse.swt.graphics.RGB;

public interface XmlColorConstants {
	RGB XML_COMMENT = new RGB(128, 0, 0);
	RGB PROC_INSTR = new RGB(128, 128, 128);
	RGB STRING = new RGB(0, 128, 0);
	RGB DEFAULT = new RGB(0, 0, 0);
	RGB TAG = new RGB(0, 0, 128);

	//enhancements
	RGB ESCAPED_CHAR = new RGB(128, 128, 0);
	RGB CDATA = new RGB(0, 128, 128);
	RGB CDATA_TEXT = new RGB(255, 0, 0);
}
