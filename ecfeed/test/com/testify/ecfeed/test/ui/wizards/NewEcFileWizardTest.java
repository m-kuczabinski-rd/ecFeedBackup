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

//package com.testify.ecfeed.test.ui.wizards;
//
//import static org.junit.Assert.*;
//
//import java.io.ByteArrayInputStream;
//
//import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
//import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
//import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import com.testify.ecfeed.constants.Constants;
//import com.testify.ecfeed.constants.DialogStrings;
//import com.testify.ecfeed.model.RootNode;
//import com.testify.ecfeed.parsers.EcParser;
//import com.testify.ecfeed.test.ui.TestUiUtils;
//
////TODO Rework - adapt to reworked new file wizard
//@RunWith(SWTBotJunit4ClassRunner.class)
//public class NewEcFileWizardTest {
//	private static SWTWorkbenchBot fBot;
//	private final static String DEFAULT_SOURCE_FOLDER = "src";
//	private final static String fProjectName = "com.example.test";
//	
//	@BeforeClass
//	public static void beforeClass() throws Exception{
//		fBot = new SWTWorkbenchBot();
//		fBot.viewByTitle("Welcome").close();
//		
//		TestUiUtils.createNewProject(fBot, fProjectName);
//		
//	}
//
//	@Test
//	public void testNewEctWizard(){
//		//test if the new ect file wizard appears at all
//		checkWizardAppears();
//		//try to create default ect file in "/src"
//		createDefaultEctFile();
////		//test if the wizard reacts appropriately on valid and not valid container paths
////		testContainerValues();
//		//test if the wizard recognizes valid and not valid file names
////		testCustomEctFileName();
//		//test wizard behavior in case when created file already exists
////		testFileExists();
//	}
//	
//	public void checkWizardAppears(){
//		TestUiUtils.openNewEctFileWizard(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER});
//		assertTrue(fBot.label("New Equivalence Class Model").isVisible());
//		fBot.button("Cancel").click();
//	}
//	
//	public void createDefaultEctFile(){
//		TestUiUtils.openNewEctFileWizard(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER});
//		assertTrue(fBot.button("Finish").isEnabled());
//		fBot.button("Finish").click();
//		
//		SWTBotEclipseEditor editor = 
//				TestUiUtils.openEditor(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER, 
//						Constants.DEFAULT_NEW_ECT_FILE_NAME});
//		
//		String modelText = editor.getText();
//		EcParser parser = new EcParser();
//		RootNode model = parser.parseEctFile(new ByteArrayInputStream(modelText.getBytes()));
//		RootNode referenceModel = new RootNode(Constants.DEFAULT_NEW_ECT_MODEL_NAME);
//		
//		assertEquals(referenceModel, model);
//	}
//	
//	public void testContainerValues(){
//		String existingContainer1 = "/" + fProjectName;
//		String existingContainer2 = "/" + fProjectName + "/" + DEFAULT_SOURCE_FOLDER;
//		String nonExistingContainer = "/" + fProjectName + "/nonExistingFolder";
//		
//		TestUiUtils.openNewEctFileWizard(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER});
//		//enter existing containers and assert that the "OK" button is enabled
//		fBot.text().setText(existingContainer1);
//		assertTrue(fBot.button("Finish").isEnabled());
//		fBot.text().setText(existingContainer2);
//		assertTrue(fBot.button("Finish").isEnabled());
//	
//		//enter non existing containers and assert that the "OK" button is disabled
//		fBot.text().setText(nonExistingContainer);
//		assertFalse(fBot.button("Finish").isEnabled());
//		
//		//cancel the operation to return to initial state
//		fBot.button("Cancel").click();
//	}
//
//	public void testCustomEctFileName(){
//		TestUiUtils.openNewEctFileWizard(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER});
////		assertTrue(fBot.button("Finish").isEnabled());
////		fBot.text(1).setText("file");
////		assertFalse(fBot.button("Finish").isEnabled());
////		fBot.text(1).setText("file.txt");
////		assertFalse(fBot.button("Finish").isEnabled());
//		fBot.text(1).setText("file.ect");
//		assertTrue(fBot.button("Finish").isEnabled());
//		fBot.button("Finish").click();
//		
//		RootNode model = TestUiUtils.getModel(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER, 
//				"file.ect"});
//		RootNode referenceModel = new RootNode(Constants.DEFAULT_NEW_ECT_MODEL_NAME);
//		assertNotEquals(referenceModel, model);
//
//		referenceModel.setName("file");
//		assertEquals(referenceModel, model);
//	}
//
//	public void testFileExists(){
//		String fileName = "fileToOverwrite.ect";
//		
//		//create new ect file using fileName name
//		TestUiUtils.openNewEctFileWizard(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER});
//		fBot.text(1).setText(fileName);
//		fBot.button("Finish").click();
//		
//		//rename the model
//		SWTBotEclipseEditor editor = TestUiUtils.openEditor(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER, fileName});
//		fBot.button("Rename...").click();
//		fBot.text(1).setText("Renamed model");
//		fBot.button("OK").click();
//		editor.save();
//		
//		//Prepare reference model with the same name as renamed model and confirm they equal
//		RootNode referenceModel = new RootNode("Renamed model");
//		RootNode model = TestUiUtils.getModel(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER, fileName});
//		assertEquals(referenceModel, model);
//
//		//try to create new ect file with the same name
//		TestUiUtils.openNewEctFileWizard(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER});
//		fBot.text(1).setText(fileName);
//		fBot.button("Finish").click();
//		//check that the "File exists" dialog appeared
//		assertEquals(DialogStrings.WIZARD_FILE_EXISTS_TITLE, fBot.activeShell().getText());
//
//		//cancel the creation of the file
//		fBot.button("Cancel").click();
//		//assert that the original file was not affected 
////		assertEquals(referenceModel, TestUiUtils.getModel(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER, fileName}));
//
//		//try to create the file again
//		TestUiUtils.openNewEctFileWizard(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER});
//		fBot.text(1).setText(fileName);
//		fBot.button("Finish").click();
//		//check that the "File exists" dialog appeared
////		assertEquals(DialogStrings.WIZARD_FILE_EXISTS_TITLE, fBot.activeShell().getText());
//		//confirm overwrite
//		fBot.button("OK").click();
//		editor.save();
//		//check that the file has changed
////		assertNotEquals(referenceModel, TestUiUtils.getModel(fBot, new String[]{fProjectName, DEFAULT_SOURCE_FOLDER, fileName}));
//	}
//}
