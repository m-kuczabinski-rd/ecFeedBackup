/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Mariusz Strozynski (m.strozynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;

public class ModelLabelDecorator implements ILabelDecorator {

	Map<List<Image>, Image> fFusedImages;
	
	private class DecorationProvider implements IModelVisitor{
		GenericNodeInterface fNodeInterface;

		public DecorationProvider(){
			fNodeInterface = new GenericNodeInterface(null);
		}
		
		@Override
		public Object visit(RootNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
		}

		@Override
		public Object visit(PartitionNode node) throws Exception {
			List<Image> decorations = new ArrayList<Image>();
			decorations.add(implementationStatusDecoration(node));
			if(node.isAbstract()){
				decorations.add(getImage("abstract.png"));
			}
			return decorations;
		}

		private Image implementationStatusDecoration(GenericNode node) {
			switch (fNodeInterface.implementationStatus(node)){
			case IMPLEMENTED:
				return getImage("implemented.png");
			case PARTIALLY_IMPLEMENTED:
				return getImage("partially_implemented.png");
			case NOT_IMPLEMENTED:
				return getImage("unimplemented.png");
			case IRRELEVANT:
			default:
				return null;
			}
		}
		
	}

	
	public ModelLabelDecorator() {
		fFusedImages = new HashMap<List<Image>, Image>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public Image decorateImage(Image image, Object element) {
		if(element instanceof GenericNode){
			try{
				List<Image> decorations = (List<Image>)((GenericNode)element).accept(new DecorationProvider());
				List<Image> all = new ArrayList<Image>(decorations);
				all.add(0, image);
				if(fFusedImages.containsKey(all) == false){
					Image decorated = new Image(Display.getCurrent(), image.getImageData());
					for(Image decoration : decorations){
						if(decoration != null){
							decorated = fuseImages(decorated, decoration, 0, 0);
						}
					}
					fFusedImages.put(decorations, decorated);
				}
				return fFusedImages.get(decorations);
			}catch(Exception e){}
		}
    	return image;
    }

	@Override
    public String decorateText(String text, Object element) {
    	return text;
    }

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	private Image getImage(String file) {
	    Bundle bundle = FrameworkUtil.getBundle(ModelLabelDecorator.class);
	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
	    ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
	    return descriptor.createImage();
	}

	private Image fuseImages(Image icon, Image decorator, int x, int y){
		ImageData idIcon = (ImageData)icon.getImageData().clone();
		ImageData idDecorator = decorator.getImageData();
		if(idIcon.width <= x || idIcon.height <= y){
			return icon;
		}
		int rbw = (idDecorator.width + x > idIcon.width) ? (idDecorator.width + x - idIcon.width) : idDecorator.width;
		int rbh = (idDecorator.height + y > idIcon.height) ? (idDecorator.height + y - idIcon.height) : idDecorator.height;		
		
		int indexa = y*idIcon.scanlinePad + x;
		int indexb = 0;
		
		for(int row = 0; row < rbh; row ++){
			for(int col = 0; col < rbw; col++){
				if(idDecorator.alphaData[indexb] < 0){
					idIcon.alphaData[indexa] = (byte)-1;
					idIcon.data[4*indexa]=idDecorator.data[4*indexb];
					idIcon.data[4*indexa+1]=idDecorator.data[4*indexb+1];
					idIcon.data[4*indexa+2]=idDecorator.data[4*indexb+2];
				}
				indexa += 1;
				indexb += 1;
			}
			indexa += x;
		}
		return new Image(Display.getDefault(), idIcon);
	}
}
