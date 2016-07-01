package com.ecfeed.ui.common.utils;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import com.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.ui.common.Messages;

public class EclipsePackageFragmentGetter {

	public static IPackageFragment getPackageFragment(
			String name, 
			IFileInfoProvider fileInfoProvider) throws CoreException {

		IPackageFragmentRoot packageFragmentRoot = getPackageFragmentRoot(fileInfoProvider);
		IPackageFragment packageFragment = packageFragmentRoot.getPackageFragment(name);

		if(packageFragment.exists() == false){
			packageFragment = packageFragmentRoot.createPackageFragment(name, false, null);
		}
		return packageFragment;
	}

	private static IPackageFragmentRoot getPackageFragmentRoot(
			IFileInfoProvider fileInfoProvider) throws CoreException{

		if (fileInfoProvider == null) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_FILE_INFO_PROVIDER_NOT_NULL);
		}

		IPackageFragmentRoot root = fileInfoProvider.getPackageFragmentRoot();

		if(root == null){
			root = getAnySourceFolder(fileInfoProvider);
		}
		if(root == null){
			root = createNewSourceFolder("src", fileInfoProvider);
		}

		return root;
	}

	private static IPackageFragmentRoot getAnySourceFolder(
			IFileInfoProvider fFileInfoProvider) throws CoreException {

		if(fFileInfoProvider.getProject().hasNature(JavaCore.NATURE_ID)){
			IJavaProject project = JavaCore.create(fFileInfoProvider.getProject());

			for (IPackageFragmentRoot packageFragmentRoot: project.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
					return packageFragmentRoot;
				}
			}
		}
		return null;
	}

	private static IPackageFragmentRoot createNewSourceFolder(
			String name, 
			IFileInfoProvider fFileInfoProvider) throws CoreException {

		IProject project = fFileInfoProvider.getProject();
		IJavaProject javaProject = JavaCore.create(project);
		IFolder srcFolder = project.getFolder(name);

		int i = 0;
		while(srcFolder.exists()){
			String newName = name + i++;
			srcFolder = project.getFolder(newName);
		}
		srcFolder.create(false, true, null);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(srcFolder);

		IClasspathEntry[] entries = javaProject.getRawClasspath();
		IClasspathEntry[] updated = new IClasspathEntry[entries.length + 1];
		System.arraycopy(entries, 0, updated, 0, entries.length);
		updated[entries.length] = JavaCore.newSourceEntry(root.getPath());
		javaProject.setRawClasspath(updated, null);
		return root;
	}
}
