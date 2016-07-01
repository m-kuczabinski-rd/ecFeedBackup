package com.testify.ecfeed.ui.editor;

import com.ecfeed.utils.IChecker;

public class CanAddDocumentChecker implements IChecker { 


	@Override
	public boolean check(String pathWithFileName) {
		if (ModelEditorHelper.isFileAlreadyOpen(pathWithFileName)) {
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage(String pathWithFileName) {
		return "File: " + pathWithFileName + " is already open in editor. Can not save document under this name."; 
	}

}
