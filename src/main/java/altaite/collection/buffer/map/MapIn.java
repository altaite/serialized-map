package altaite.collection.buffer.map;

import altaite.collection.buffer.BigIn;
import altaite.collection.buffer.BigIterable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapIn<K, V> {

	private Map<K, Integer> keys = new HashMap<>();
	private BigIn<V> values;

	public MapIn(Path dir) {
		MapStore store = new MapStore(dir);
		BigIterable<K> keyIterable = new BigIterable<>(store.getKeyResource());
		int i = 0;
		for (K key : keyIterable) {
			keys.put(key, i++);
		}
		values = new BigIn<>(store.getValueResource());
	}

	public V get(K key) {
		int i = keys.get(key);
		V value = values.get(i);
		return value;
	}

	public Set<K> keySet() {
		return keys.keySet();
	}
	
	public int size() {
		return keys.size();
	}

	public boolean isEmpty() {
		return keys.isEmpty();
	}

	public void close() {
		values.close();
	}
}
