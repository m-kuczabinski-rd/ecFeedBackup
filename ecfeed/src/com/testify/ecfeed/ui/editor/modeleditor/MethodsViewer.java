package com.testify.ecfeed.ui.editor.modeleditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ModelUtils;

public class MethodsViewer extends CheckboxTableViewerSection {

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private ColorManager fColorManager;
	private ClassNode fSelectedClass;
	
	private class RemoveSelectedMethodsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_METHODS_TITLE, 
					Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
				removeMethods(getCheckboxViewer().getCheckedElements());
			}
		}

		private void removeMethods(Object[] checkedElements) {
			for(Object object : checkedElements){
				if(object instanceof MethodNode){
					fSelectedClass.removeMethod((MethodNode)object);
				}
			}
			modelUpdated();
		}
	}
	
	private class MethodsLabelProvider extends ColumnLabelProvider{
		public MethodsLabelProvider() {
			fColorManager = new ColorManager();
		}
		
		@Override
		public String getText(Object element){
			MethodNode method = (MethodNode)element;
			String result = method.toString();
			if(methodObsolete(method)){
				result += " [obsolete]";
			}
			return result;
		}

		@Override
		public Color getForeground(Object element){
			MethodNode method = (MethodNode)element;
			if(methodObsolete(method)){
				return fColorManager.getColor(ColorConstants.OBSOLETE_METHOD);
			}
			return null;
		}
		
		private boolean methodObsolete(MethodNode method) {
			List<MethodNode> obsoleteMethods = getObsoleteMethods();
			for(MethodNode obsoleteMethod : obsoleteMethods){
				if(obsoleteMethod.toString().equals(method.toString())){
					return true;
				}
			}
			return false;
		}
		
		private List<MethodNode> getObsoleteMethods(){
			if(fSelectedClass != null){
				return ModelUtils.getObsoleteMethods(fSelectedClass, fSelectedClass.getQualifiedName());
			}
			return new ArrayList<MethodNode>();
		}
	}
	
	public MethodsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);

		setText("Methods");
		addButton("Remove selected", new RemoveSelectedMethodsAdapter());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		addColumn("Methods", 800, new MethodsLabelProvider());
	}
	
	public void setInput(ClassNode classNode){
		fSelectedClass = classNode;
		super.setInput(classNode.getMethods());
	}

	@Override
	protected boolean tableLinesVisible() {
		return true;
	}

	@Override
	protected boolean tableHeaderVisible() {
		return false;
	}
	
}
