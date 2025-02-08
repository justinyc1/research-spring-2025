package javacode;

import java.util.List;
import java.util.ArrayList;

/** Tuple: A collection of elements that is: finite, ordered, immutable, fixed-length, allows repetition, and of type integer
 */
public class Tuple<E> {
    final E[] elements; // final: can't be change once assigned an elements 

    /** Create a Tuple from the contents of a List
     * 
     * @param values
     */
    @SuppressWarnings("unchecked")
    public Tuple(List<E> values) {
        int capacity = values.size();
        elements = (E[]) new Object[capacity];
        for (int i = 0; i < capacity; ++i) {
            elements[i] = values.get(i);
        }
    }

    /** Get the capacity/length of the tuple
     * 
     * @return the capacity/length of the tuple
     */
    public int size() {
        return elements.length;
    }

    /** Get an element by its index
     * 
     * @param index
     * @return
     */
    public E get(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return elements[index];
    }

    /** Print the tuple surrounded by parentheses
     * 
     */
    @Override
    public String toString() {
        int lastElementIndex = elements.length-1;
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < lastElementIndex; ++i) {
            sb.append(elements[i]).append(", ");
        }
        sb.append(elements[lastElementIndex]).append(")");
        return sb.toString();
    }

    /** Converts the tuple into a List
     * 
     * @return a List with the same content as the tuple
     */
    public List<E> toList() {
        List<E> newList = new ArrayList<>();
        for (int i = 0; i < elements.length; ++i) {
            newList.add(elements[i]);
        }
        return newList;
    }

    /** Compares a tuple to another object and returns true if both tuples are equal, equal as in for tuples a and b, a_i = b_i for all i
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // they are the same object
        if (obj == null || getClass() != obj.getClass()) return false; // different class
        Tuple<E> other = (Tuple<E>) obj; // same class, cast obj and compare
        for (int i = 0; i < elements.length; ++i) {
            if (!elements[i].equals(other.elements[i])) {
                return false;
            }
        }
        return true;
    }
}
