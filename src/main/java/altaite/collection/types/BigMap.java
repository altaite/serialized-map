package altaite.collection.types;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * The purpose of this class is to provide Map-like data structure in case values is too big to be held in memory.
 *
 * All values are serialized only. All keys are in memory. Additionally, the whole class can be serialized and
 * deserialized using its methods.
 *
 * Kryo is used for efficiency. Requires zero arguments constructors.
 *
 * Some Map methods throws UnsupportedOperationException, methods related to removal or value iteration.
 *
 * @author Antonin Pavelka
 * @param <K> Key type. Must implement Serializable.
 * @param <V> Value type. Must provide empty public constructor (for Kryo).
 */
public class BigMap<K extends Serializable, V extends Serializable> implements BigCollection<V> {

	private List<K> indexToKey;
	private Map<K, Integer> keyToIndex;
	private transient Path path;
	private BigList<V> values;

	
	/*
	plan
	separate list for keys and values
	upon initialization, load all keys
	first check if lists are same lenght, truncate
	*/
	
	/**
	 * @param dir Directory where all data are be stored.
	 */
	public BigMap(Path dir) {
		initializePaths(dir);
		this.indexToKey = new ArrayList<>();
		this.keyToIndex = new HashMap<>();
	}

	public void initializePaths(Path path) {
		this.path = path;
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		values = BigList.<V>open(path.resolve("values"));
	}

	public void put(K key, V value) {
		keyToIndex.put(key, indexToKey.size());
		indexToKey.add(key);
		values.add(value);
	}

	public int keyToIndex(K key) {
		return keyToIndex.get(key);
	}

	public K indexToKey(int index) {
		return indexToKey.get(index);
	}

	public V getByKey(K key) {
		int i = keyToIndex(key);
		return getByIndex(i);
	}

	public V getByIndex(int index) {
		return values.get(index);
	}

	public Set<K> keys() {
		return keyToIndex.keySet();
	}

	public int size() {
		return indexToKey.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean containsKey(Object o) {
		return keyToIndex.containsKey(o);
	}

	@Override
	public Path getPath() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void closeWriter() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void closeReader() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Iterator<V> iterator() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
