package altaite.collection.buffer.map.oflists;

import altaite.collection.buffer.BigOut;
import altaite.collection.buffer.BigResource;
import altaite.collection.util.RadixTree;
import altaite.collection.util.TreePath;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

//
/**
 * Possibly too slow to be of value, if performance is necessary, use primitives instead of Kryo serialization.
 *
 * TODO check RadixTree, duplication
 *
 * A serializer of the maping of keys (K) to series of objects (V). Something like simpler and more convenient Map<K,
 * List<V>>, but series of V are not held in memory.
 */
public class MapOfBigOut<K, V> {

	private BigOut<K> keys;
	private Map<K, BigOut<V>> outs;
	private MapOfBigStore store;

	private RadixTree tree;

	public MapOfBigOut(Path path) {
		outs = new HashMap<>();
		store = new MapOfBigStore(path);
		tree = store.getRadixTree();
	}

	public void add(K key, V value) {
		BigOut<V> big = outs.get(key);
		if (big == null) {
			TreePath tp = tree.addPath();
			BigResource resource = new BigResource(store.getSubdir(tp));
			big = new BigOut<V>(resource);
			keys.add(key);
		}
		big.add(value);
	}

	public void close() {
		for (BigOut<V> out : outs.values()) {
			out.close();
		}
	}

}
