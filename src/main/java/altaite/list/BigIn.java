package altaite.list;

import altaite.kryo.KryoReaderPositional;
import java.util.ArrayList;
import java.util.List;

public class BigIn<T> {

	private BigStore store;
	private KryoReaderPositional<T> dataReader;

	public BigIn(BigStore store) {
		this.store = store;
		dataReader = new KryoReaderPositional<>(store.getResources().getDataFile());
	}

	public T get(int i) {
		assert store.getPointer(0) == 0;
		return dataReader.get(store.getPointer(i));
	}

	public int size() {
		return store.size();
	}

	public List<T> toList() {
		List<T> list = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			list.add(get(i));
		}
		return list;
	}
}
