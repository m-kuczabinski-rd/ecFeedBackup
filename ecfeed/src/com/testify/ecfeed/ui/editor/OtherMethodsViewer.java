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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.modelif.ClassInterface;

public class OtherMethodsViewer extends CheckboxTableViewerSection {
	
	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	private final static int VIEWER_STYLE = SWT.BORDER;

	private ClassInterface fClassIf;

	private class AddSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fClassIf.addMethods(getSelectedMethods(), OtherMethodsViewer.this);
		}
	}
	
	public OtherMethodsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent, parent.getOperationManager());
		fClassIf = new ClassInterface();
		addButton("Add selected", new AddSelectedAdapter());
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
	
	@Override
	protected void createTableColumns() {
	}

	@Override
	protected int viewerStyle(){
		return VIEWER_STYLE;
	}
}
