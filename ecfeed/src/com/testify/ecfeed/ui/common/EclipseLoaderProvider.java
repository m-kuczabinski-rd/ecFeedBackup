package com.testify.ecfeed.ui.common;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.testify.ecfeed.adapter.java.ILoaderProvider;
import com.testify.ecfeed.adapter.java.ModelClassLoader;

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
						IPath path = project.getWorkspace().getRoot().getLocation();
						path = path.append(javaProject.getOutputLocation());
						urls.add(new URL("file", null, path.toOSString() + "/"));
						path = project.getLocation();
						path = path.append(javaProject.getOutputLocation().removeFirstSegments(1));
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

	public static ModelClassLoader createLoader(){
		List<URL> urls = new ArrayList<URL>();
		try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects){
				if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)){
					IJavaProject javaProject = JavaCore.create(project);
					IPath path = project.getWorkspace().getRoot().getLocation();
					path = path.append(javaProject.getOutputLocation());
					urls.add(new URL("file", null, path.toOSString() + "/"));
				}
			}
			if (fLoader != null) {
				fLoader.close();
			}
		} catch (Throwable e) {
		}
		fLoader = new ModelClassLoader(urls.toArray(new URL[]{}), null);
		return fLoader;
	}
}
