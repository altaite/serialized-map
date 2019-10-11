package altaite.collection.util;

import altaite.kryo.KryoFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * The purpose of this class is to provide Map-like data structure in case total size of all entries is too big to be
 * held in memory.
 *
 * All values are serialized only. All keys are in memory. Additionally, the whole class can be serialized and
 * deserialized using its methods.
 *
 * Auxiliary small data are serialized by Java, values by Kryo for efficiency.
 *
 * @author Antonin Pavelka
 * @param <K> Key type. Must implement Serializable.
 * @param <V> Value type. Must provide empty public constructor (for Kryo).
 */
// probably deprecated
@Deprecated
public class SerializedMapFiles<K extends Serializable, V extends Serializable>
	implements Serializable, Iterable<V> {

	private Class<V> valueClass;
	private Map<Integer, K> indexToKey;
	private Map<K, Integer> keyToIndex;
	private RadixTree radixTree;
	private transient File dir;
	private transient Kryo kryo;

	public SerializedMapFiles() {
		// for kryo
	}

	/**
	 * @param dir Directory where all data are be stored.
	 * @param itemsPerDir Maximum number of object files in directory. Total number of files and directories within a
	 * single directory can be up to 2 * itemsPerDir + 1
	 * @param valueClass Class of the value V type (e.g., String.class).
	 */
	public SerializedMapFiles(File dir, int itemsPerDir, Class<V> valueClass) {
		this.dir = dir;
		if (dir.exists()) {
			dir.mkdir();
		}
		this.indexToKey = new HashMap<>();
		this.keyToIndex = new HashMap<>();
		this.radixTree = new RadixTree(itemsPerDir);
		this.kryo = KryoFactory.createKryo();
		this.valueClass = valueClass;
	}

	public static <K extends Serializable, V extends Serializable> SerializedMapFiles<K, V> load(File dir) {
		SerializedMapFiles<K, V> map;
		File file = getFile(dir);
		SerializedMapFiles<K, V> dummy = new SerializedMapFiles<>();
		try (Input input = new Input(new FileInputStream(file))) {
			map = new Kryo().readObject(input, dummy.getClass());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		/*try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			map = (SerializedMap<K, V>) in.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}*/
		map.dir = dir;
		map.kryo = KryoFactory.createKryo();
		return map;
	}

	public void save() {
		File file = getFile(dir);
		try (Output output = new Output(new FileOutputStream(file))) {
			kryo.writeObject(output, this);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		/*try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
		
			out.writeObject(this);
			out.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}*/
	}

	public void put(K key, V value) {
		TreePath treePath = radixTree.addPath();
		int index = treePath.getLeafId();
		indexToKey.put(index, key);
		keyToIndex.put(key, index);
		serializeValue(treePath.getPath(dir.toPath()), value);
	}

	public int keyToIndex(K key) {
		return keyToIndex.get(key);
	}

	public K indexToKey(int index) {
		return indexToKey.get(index);
	}

	public V getByKey(K key) {
		int index = keyToIndex(key);
		TreePath treePath = radixTree.getPath(index);
		Path path = treePath.getPath(dir.toPath());
		return deserializeValue(path);
	}

	public V getByIndex(int index) {
		return getByKey(indexToKey(index));
	}

	private static File getFile(File dir) {
		File file = dir.toPath().resolve("map.obj").toFile();
		return file;
	}

	private void serializeValue(Path path, V value) {
		createDirs(path.getParent());
		try (Output output = new Output(new FileOutputStream(path.toFile()))) {
			kryo.writeObject(output, value);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private V deserializeValue(Path path) {
		try (Input input = new Input(new FileInputStream(path.toFile()))) {
			return kryo.readObject(input, valueClass);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void createDirs(Path path) {
		try {
			Files.createDirectories(path);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Set<K> keys() {
		return keyToIndex.keySet();
	}

	@Override
	public Iterator<V> iterator() {
		return new SerializedMapFilesValuesIterator(this);
	}

	public int size() {
		return indexToKey.size();
	}

}

class SerializedMapFilesValuesIterator<K extends Serializable, V extends Serializable> implements Iterator<V> {

	private SerializedMapFiles<K, V> map;
	private Iterator<K> iterator;

	public SerializedMapFilesValuesIterator(SerializedMapFiles<K, V> map) {
		this.map = map;
		iterator = map.keys().iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public V next() {
		K key = iterator.next();
		return map.getByKey(key);
	}

}
