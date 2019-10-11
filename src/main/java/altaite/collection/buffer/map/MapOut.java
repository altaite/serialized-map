package altaite.collection.buffer.map;

import altaite.collection.buffer.BigOut;
import java.nio.file.Path;

public class MapOut<K, V> {

	private BigOut<K> keys;
	private BigOut<V> values;

	public MapOut(Path dir) {
		MapStore store = new MapStore(dir);
		store.clear();
		keys = new BigOut<>(store.getKeyResource());
		values = new BigOut<>(store.getValueResource());
	}

	public MapOut(Path dir, boolean append) {
		MapStore store = new MapStore(dir);
		if (!append) {
			store.clear();
		}
		keys = new BigOut<>(store.getKeyResource());
		values = new BigOut<>(store.getValueResource());
	}

	public void put(K key, V value) {
		keys.add(key);
		values.add(value);
	}

	/**
	 * seems to be necessary, i.e. does the original idea of fixing corruption any value at all? closing and opening all the time? flush?
	 */
	public void close() {
		keys.close();
		values.close();
	}

	protected void TEST_ONLY_ADD_KEY(K key) {
		keys.add(key);
	}

	protected void TEST_ONLY_ADD_VALUE(V value) {
		values.add(value);
	}
}
