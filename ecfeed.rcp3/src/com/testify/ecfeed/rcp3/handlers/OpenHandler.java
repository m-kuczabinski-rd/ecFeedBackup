package com.testify.ecfeed.rcp3.handlers;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


public class OpenHandler extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("OpenHandler.execute");
		
		File file = new File("/home/marekq/Tymczasowy/xxx.ect");
		
		if (!file.exists()) {
			return null;
		}
		if (!file.isFile()) {
			return null;
		}
			
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
	    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	 
	    try {
	        IDE.openEditorOnFileStore( page, fileStore );
	    } catch ( PartInitException e ) {
	    }
	    
		return null;
	}
	
}
