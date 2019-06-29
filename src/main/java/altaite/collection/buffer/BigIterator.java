package altaite.collection.buffer;

import altaite.kryo.KryoReader;
import java.util.NoSuchElementException;

/**
 * Closes itself after hasNext() returns false, or next() results in NoSuchElementException().
 */
public class BigIterator<T> implements java.util.Iterator<T> {

	private KryoReader<T> reader;
	private BigResource resource;

	public BigIterator(BigResource resource) {
		this.resource = resource;
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
