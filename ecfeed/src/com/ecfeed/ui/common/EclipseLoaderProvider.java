/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.ecfeed.core.adapter.java.ILoaderProvider;
import com.ecfeed.core.adapter.java.ModelClassLoader;

public class EclipseLoaderProvider implements ILoaderProvider {

	private static ModelClassLoader fLoader;

	public ModelClassLoader getLoader(boolean create, ClassLoader parent){
		if ((fLoader == null) || create){
			List<URL> urls = new ArrayList<URL>();
			try {
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject project : projects){
					if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)){
						IJavaProject javaProject = JavaCore.create(project);
						IPath path = project.getLocation().append(javaProject.getOutputLocation().removeFirstSegments(1));
						urls.add(new URL("file", null, path.toOSString() + "/"));
						IClasspathEntry table[] = javaProject.getResolvedClasspath(true);
						for (int i = 0; i < table.length; ++i) {
							if (table[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
								urls.add(new URL("file", null, table[i].getPath().toOSString()));
								path = project.getLocation();
								path = path.append(table[i].getPath().removeFirstSegments(1));
								urls.add(new URL("file", null, path.toOSString()));
							}
						}
					}
				}
				if (fLoader != null) {
					fLoader.close();
				}
			} catch (Throwable e) {
			}
			fLoader = new ModelClassLoader(urls.toArray(new URL[]{}), parent);
		}
		return fLoader;
	}
}
