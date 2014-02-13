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

package com.testify.ecfeed.runner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TestClassLoader extends ClassLoader{
	String fOutputFolder;
	
	public TestClassLoader(ClassLoader parent, String outputFolder){
		super(parent);
		fOutputFolder = outputFolder;
	}
	
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Class loadClass(String name) throws ClassNotFoundException{
		try{
			return super.loadClass(name);
		}
		catch(ClassNotFoundException e){};
		String path = getClassFilePath(name);
		try {
			byte[] classData = getClassData(path);
			Class loadedClass = defineClass(name, classData, 0, classData.length);
			resolveClass(loadedClass);
			return loadedClass;
		} catch (FileNotFoundException e) {
			throw new ClassNotFoundException("Class file " + path + " not found");
		} catch (IOException e) {
			throw new ClassNotFoundException("Error while reading class file: " + path);
		}
	}

	private byte[] getClassData(String path) throws FileNotFoundException,
			IOException {
		InputStream istream = new FileInputStream(new File(path));
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int data;
		while((data = istream.read()) != -1){
			buffer.write(data);
		}
		istream.close();
		byte[] classData = buffer.toByteArray();
		return classData;
	}

	protected String getClassFilePath(String name) {
		String localPath = name;
		localPath = localPath.replaceAll("\\.", "/");
		return fOutputFolder + "/" + localPath + ".class"; 
	}
}
