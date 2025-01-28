public class MyTuple<E> {
	private int size, capacity;
	E[] array = (E[]) new Object[capacity];
	
	public MyTuple() {
		capacity = 5;
		array = (E[]) new Object[capacity];
	}
	
	private void checkCapacity() {
		if (size == capacity) {
			capacity *= 2;
			E[] newArray = (E[]) new Object[capacity];
			for (int i = 0; i < array.length; i++) {
				newArray[i] = array[i];
			}
			array = newArray;
		}
	}
	
	public E get(int pos) {
		if (pos < 0) throw new MyTupleException("MyTuple", "get", "pos is less than zero.");
		if (size == 0) throw new MyTupleException("MyTuple", "get", "size is zero, nothing to get");
		if (pos >= size) throw new MyTupleException("MyTuple", "get", "size is less than or equal to position.");
		return array[pos];
	}
	
	public void set(int pos, E value) {
		if (pos < 0) throw new MyTupleException("MyTuple", "set", "pos is less than zero.");
		if (size == 0) throw new MyTupleException("MyTuple", "set", "size is zero, nothing to set");
		if (pos >= size) throw new MyTupleException("MyTuple", "set", "size is less than or equal to position.");
		array[pos] = value;
	}
	
	public void add(int pos, E value) {
		checkCapacity();
		if (pos < 0) throw new MyTupleException("MyTuple", "add", "pos is less than zero.");
		if (pos > size) throw new MyTupleException("MyTuple", "add", "size is less than position.");
		if (pos < size) {
			for (int i = size-1; i >= pos; i--) {
				array[i+1] = array[i];
			}
		}
		array[pos] = value;
		size++;
	}
	
	public E remove(int pos) {
		if (pos < 0) throw new MyTupleException("MyTuple", "remove", "pos is less than zero.");
		if (size == 0) throw new MyTupleException("MyTuple", "remove", "size is zero, nothing to remove");
		if (pos >= size) throw new MyTupleException("MyTuple", "remove", "size is less than or equal to position.");
		E temp = array[pos];
		if (pos != size) {
			for (int i = pos; i < size-1; i++) {
				array[i] = array[i+1];
			}
		}
		size--;
		return temp;
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public int capacity() {
		return capacity;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < size; i++) {
			sb.append(array[i] + (i < size - 1 ? ", " : ""));
		}
		return sb.append("}").toString();
	}
}
