package altaite.list;

import altaite.kryo.KryoReader;
import java.io.File;
import java.util.NoSuchElementException;

public class BigIterator<T> implements java.util.Iterator<T> {

	private KryoReader<T> reader;

	public BigIterator(File file) {
		reader = new KryoReader<>(file);
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
}
