/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.rcp3;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.testify.ecfeed.utils.EclipseHelper;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}
	
	@Override
	public void postWindowCreate() {
		hideSearchMenu();
	}	

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1200, 800));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("ecFeed");
	}

	@Override
	public void postWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.getWindow().getShell().setMaximized(true);
	}

	private void hideSearchMenu() {
		IWorkbenchWindow workbenchWindow = EclipseHelper.getActiveWorkbenchWindow();
		IContributionItem[] items = ((WorkbenchWindow)workbenchWindow).getMenuBarManager().getItems();
		
		for (IContributionItem item : items)
		{
			if (item.getId().equals("org.eclipse.search.menu")) {
				item.setVisible(false);
			}
		}
	}
}

