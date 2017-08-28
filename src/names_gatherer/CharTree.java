package names_gatherer;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps hashes generated from notes to questions.
 * 
 * @author James-Beetham
 */
public class CharTree {

	private int count;
	private Node root;

	public CharTree() {
		root = new Node((char) 0);
		count = 0;
	}

	public CharTree(List<String> list) {
		root = new Node((char) 0);
		count = 0;
		for (String s : list)
			add(s);
	}

	/**
	 * Add the data to the tree using the hash as a key.
	 * 
	 * @param hash
	 *            String key, can't be null
	 * @param data
	 *            to store
	 * @return true if successfully added data, or false if failed to add or if name
	 *         was already there
	 */
	public boolean add(String name) {
		if (name == null)
			return false;
		Node cur = getLastNode(name);
		// Check if name is already listed
		String nameStored = getName(cur);
		if (nameStored.equals(name))
			return false;

		if (cur.child != null) {
			cur = cur.child;
			
			char character = name.charAt(nameStored.length());
			// find horizontal position
			while (cur.next != null && character > cur.c)
				cur = cur.next;
			Node n = new Node(character);
			n.next = cur.next;
			cur.next = n;
			n.prev = cur;
			if (n.next != null)
				n.next.prev = n;
			
			n.parent = cur.parent;
			cur = n;
			nameStored += name.charAt(nameStored.length());
		}

		for (int i = nameStored.length(); i < name.length(); i++) {
			Node n = new Node(name.charAt(i));
			n.parent = cur;
			cur.child = n;
			cur = n;
		}
		count++;
		return cur.isWord = true;
	}

	/**
	 * Removes the node specified by hash.
	 * 
	 * @param hash
	 *            string telling which node to remove
	 * @return true if successfully removed
	 */
	public boolean remove(String name) {
		Node n = getNode(name);
		if (n == null)
			return false;
		remove(n);
		count--;
		return true;
	}

	private void remove(Node n) {
		if (n == null)
			return;
		n.isWord = false;
		while (!n.isWord && n != root) {
			if (n.child != null)
				return;
			if (n.parent.child == n)
				n.parent.child = n.next;

			if (n.next != null) {
				if (n.prev != null) {
					n.prev.next = n.next;
					n.next.prev = n.prev;
				}
			}

			if (n.prev != null)
				n.prev.next = null;
			n = n.parent;
		}
	}

	/**
	 * Finds the node specified by the string name.
	 * 
	 * @param name
	 *            location of node
	 * @return node specified or the parent of the last node that follows the path
	 */
	private Node getLastNode(String name) {
		if (name == null)
			return null;
		Node cur = root;
		for (int i = 0; i < name.length(); i++) {
			if (cur.child == null)
				return cur;
			cur = cur.child;
			while (cur.c != name.charAt(i) && cur.next != null)
				cur = cur.next;

			// If not found
			if (cur.c != name.charAt(i))
				return cur.parent;
		}

		return cur;
	}

	/**
	 * Finds the name specified by a specific node.
	 * 
	 * @param n
	 *            node to generate from
	 * @return string of the name
	 */
	private String getName(Node n) {
		if (n == null)
			return null;
		String output = "";
		while (n != root) {
			output = n.c + output;
			n = n.parent;
		}
		return output;
	}

	/**
	 * Checks if the tree contains String name.
	 * 
	 * @param name
	 * @return
	 */
	public boolean contains(String name) {
		return getNode(name) != null;
	}

	public int size() {
		return count;
	}

	/**
	 * Same as getLastNode but returns null if word is not present.
	 * 
	 * @param name
	 * @return node specified by the name, or null
	 */
	private Node getNode(String name) {
		Node n = getLastNode(name);
		if (n == null)
			return null;
		return n.isWord && name.equals(getName(n)) ? n : null;
	}

	@Override
	public String toString() {
		return toStringHelper(root.child, "");
	}

	private String toStringHelper(Node n, String spaces) {
		if (n == null || spaces == null)
			return "";

		String output = spaces + n.c + "\n";

		output += toStringHelper(n.child, spaces + " ");
		output += toStringHelper(n.next, spaces);
		return output;
	}

	public ArrayList<String> list() {
		ArrayList<String> list = new ArrayList<String>();
		listHelper(list, root.child);
		// java.util.Collections.sort(list);
		return list;
	}

	private void listHelper(ArrayList<String> list, Node n) {
		if (n == null)
			return;
		if (n.isWord)
			list.add(getName(n));
		listHelper(list, n.child);
		listHelper(list, n.next);
	}

	private class Node {
		public char c;
		public boolean isWord;
		public Node next;
		public Node parent;
		public Node child;
		public Node prev;

		public Node(char c) {
			isWord = false;
			this.c = c;
			next = null;
			child = null;
			parent = null;
			prev = null;
		}
	}

	public static void main(String[] args) {
		CharTree ct = new CharTree();
		ct.add("bbb");
		ct.add("ccc");
		ct.add("aaa");
		System.out.println(ct.toString());
	}
}
