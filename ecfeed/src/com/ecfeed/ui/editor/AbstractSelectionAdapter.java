/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public abstract class AbstractSelectionAdapter extends SelectionAdapter {

	@Override
	public abstract void widgetSelected(SelectionEvent e);
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e){
		widgetSelected(e);
	}
}
