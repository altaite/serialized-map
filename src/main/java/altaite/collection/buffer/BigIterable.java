package altaite.collection.buffer;

import altaite.kryo.KryoReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Closes itself after hasNext() returns false, or next() results in NoSuchElementException().
 *
 * @param <T>
 */
public class BigIterable<T> implements Iterable<T> {

	private BigResource resource;

	public BigIterable(BigResource resource) {
		this.resource = resource;

	}

	@Override
	public Iterator<T> iterator() {
		return new BigIterator<T>(resource);
	}
}

class BigIterator<T> implements java.util.Iterator<T> {

	private KryoReader<T> reader;

	public BigIterator(BigResource resource) {
		reader = new KryoReader<>(resource.getDataFile());
	}

	@Override
	public boolean hasNext() {
		boolean hasNext = reader.hasNext();
		if (!hasNext) {
			reader.close();
		}
		return hasNext;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return reader.read();
	}

	public void close() {
		reader.close();
	}
}
