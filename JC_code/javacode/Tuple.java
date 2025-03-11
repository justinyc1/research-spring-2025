package JC_code.javacode;

import java.util.List;
import java.util.ArrayList;

/** Tuple: A collection of elements that is: finite, ordered, immutable, fixed-length, allows repetition, and of type integer
 */
public class Tuple implements Comparable<Tuple> {
    private final int[] elements; // final: can't be change once assigned an to an array 
    public static Tuple EMPTY_TUPLE = new Tuple(0);

    private Tuple(int capacity) {
        elements = new int[capacity];
    }

    /** Create a Tuple from the contents of a List
     * 
     * @param list
     */
    public Tuple(List<Integer> list) {
        int capacity = list.size();
        elements = new int[capacity];
        for (int i = 0; i < capacity; ++i) {
            elements[i] = list.get(i);
        }
    }

    /** Create a Tuple from the contents of an array
     * 
     * @param arr
     */
    public Tuple(int[] arr) {
        int capacity = arr.length;
        elements = new int[capacity];
        for (int i = 0; i < capacity; ++i) {
            elements[i] = arr[i];
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
    public int get(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return elements[index];
    }

    public int sum() {
        int sum = 0;
        for (int i = 0; i < elements.length; ++i) {
            sum += elements[i]; 
        }
        return sum;
    }

    /** Return a subtuple starting at index start (inclusive) until index end (exclusive)
     * 
     * @param start - inclusive
     * @param end - exclusive (one less than size)
     * @return
     */
    public Tuple getSubTuple(int start, int end) throws ProjectException {
        if (start >= end) throw new ProjectException("start = " + start + " and end = " + end + " index are invalid.");
        Tuple subTuple = new Tuple(end - start);
        for (int i = 0; i < subTuple.size(); ++i) {
            subTuple.elements[i] = this.elements[start+i];
        }
        return subTuple;
    }
    
    /** Return a subtuple starting at index start (inclusive) until the end of the tuple
     * 
     * @param start
     * @return
     * @throws ProjectException
     */
    public Tuple getSubTuple(int start) throws ProjectException {
        return getSubTuple(start, this.size());
    }

    public Tuple getNextAscendingTupleAfter(Tuple subTuple, int min, int max) throws ProjectException {
        List<Integer> nextAsList = subTuple.toList();
        // System.out.print("for tuple " + nextAsList + ":"); // DEBUG
        for (int i = nextAsList.size() - 1; i >= 0; --i) {
            // System.out.print("  for i = " + i + ", at(i) = " + nextAsList.get(i)); // DEBUG
            if (i == nextAsList.size() - 1) { // last element
                if (nextAsList.get(i) < max) {
                    nextAsList.set(i, nextAsList.get(i) + 1);
                    return new Tuple(nextAsList);
                } else { // last element == max 
                    continue;
                }
            } else { // not last element
                if (nextAsList.get(i) + 1 < nextAsList.get(i+1)) {
                    nextAsList.set(i, nextAsList.get(i) + 1);
                    // every element after what was just changed is reduced to minimum possible combination
                    for (int j = i + 1; j < nextAsList.size(); ++j) { 
                        nextAsList.set(j, nextAsList.get(j-1) + 1);
                    }
                    return new Tuple(nextAsList);
                } else {
                    continue;
                }
            }

        }
        return null;
    }

    public Tuple getNextAscendingTupleAfter(Tuple subTuple) throws ProjectException {
        return getNextAscendingTupleAfter(subTuple, this.getMinInt(), this.getMaxInt());
    }

    public Tuple getNextTupleAfter(Tuple subTuple, int min, int max) throws ProjectException {
        List<Integer> nextAsList = subTuple.toList();
        for (int i = nextAsList.size() - 1; i >= 0; --i) {
            if (nextAsList.get(i) < max) {
                nextAsList.set(i, nextAsList.get(i) + 1);
                return new Tuple(nextAsList);
            } else {
                nextAsList.set(i, min);
            }
        }
        return null;
    }

    public Tuple getNextTupleAfter(Tuple subTuple) throws ProjectException {
        return getNextTupleAfter(subTuple, this.getMinInt(), this.getMaxInt());
    }

    public int getMinInt() {
        return this.get(minIntIndex());
    }

    public int getMaxInt() {
        return this.get(maxIntIndex());
    }

    public int minIntIndex() {
        int minIndex = 0;
        for (int i = 0; i < this.size(); ++i) {
            if (this.get(minIndex) > this.get(i)) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    public int maxIntIndex() {
        int maxIndex = 0;
        for (int i = 0; i < this.size(); ++i) {
            if (this.get(maxIndex) < this.get(i)) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public int containsInt(int key) {
        for (int i = 0; i < elements.length; ++i) {
            if (elements[i] == key) {
                return i;
            }
        }
        return -1;
    }

    public int contains(int key) {
        for (int i = 0; i < elements.length; ++i) {
            if (elements[i] == key) {
                return i;
            }
        }
        return -1;
    }

    /** Print the tuple surrounded by parentheses
     * 
     */
    @Override
    public String toString() {
        return toString(", ");
    }

    public String toString(String delimiter) {
        int lastElementIndex = elements.length-1;
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < lastElementIndex; ++i) {
            sb.append(elements[i]).append(delimiter);
        }
        sb.append(elements[lastElementIndex]).append(")");
        return sb.toString();
    }


    /** Converts the tuple into a List
     * 
     * @return a List with the same content as the tuple
     */
    public List<Integer> toList() {
        List<Integer> newList = new ArrayList<>();
        for (int i = 0; i < elements.length; ++i) {
            newList.add(elements[i]);
        }
        return newList;
    }

    /** Compares a tuple to another object and returns true if both tuples are equal, equal as in for tuples a and b, a_i = b_i for all i
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // they are the same object
        if (obj == null || getClass() != obj.getClass()) return false; // different class
        Tuple other = (Tuple) obj; // same class, cast obj and compare
        for (int i = 0; i < elements.length; ++i) {
            if (elements[i] != (other.elements[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(Tuple other) { // other is a Tuple<E>
        if (Integer.compare(this.size(), ((Tuple)other).size()) != 0) { // if not same size, smaller come first
            return this.size() - ((Tuple)other).size();
        }
        for (int i = 0; i < Integer.min(this.size(), ((Tuple)other).size()); ++i) { // else compare every element
            if (Integer.compare(this.get(i), ((Tuple)other).get(i)) != 0) {
                return this.get(i) - ((Tuple)other).get(i);
            }
        }
        return 0;
    }
}