package com.testify.ecfeed.outline;
import java.io.ByteArrayInputStream;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.testify.ecfeed.editors.EcEditor;
import com.testify.ecfeed.model.Node;
import com.testify.ecfeed.parsers.EcParser;


public class EcContentProvider implements ITreeContentProvider {

	public static final Object[] EMPTY_ARRAY = new Object[]{};
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof EcEditor){
			EcParser parser = new EcParser();
			EcEditor editor = (EcEditor)inputElement;
			IDocument doc = editor.getDocument();
			Node modelRoot = parser.parseEctFile(new ByteArrayInputStream(doc.get().getBytes()));
			return new Object[]{modelRoot};
		}
		else if(inputElement instanceof Node){
			return ((Node)inputElement).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Node){
			return ((Node)parentElement).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof Node){
			return ((Node)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Node){
			return ((Node)element).hasChildren();
		}
		return false;
	}

}
