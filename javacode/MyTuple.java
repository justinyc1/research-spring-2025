package javaCode;
import java.util.List;
import java.util.ArrayList;
/*
 * tuple: an ordered sequence of values that can repeat
 * this tuple will also be non-decreasing, and consist of integers
 * 
 */
public class MyTuple {
	private List<Integer> elements;
	
    // Constructor to create a Tuple from an array of integers
    public MyTuple(Integer... values) {
        elements = new ArrayList<>();
        for (Integer value : values) {
            elements.add(value);
        }
    }

    // Get the size (number of elements) of the tuple
    public int size() {
        return elements.size();
    }

    // Get an element by its index
    public int get(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return elements.get(index);
    }

    // Setter to modify an element at a specific index
    public void set(int index, int value) {
        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        elements.set(index, value);
    }

    // Overriding toString() to print the tuple in a readable format
    @Override
    public String toString() {
        return elements.toString();
    }

    // Override equals() to compare tuples
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // they are the same object
        if (obj == null || getClass() != obj.getClass()) return false; // different class
        MyTuple tuple = (MyTuple) obj; // same class, cast obj and compare
        return elements.equals(tuple.elements);
    }
}
