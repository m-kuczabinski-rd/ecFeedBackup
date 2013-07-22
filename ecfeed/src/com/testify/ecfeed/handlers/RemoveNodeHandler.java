package com.testify.ecfeed.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.testify.ecfeed.editor.EcMultiPageEditor;
//import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class RemoveNodeHandler extends AbstractHandler implements IHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		RootNode root = (RootNode)((GenericNode)selection.getFirstElement()).getRoot();
		
		Iterator iterator = selection.iterator();
		while(iterator.hasNext()){
			GenericNode node = (GenericNode)iterator.next();
			if(node != null && node.getParent() != null){
				if(node instanceof PartitionNode && node.getParent().getChildren().size() > 1){
					removePartitionNode((PartitionNode) node);
				}
				else{
					node.getParent().removeChild(node);
				}
			}
		}
		
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		IPage page = ((ContentOutline)part).getCurrentPage();
//		EcMultiPageEditor editor = ((EcContentOutlinePage)page).getEditor();
//
//		editor.updateModel(root);
		return null;
	}

	private void removePartitionNode(PartitionNode partition) {
		CategoryNode category = (CategoryNode)partition.getParent();
		partition.getParent().removeChild(partition);
		
		//change references to removed partition in all test cases
		PartitionNode substitutePartition = category.getPartitions().elementAt(0); 
		MethodNode method = (MethodNode) category.getParent();
		int categoryIndex = method.getCategories().indexOf(category);
		
		for(TestCaseNode testCase : method.getTestCases()){
			if(testCase.getTestData().elementAt(categoryIndex) == partition){
				testCase.getTestData().setElementAt(substitutePartition, categoryIndex);
			}
		}
	}
}
