/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class LinkingMethodsViewer extends TreeViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;

	private GlobalParameterNode fParameter;

	private class LinkingMethodsContentProvider extends TreeNodeContentProvider{
		public final Object[] EMPTY_ARRAY = new Object[] {};

		@Override
		public Object[] getElements(Object inputElement){
			if(inputElement instanceof GlobalParameterNode){
				GlobalParameterNode parameter = (GlobalParameterNode)inputElement;
				if(parameter.getParametersParent() instanceof ClassNode){
					return getLinkingMethods(parameter, (ClassNode)parameter.getParametersParent()).toArray();
				}else{
					Set<ClassNode> linkingClasses = new LinkedHashSet<ClassNode>();
					for(MethodParameterNode linker : parameter.getLinkers()){
						linkingClasses.add(linker.getMethod().getClassNode());
					}
					return linkingClasses.toArray();
				}
			}else{
				return EMPTY_ARRAY;
			}
		}

		@Override
		public Object[] getChildren(Object parentElement){
			if(parentElement instanceof ClassNode){
				return getLinkingMethods(fParameter, (ClassNode)parentElement).toArray();
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element){
			if(element instanceof AbstractNode){
				return ((AbstractNode)element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element){
			return getChildren(element).length > 0;
		}

		private Set<MethodNode> getLinkingMethods(GlobalParameterNode parameter, ClassNode parentClass) {
			Set<MethodNode> linkingMethods = new LinkedHashSet<>();
			for(MethodParameterNode linker : parameter.getLinkers()){
				if(linker.getMethod().getClassNode() == parentClass){
					linkingMethods.add(linker.getMethod());
				}
			}
			return linkingMethods;
		}
	}

	private class LinkingMethodsLabelProvider extends LabelProvider{

		@Override
		public String getText(Object element){
			if(element instanceof ClassNode){
				return ((ClassNode)element).getName();
			} else if(element instanceof MethodNode){
				return JavaUtils.simplifiedToString((MethodNode)element);
			}
			return "";
		}
	}

	public LinkingMethodsViewer(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, STYLE);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);

		getSection().setText("Linking methods");

		addDoubleClickListener(new SelectNodeDoubleClickListener(sectionContext.getMasterSection()));

		getTreeViewer().setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
	}

	@Override
	protected IContentProvider viewerContentProvider() {
		return new LinkingMethodsContentProvider();
	}

	@Override
	protected IBaseLabelProvider viewerLabelProvider() {
		return new LinkingMethodsLabelProvider();
	}

	public void setInput(GlobalParameterNode parameter){
		fParameter = parameter;
		super.setInput(parameter);
	}
}
