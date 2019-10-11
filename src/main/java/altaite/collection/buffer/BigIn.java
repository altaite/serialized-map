package altaite.collection.buffer;

import altaite.kryo.KryoReaderPositional;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserializes a series of objects.
 */
public class BigIn<T> {

	private BigStore store;
	private KryoReaderPositional<T> dataReader;

	public BigIn(BigResource resource) {
		this.store = new BigStore(resource);
		dataReader = new KryoReaderPositional<>(store.getResources().getDataFile());
	}

	public T get(int i) {
		assert store.getPointer(0) == 0;
		return dataReader.get(store.getPointer(i));
	}

	public int size() {
		return store.size();
	}

	public boolean isEmpty() {
		return store.isEmpty();
	}

	public List<T> toList() {
		List<T> list = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			list.add(get(i));
		}
		return list;
	}

	public void close() {
		dataReader.close();
		store.close();
	}
}
