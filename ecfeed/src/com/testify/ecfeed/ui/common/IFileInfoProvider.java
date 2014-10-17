package com.testify.ecfeed.ui.common;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public interface IFileInfoProvider {
	public IFile getFile();
	public IProject getProject();
	public IPackageFragmentRoot getPackageFragmentRoot();
	public IPath getPath();
}
