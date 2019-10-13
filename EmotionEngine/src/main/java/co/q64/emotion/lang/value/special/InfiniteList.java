package co.q64.emotion.lang.value.special;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.util.Rational;
import com.google.auto.factory.AutoFactory;

import co.q64.emotion.types.Comparison;
import co.q64.emotion.types.Operation;

public class InfiniteList implements Value {
    private final Function<Integer, Value> func;
    private final InfinitListImpl list;

    private InfiniteList(Function<Integer, Value> func) {
        this.func = func;
        this.list = new InfinitListImpl();
    }

    public static InfiniteList of(Function<Integer, Value> func) {
        return new InfiniteList(func);
    }

    @Override
    public List<Value> iterate() {
        return list;
    }

    @Override
    public String toString() {
        return "[" + IntStream.range(0, 100).mapToObj(i -> func.apply(i)).map(Value::toString).collect(Collectors.joining(",")) + "]";
    }

    private class InfinitListImpl implements List<Value> {
        private static final String READ_ONLY = "Inifinte lists are read only.";
        private static final String CONTAINS = "Inifinte list 'contains' is non-deterministic.";
        private static final String INDEX = "Inifinte list 'indexOf' is non-deterministic.";
        private static final String ARRAY = "Infinite list cannot be converted to an array.";

        @Override
        public Value get(int index) {
            return func.apply(index);
        }

        @Override
        public Iterator<Value> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<Value> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<Value> listIterator(int start) {
            return new InfiniteIterator(start);
        }

        @Override
        public List<Value> subList(int start, int end) {
            if (start < 0 || end < 0) {
                throw new NoSuchElementException();
            }
            List<Value> result = new ArrayList<>();
            for (int i = start; i < end; i++) {
                result.add(func.apply(i));
            }
            return result;
        }

        private class InfiniteIterator implements ListIterator<Value> {
            private int index = 0;

            public InfiniteIterator(int index) {
                this.index = index;
            }

            @Override
            public Value next() {
                if (index < 0) {
                    throw new NoSuchElementException();
                }
                return func.apply(index++);
            }

            @Override
            public int nextIndex() {
                return index;
            }

            @Override
            public Value previous() {
                if (index <= 0) {
                    throw new NoSuchElementException();
                }
                index--;
                return func.apply(index);
            }

            @Override
            public boolean hasPrevious() {
                return index > 0;
            }

            @Override
            public int previousIndex() {
                return index - 1;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(READ_ONLY);
            }

            @Override
            public void set(Value e) {
                throw new UnsupportedOperationException(READ_ONLY);
            }

            @Override
            public void add(Value e) {
                throw new UnsupportedOperationException(READ_ONLY);
            }
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean add(Value t) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public void add(int i, Value t) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public boolean addAll(Collection<? extends Value> c) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public boolean addAll(int i, Collection<? extends Value> c) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException(CONTAINS);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException(CONTAINS);
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException(INDEX);
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException(INDEX);
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public Value remove(int i) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException(CONTAINS);
        }

        @Override
        public Value set(int i, Value t) {
            throw new UnsupportedOperationException(READ_ONLY);
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException(ARRAY);
        }

        @Override
        public <T> T[] toArray(T[] t) {
            throw new UnsupportedOperationException(ARRAY);
        }
    }


    @Override
    public Value operate(Value value, Operation type) {
        return this;
    }

	@Override
    public int compareTo(Value value) {
        return -1;
    }

    @Override
    public Rational asNumber() {
        return Rational.of(0);
    }

    @Override
    public boolean asBoolean() {
        return false;
    }
}
