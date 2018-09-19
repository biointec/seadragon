package tree;

/*
 * #%L
 * Jesse
 * %%
 * Copyright (C) 2017 Intec/UGent - Ine Melckenbeeck
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import graphlets.AbstractGraphlet;

/**
 * Class for TreeNodes that are used for symmetry-breaking constraints when
 * counting the OrbitRepresentatives.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class ConditionNode<T extends AbstractGraphlet<U>, U extends Comparable<U>> extends TreeNode<T, U> {

	private int first, second;
	private TreeNode<T, U> child;

	/**
	 * Creates a new ConditionNode with the specified parameters.
	 * 
	 * @param parent
	 * @param first
	 * @param second
	 * @param tree
	 */
	ConditionNode(TreeNode<T, U> parent, int first, int second) {
		super(parent, parent.representation);
		this.first = first;
		this.second = second;
	}

	@Override
	public void removeChild(TreeNode<T, U> t) {
		if (t.equals(child)) {
			child = null;
		}
	}

	@Override
	public boolean isLeaf() {
		return child == null;
	}

	@Override
	public Collection<TreeNode<T, U>> getChildren() {
		List<TreeNode<T, U>> result = new ArrayList<>();
		if (child != null)
			result.add(child);
		return result;
	}

	@Override
	public void replaceChild(TreeNode<T, U> original, TreeNode<T, U> newNode) {
		if (original.equals(child)) {
			child = newNode;
		}
	}

	/**
	 * Inserts this ConditionNode in the place of the specified TreeNode. It will
	 * become the child of the given TreeNode's parent, with the same parameters as
	 * the given TreeNode. The given TreeNode will become this ConditionNode's
	 * child.
	 * 
	 * @param tn
	 *            The TreeNode in whose place this ConditionNode will be inserted.
	 */
	void insert(TreeNode<T, U> tn) {
		if (tn.getParent() != null) {
			this.parent = tn.parent;
			tn.parent.replaceChild(tn, this);
			tn.parent = this;
			this.child = tn;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		ConditionNode<T, U> other = (ConditionNode<T, U>) obj;
		if (first != other.first)
			return false;
		if (second != other.second)
			return false;
		if (child == null)
			return other.child == null;
		if (!child.equals(other.child))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "ConditionNode " + first + "<" + second;
		return result;
	}

	public int getFirst() {
		return first;
	}

	public int getSecond() {
		return second;
	}

	/**
	 * Returns this ConditionNode's child TreeNode.
	 *
	 * @return this ConditionNode's child TreeNode.
	 */
	public TreeNode<T, U> getChild() {
		return child;
	}

	void setChild(TreeNode<T,U> child) {
		this.child = child;
	}

	@Override
	public void print(String spaces) {
		System.out.println(this);
		System.out.print(spaces + " ");
		child.print(spaces + " ");

	}

//	@Override
//	public int numberOfNodes() {
//		return child.numberOfNodes();
//	}
}