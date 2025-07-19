import randoop.CheckRep;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

// List class, generic type T needs to implement Hashable interface
public class List<T extends Comparable<T>> implements Iterable<T> {
    protected Object[] elements;
    protected final boolean fixed;
    protected int size;
    protected final int initialCapacity;

    // Create a new List
    public static <T extends Comparable<T>> List<T> newList(int initialSize, boolean fixed) {
        if (initialSize < 0) throw new IllegalArgumentException("invalid size");
        return new List<>(initialSize, fixed);
    }

    // Create List from array
    @SuppressWarnings("unused")
    public static <T extends Comparable<T>> List<T> fromSlice(T[] array) {
        List<T> list = new List<>(array.length, false);
        System.arraycopy(array, 0, list.elements, 0, array.length);
        list.size = array.length;
        return list;
    }

    protected List(int initialSize, boolean fixed) {
        if (initialSize < 0) throw new IllegalArgumentException("invalid size");
        this.elements = new Object[initialSize];
        this.fixed = fixed;
        this.size = 0;
        this.initialCapacity = initialSize;
    }

    // Create a copy of the List
    public List<T> copy() {
        List<T> newList = new List<>(this.elements.length, this.fixed);
        System.arraycopy(this.elements, 0, newList.elements, 0, this.size);
        newList.size = this.size;
        return newList;
    }

    // Clear the List
    public void clear() {
        this.size = 0;
        if (!fixed) {
            Arrays.fill(elements, null);
        }
    }

    // Get the size of the List
    public int size() {
        return this.size;
    }

    // Get the capacity of the List
    public int getCapacity() {
        return elements.length;
    }

    // Check if List is fixed size
    public boolean isFixed() {
        return fixed;
    }

    // Check if List is full
    public boolean isFull() {
        return fixed && size == elements.length;
    }

    // Check if List is empty
    @SuppressWarnings("unused")
    public boolean isEmpty() {
        return size == 0;
    }

    // Check if List contains specified element
    @SuppressWarnings("unused")
    public boolean has(T item) {
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T element = (T) elements[i];
            if (element.equals(item)) {
                return true;
            }
        }
        return false;
    }

    // Override equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof List)) return false;
        List<?> other = (List<?>) obj;
        if (size != other.size) return false;

        Iterator<T> thisIter = iterator();
        Iterator<?> otherIter = other.iterator();
        while (thisIter.hasNext() && otherIter.hasNext()) {
            if (!thisIter.next().equals(otherIter.next())) {
                return false;
            }
        }
        return true;
    }

    // Check if current List is less than another List
    @SuppressWarnings("unused")
    public boolean less(List<T> other) {
        if (size < other.size) return true;
        if (size > other.size) return false;

        Iterator<T> thisIter = iterator();
        Iterator<T> otherIter = other.iterator();
        while (thisIter.hasNext() && otherIter.hasNext()) {
            T a = thisIter.next();
            T b = otherIter.next();
            if (a.compareTo(b) < 0) return true;
            if (!a.equals(b)) return false;
        }
        return false;
    }

    // Override hashCode method
    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            result = 31 * result + ((T) elements[i]).hashCode();
        }
        return result;
    }

    // Implement Iterator interface
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) elements[index++];
            }
        };
    }

    // Iterate from back to front
    @SuppressWarnings("unused")
    public Iterator<T> reverseIterator() {
        return new Iterator<T>() {
            private int index = size - 1;

            @Override
            public boolean hasNext() {
                return index >= 0;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) elements[index--];
            }
        };
    }

    // Get element at specified index
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) elements[index];
    }

    // Set element at specified index
    @SuppressWarnings("unused")
    public void set(int index, T item) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        elements[index] = item;
    }

    // Push element to List
    @SuppressWarnings("unused")
    public void push(T item) {
        append(item);
    }

    // Append element to end of List
    public void append(T item) {
        insert(size, item);
    }

    // Insert element at specified position
    public void insert(int index, T item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (isFull()) {
            throw new IllegalStateException("Fixed size list is full");
        }
        // added precondition
        if (item == null) throw new IllegalArgumentException("invalid element");
        if (size == elements.length) {
            expand();
        }
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = item;
        size++;
    }

    // Append all elements from iterator to end of List
    @SuppressWarnings("unused")
    public void extend(Iterator<T> iterator) {
        while (iterator.hasNext()) {
            append(iterator.next());
        }
    }

    // Pop element from end of List
    public T pop() {
        if (size == 0) throw new NoSuchElementException("List is empty");
        T item = get(size - 1);
        remove(size - 1);
        return item;
    }

    // Remove element at specified index
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
        if (!fixed) shrink();
    }

    // Expand List capacity
    private void expand() {
        int newCapacity = elements.length < 100 && elements.length > 0 ?
                elements.length * 2 : elements.length + 100;
        elements = Arrays.copyOf(elements, newCapacity);
    }

    // Shrink List capacity
    private void shrink() {
        if (elements.length / 2 < 10 || size * 2 > elements.length) return;
        int newCapacity = Math.max(elements.length / 2 + 1, 10);
        elements = Arrays.copyOf(elements, newCapacity);
    }

    // Override toString method
    @Override
    public String toString() {
        if (size == 0) return "{}";
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append("}").toString();
    }

    // Verify List invariants
    @CheckRep
    public boolean repOK() {
        if (elements == null) return false;

        if (size < 0 || size > elements.length) return false;

        for (int i = 0; i < size; i++) {
            if (elements[i] == null || !(elements[i] instanceof Comparable)) return false;
        }

        if (fixed && (elements.length != initialCapacity || size > initialCapacity)) return false;

        if (!fixed) {
            if (size >= 10 && elements.length > 10 && size * 2 < elements.length) return false;
        }

        return true;
    }
}
