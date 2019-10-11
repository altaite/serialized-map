package altaite.collection.buffer.map.oflists;

import altaite.collection.buffer.BigIn;
import altaite.collection.util.RadixTree;
import java.nio.file.Path;
import java.util.Map;

public class MapOfBigIn<K, V> {

	private BigIn<K> keys;
	private Map<K, BigIn<V>> ins;
	private MapOfBigStore store;
	private RadixTree tree;

	public MapOfBigIn(Path dir) {
		
		// read the keys, use the int of their order to figure out radix paths
		store = new MapOfBigStore(dir);
		tree = store.getRadixTree();
	}

	public BigIn<V> get(K key) {
		return ins.get(key);
	}
}
