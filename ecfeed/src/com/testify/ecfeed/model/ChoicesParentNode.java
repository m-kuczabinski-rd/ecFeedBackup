package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class ChoicesParentNode extends AbstractNode{

	protected List<ChoiceNode> fChoices;

	public ChoicesParentNode(String name) {
		super(name);
		fChoices = new ArrayList<ChoiceNode>();
	}

	@Override
	public List<? extends AbstractNode> getChildren(){
		return fChoices;
	}

	public abstract MethodParameterNode getParameter();

	public void addChoice(ChoiceNode choice) {
		addChoice(choice, fChoices.size());
	}

	public void addChoice(ChoiceNode choice, int index) {
			fChoices.add(index, choice);
			choice.setParent(this);
	}

	public void addChoices(List<ChoiceNode> choices) {
		for(ChoiceNode p : choices){
			addChoice(p);
		}
	}

	public List<ChoiceNode> getChoices() {
		return fChoices;
	}

	public ChoiceNode getChoice(String qualifiedName) {
		return (ChoiceNode)getChild(qualifiedName);
	}

	public List<ChoiceNode> getLeafChoices() {
		return getLeafChoices(getChoices());
	}

	public Set<ChoiceNode> getAllChoices() {
		return getAllChoices(getChoices());
	}

	public Set<String> getChoiceNames() {
		return getChoiceNames(getChoices());
	}

	public Set<String> getAllChoiceNames() {
		return getChoiceNames(getAllChoices());
	}

	public Set<String> getLeafChoiceNames(){
		return getChoiceNames(getLeafChoices());
	}

	public Set<ChoiceNode> getLabeledChoices(String label) {
		return getLabeledChoices(label, getChoices());
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

	protected List<ChoiceNode> getLeafChoices(Collection<ChoiceNode> choices) {
		List<ChoiceNode> result = new ArrayList<ChoiceNode>();
		for(ChoiceNode p : choices){
			if(p.isAbstract() == false){
				result.add(p);
			}
			result.addAll(p.getLeafChoices());
		}
		return result;
	}

	protected Set<ChoiceNode> getAllChoices(Collection<ChoiceNode> choices) {
		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();
		for(ChoiceNode p : choices){
			result.add(p);
			result.addAll(p.getAllChoices());
		}
		return result;
	}

	protected Set<String> getChoiceNames(Collection<ChoiceNode> choices) {
		Set<String> result = new LinkedHashSet<String>();
		for(ChoiceNode p : choices){
			result.add(p.getQualifiedName());
		}
		return result;
	}

	protected Set<ChoiceNode> getLabeledChoices(String label, List<ChoiceNode> choices) {
		Set<ChoiceNode> result = new LinkedHashSet<ChoiceNode>();
		for(ChoiceNode p : choices){
			if(p.getLabels().contains(label)){
				result.add(p);
			}
			result.addAll(p.getLabeledChoices(label));
		}
		return result;
	}
}