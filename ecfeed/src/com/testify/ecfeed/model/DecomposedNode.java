package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class DecomposedNode extends AbstractNode{

	private List<ChoiceNode> fChoices;

	public DecomposedNode(String name) {
		super(name);
		fChoices = new ArrayList<ChoiceNode>();
	}

	public abstract ParameterNode getParameter();

	@Override
	public List<? extends AbstractNode> getChildren(){
		return fChoices;
	}

	public List<ChoiceNode> getChoices() {
		return fChoices;
	}

	public void addChoice(ChoiceNode choice) {
		addChoice(choice, fChoices.size());
	}

	public void addChoice(ChoiceNode choice, int index) {
			fChoices.add(index, choice);
			choice.setParent(this);
	}

	public ChoiceNode getChoice(String qualifiedName) {
		return (ChoiceNode)getChild(qualifiedName);
	}

	public boolean removeChoice(ChoiceNode choice) {
		if(fChoices.contains(choice) && fChoices.remove(choice)){
			choice.setParent(null);
			return true;
		}
		return false;
	}

	public void replaceChoices(List<ChoiceNode> newChoices) {
		fChoices.clear();
		fChoices.addAll(newChoices);
		for(ChoiceNode p : newChoices){
			p.setParent(this);
		}
	}

	public List<ChoiceNode> getLeafChoices() {
		List<ChoiceNode> result = new ArrayList<ChoiceNode>();
		for(ChoiceNode p : fChoices){
			if(p.isAbstract() == false){
				result.add(p);
			}
			result.addAll(p.getLeafChoices());
		}
		return result;
	}

	public Set<ChoiceNode> getAllChoices() {
		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();
		for(ChoiceNode p : fChoices){
			result.add(p);
			result.addAll(p.getAllChoices());
		}
		return result;
	}

	public Set<String> getAllChoiceNames() {
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : fChoices){
			result.add(p.getQualifiedName());
			result.addAll(p.getAllChoiceNames());
		}
		return result;
	}

	public Set<String> getChoiceNames() {
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : fChoices){
			result.add(p.getName());
		}
		return result;
	}

	public Set<ChoiceNode> getLabeledChoices(String label) {
		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();
		for(ChoiceNode p : fChoices){
			if(p.getLabels().contains(label)){
				result.add(p);
			}
			result.addAll(p.getLabeledChoices(label));
		}
		return result;
	}

	public Set<String> getLeafLabels() {
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : getLeafChoices()){
			result.addAll(p.getAllLabels());
		}
		return result;
	}

	public Set<String> getLeafChoiceValues(){
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : getLeafChoices()){
			result.add(p.getValueString());
		}
		return result;
	}

	public Set<String> getLeafChoiceNames(){
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : getLeafChoices()){
			result.add(p.getQualifiedName());
		}
		return result;
	}
}