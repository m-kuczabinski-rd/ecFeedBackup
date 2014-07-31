package com.testify.ecfeed.modelif.java;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class LoaderProvider {
	private static URLClassLoader fClassLoader = null;

	public static URLClassLoader getClassLoader(boolean create, ClassLoader parentLoader) {
		if ((fClassLoader == null) || create){
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
				if (fClassLoader != null) {
					fClassLoader.close();
				}
			} catch (Throwable e) {
			}
			fClassLoader = new URLClassLoader(urls.toArray(new URL[]{}), parentLoader);
		}
		return fClassLoader;
	}

	public static Class<?> loadClass(ClassLoader loader, String className) {
		try {
			return loader.loadClass(className);
		} catch (Throwable e) {
		}

		try {
			Class<?> typeClass = loader.loadClass(className.substring(0, className.lastIndexOf('.')));
			for (Class<?> innerClass : typeClass.getDeclaredClasses()) {
				if (innerClass.getCanonicalName().equals(className)) {
					return innerClass;
				}
			}
		} catch (Throwable e) {
		}

		return null;
	}

}
