package names_gatherer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class QQueue<T> implements Queue {
	private Node top;
	private Node bot;
	private int size;

	public QQueue() {
		top = new Node(null);
		bot = new Node(null);
		top.prev = bot;
		bot.prev = top;
		size = 0;
	}

	private class Node {
		public T data;
		public Node prev;

		public Node(T s) {
			data = s;
			prev = null;
		}
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Object e) {
		Node n = new Node((T) e);
		bot.prev.prev = n;
		bot.prev = n;
		n.prev = bot;
		size++;
		return true;
	}

	@Override
	public T element() {
		if (size == 0)
			throw new IllegalStateException("Queue is empty");
		return top.prev.data;
	}

	@Override
	public boolean offer(Object e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object peek() {
		return size == 0 ? null : top.prev.data;
	}

	@Override
	public T poll() {
		if (size == 0)
			return null;
		return remove();
	}

	@Override
	public T remove() {
		Node n = top.prev;
		top.prev = top.prev.prev;
		T d = n.data;
		n.data = null;
		size--;
		return d;
	}
}
