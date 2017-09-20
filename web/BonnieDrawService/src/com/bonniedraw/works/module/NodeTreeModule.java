package com.bonniedraw.works.module;

import java.util.ArrayList;
import java.util.List;

public class NodeTreeModule {
	private int nodeId;
	private String name;
	private boolean disabled;
	private boolean expanded;
	private List<NodeTreeModule> children;
	private int categoryLevel;
	private int categoryParentId;

	public NodeTreeModule() {
		super();
		this.children = new ArrayList<NodeTreeModule>();
		this.expanded = false;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = !disabled;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public List<NodeTreeModule> getChildren() {
		return children;
	}

	public void setChildren(List<NodeTreeModule> children) {
		this.children = children;
	}

	public int getCategoryLevel() {
		return categoryLevel;
	}

	public void setCategoryLevel(int categoryLevel) {
		this.categoryLevel = categoryLevel;
	}

	public int getCategoryParentId() {
		return categoryParentId;
	}

	public void setCategoryParentId(int categoryParentId) {
		this.categoryParentId = categoryParentId;
	}

}
