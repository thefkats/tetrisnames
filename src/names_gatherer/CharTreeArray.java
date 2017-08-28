package names_gatherer;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps hashes generated from notes to questions.
 * 
 * @author James-Beetham
 */
public class CharTreeArray {

	private ArrayList<Node> idArray;
	private int count;
	private Node root;

	public CharTreeArray() {
		load();
	}

	public CharTreeArray(List<String> list) {
		load();
		for (String s : list) {
			add(s);
		}
	}

	private void load() {
		root = new Node((char) 0);
		count = 0;
		idArray = new ArrayList<Node>();
	}

	private void add(String s) {
		if (s.equals("null"))
			return;
		String word = s.substring(0, s.indexOf(" "));
		int id = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.indexOf(",")));
		long time = Long.parseLong(s.substring(s.indexOf(",") + 1, s.indexOf(":")));
		String data = s.substring(s.indexOf(":") + 1);
		add(word, id, time, data);
	}

	/**
	 * Add the data to the tree using the hash as a key. Overwrites data in the
	 * array.
	 * 
	 * @param hash
	 *            String key, can't be null
	 * @param data
	 *            to store
	 * @return true if successfully added data, or false if failed to add or if name
	 *         was already there
	 */
	public boolean add(String name, int id, long time, String stats) {
		if (time == -1)
			time = System.currentTimeMillis();

		if (name == null)
			name = "-" + id;

		Node cur = getLastNode(name);
		String nameStored = getName(cur);

		if (cur.child != null) {
			cur = cur.child;
			while (cur.next != null && cur.next.c < cur.c)
				cur = cur.next;
			Node n = new Node(name.charAt(nameStored.length()));
			n.prev = cur;
			cur.next = n;
			n.parent = cur.parent;
			cur = n;
			nameStored += "" + name.charAt(nameStored.length());
		}

		for (int i = nameStored.length(); i < name.length(); i++) {
			Node n = new Node(name.charAt(i));
			n.parent = cur;
			cur.child = n;
			cur = n;
		}

		count++;
		cur.id = id;
		cur.stats = stats;
		cur.time = time;
		while (idArray.size() <= cur.id)
			idArray.add(null);
		idArray.set(cur.id, cur);
		cur.isWord = true;
		return true;

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

	public int arrSize() {
		return idArray.size();
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
		String output = "";
		if (n == null || spaces == null)
			return output;

		output += spaces + n.c + "\n";
		if (n.child == null)
			return output;
		n = n.child;
		spaces += " ";

		while (n != null) {
			output += toStringHelper(n, spaces);
			n = n.next;
		}

		return output;
	}

	public ArrayList<String> list() {
		ArrayList<String> list = new ArrayList<String>();
		// listHelper(list, root.child);
		// java.util.Collections.sort(list);
		// System.out.println(toString());
		// System.out.println("size: " + size());
		// System.out.println("Arr size: " + arrSize());
		for (Node n : idArray)
			list.add(getName(n) + ((n == null) ? "" : n.toString()));
		return list;
	}

	private void listHelper(ArrayList<String> list, Node n) {
		if (n == null)
			return;
		if (n.isWord)
			list.add(getName(n) + n.toString());
		listHelper(list, n.child);
		listHelper(list, n.next);
	}

	private class Node {
		public char c;
		public boolean isWord;
		public int id;
		public long time;
		public String stats;
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
			stats = null;
			id = -1;
		}

		@Override
		public String toString() {
			return " " + id + "," + time + ":" + ((stats == null) ? "" : stats.trim());
		}
	}

	public static void main(String[] args) {
		CharTreeArray cta = new CharTreeArray();
		cta.add("hi1", 1, -1, null);
		cta.add("hi2", 2, -1, null);
		cta.add("h4a", 3, -1, null);
		for (String s : cta.list())
			System.out.println(s);
	}
}
