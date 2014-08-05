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

package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.ClassInterface;

public class OtherMethodsViewer extends CheckboxTableViewerSection {
	
	public final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	private ClassInterface fClassIf;

	private class AddSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fClassIf.addMethods(getSelectedMethods(), OtherMethodsViewer.this, getUpdateListener());
		}
	}
	
	public OtherMethodsViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		fClassIf = new ClassInterface(operationManager);
		addButton("Add selected", new AddSelectedAdapter());
	}
	
	@Override
	protected void createTableColumns() {
	}
	
	public void setInput(ClassNode classNode){
		fClassIf.setTarget(classNode);
		setText("Other methods in " + classNode.getLocalName());
		super.setInput(fClassIf.getOtherMethods());
	}
	
	public void setVisible(boolean visible) {
		GridData gd = (GridData)getSection().getLayoutData();
		gd.exclude = !visible;
		getSection().setLayoutData(gd);
		getSection().setVisible(visible);
	}
	
	public int getItemsCount(){
		return fClassIf.getOtherMethods().size();
	}
	
	public List<MethodNode> getSelectedMethods(){
		List<MethodNode> methods = new ArrayList<MethodNode>();
		for(Object object : getCheckboxViewer().getCheckedElements()){
			if(object instanceof MethodNode){
				methods.add((MethodNode)object);
			}
		}
		return methods;

	}
}
