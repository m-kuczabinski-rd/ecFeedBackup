package com.testify.ecfeed.test.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.EcParser;

public class TestUiUtils {
	public static void createNewProject(SWTWorkbenchBot bot, String projectName){
		
		bot.menu("File").menu("New").menu("Project...").click();
		bot.tree().getTreeItem("Java Project").select();
		bot.button("Next >").click();
		bot.text().setText(projectName); 
		bot.button("Next >").click();
		bot.button("Finish").click();
		bot.button("Yes").click();
	}
	
	public static void createTestPackage(SWTWorkbenchBot bot, String project, 
			String sourceFolder, String packageName){
		
		bot.tree().getTreeItem(project).expand();
		bot.tree().getTreeItem(project).getNode(sourceFolder).select();
		bot.menu("File").menu("New").menu("Package").click();
		bot.text(1).setText(packageName);
		bot.button("Finish").click();
	}

	public static void createSomeTestClass(SWTWorkbenchBot bot, String project, 
			String sourceFolder, String packageName, String className) {
		
		bot.tree().getTreeItem(project).expand();
		bot.tree().getTreeItem(project).getNode(sourceFolder).expand();
		bot.tree().getTreeItem(project).getNode(sourceFolder).getNode(packageName).select();
		bot.menu("File").menu("New").menu("Class").click();
		bot.text(3).setText(className);
		bot.button("Finish").click();
	}

	public static void setClassSource(SWTWorkbenchBot bot,
			String projectName, String sourceFolder, String packageName, 
			String className, String sourcePath) {
		
		SWTBotEclipseEditor classEditor = openEditor(bot, projectName, sourceFolder, packageName, className);
		classEditor.setText("");

		Bundle bundle = FrameworkUtil.getBundle(TestUiUtils.class);
	    URL url = FileLocator.find(bundle, new Path(sourcePath), null);

		try {
			InputStream istream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(istream));
			OutputStream ostream = new ByteArrayOutputStream();
			String inputLine;
			
			while((inputLine = in.readLine()) != null){
				ostream.write((inputLine + "\n").getBytes());
			}
			classEditor.setText(ostream.toString());
			in.close();
			classEditor.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SWTBotEclipseEditor openEditor(SWTWorkbenchBot bot, String projectName,
			String sourceFolder, String packageName, String className) {
		bot.tree().getTreeItem(projectName).expand();
		bot.tree().getTreeItem(projectName).getNode(sourceFolder).expand();
		bot.tree().getTreeItem(projectName).getNode(sourceFolder).getNode(packageName).expand();
		bot.tree().getTreeItem(projectName).getNode(sourceFolder).getNode(packageName).getNode(className + ".java").doubleClick();

		return bot.editorByTitle(className + ".java").toTextEditor();
	}
	
	public static void openNewEctFileWizard(SWTWorkbenchBot bot, String[] path){
		selectTreeNode(bot, path);
		bot.menu("File").menu("New").menu("Other...").click();
		selectTreeNode(bot, new String[]{"ecFeed", "New Equivalence Class Tree file"});
		bot.button("Next >").click();
	}

	public static void createEctFile(SWTWorkbenchBot bot, String projectName,
			String sourceFolder, String fileName) {
		bot.menu("File").menu("New").menu("Other...").click();
		selectTreeNode(bot, new String[]{"ecFeed", "New Equivalence Class Tree file"});
		bot.button("Next >").click();
		bot.text().setText("/" + projectName + "/" + sourceFolder);
		if(fileName != null && fileName != (Constants.DEFAULT_NEW_ECT_MODEL_NAME + "." + Constants.EQUIVALENCE_CLASS_FILE_EXTENSION)){
			bot.text(1).setText(fileName);
		} 
		if(bot.button("Finish").isEnabled()){
			bot.button("Finish").click();
		}
	}
	
	public static SWTBotTreeItem selectTreeNode(SWTWorkbenchBot bot, String[] path){
		if (path.length < 2) return null;
		SWTBotTreeItem item = bot.tree().getTreeItem(path[0]);
		item.expand();
		item.select();
		
		int i = 1;
		while(i < path.length){
			item = item.getNode(path[i]);
			item.expand();
			i++;
		}
		item.select();
		return item;
	}

	public static void openFile(SWTWorkbenchBot bot, String[] path) {
		SWTBotTreeItem fileItem = selectTreeNode(bot, path);
		fileItem.doubleClick();
	}

	public static SWTBotEclipseEditor openEditor(SWTWorkbenchBot bot, String[] path) {
		SWTBotTreeItem fileItem = selectTreeNode(bot, path);
		fileItem.doubleClick();
		return bot.editorByTitle(path[path.length - 1]).toTextEditor();
	}

	public static RootNode getModel(SWTWorkbenchBot bot, String[] path) {
		
		SWTBotEclipseEditor editor = openEditor(bot, path);
		String modelText = editor.getText();
		EcParser parser = new EcParser();
		return parser.parseEctFile(new ByteArrayInputStream(modelText.getBytes()));
	}

}
