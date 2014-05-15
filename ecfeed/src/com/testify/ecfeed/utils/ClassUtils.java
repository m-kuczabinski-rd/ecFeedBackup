package com.testify.ecfeed.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.testify.ecfeed.model.PartitionNode;

public class ClassUtils {

	private static URLClassLoader classLoader = null;

	public static URLClassLoader getClassLoader(boolean create, ClassLoader parentLoader) {
		if ((classLoader == null) || create){
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
				if (classLoader != null) {
					classLoader.close();
				}
			} catch (Throwable e) {
			}
			classLoader = new URLClassLoader(urls.toArray(new URL[]{}), parentLoader);
		}
		return classLoader;
	}

	public static ArrayList<PartitionNode> defaultEnumPartitions(String typeName) {
		ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
		try {
			Class<?> typeClass = getClassLoader(true, null).loadClass(typeName);
			if (typeClass != null) {
				for (Object object: typeClass.getEnumConstants()) {
					partitions.add(new PartitionNode(object.toString(), object));
				}	
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return partitions;
	}

	public static Object defaultEnumExpectedValue(String typeName) {
		Object value = null;
		try {
			Class<?> typeClass = getClassLoader(true, null).loadClass(typeName);
			if (typeClass != null) {
				for (Object object: typeClass.getEnumConstants()) {
					value = object;
					break;
				}	
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}			
		return value;
	}

	public static Object enumPartitionValue(String valueString, String typeName, ClassLoader loader) {
		Object value = null;
		try {
			Class<?> typeClass = loader.loadClass(typeName);
			if (typeClass != null) {
				for (Object object: typeClass.getEnumConstants()) {
					if ((((Enum<?>)object).name()).equals(valueString)) {
						value = object;
						break;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return value;
	}

	public static Object parseEnumValue(String valueString, String typeName, ClassLoader loader) {
		return enumPartitionValue(valueString, typeName, loader);
	}
}
