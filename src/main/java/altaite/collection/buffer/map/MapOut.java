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

	public void close() {
		keys.close();
		values.close();
	}
}
