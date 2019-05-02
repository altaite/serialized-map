package altaite.collection.types;

import altaite.kryo.KryoWriter;
import altaite.collection.util.RadixTree;
import altaite.collection.util.TreePath;
import altaite.kryo.KryoReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ListOfLists<T> {

	private InMemory buffer = new InMemory(10000);
	private transient Path home;

	public ListOfLists(Path home) {
		if (!Files.exists(home)) {
			throw new RuntimeException(home + " does not exist.");
		}
		this.home = home;
	}

	public void add(int index, T t) {
		buffer.add(index, t);
		if (buffer.full()) {
			free();
		}
	}

	private void free() {
		//List<Bucket> buckets = buffer.partialClear();
		//for (Bucket bucket : buckets) {
		for (Bucket bucket : buffer.getBuckets()) {
			write(bucket);
		}
	}

	private void write(Bucket bucket) {
		KryoWriter writer = new KryoWriter(getFile(bucket.getId()));
		for (T t : bucket) {
			writer.write(t);
		}
		writer.close();
		bucket.clear();
	}

	public KryoReader getReader(int index) {
		File file = getFile(index);
		return new KryoReader(file);
	}

	private File getFile(int id) {
		TreePath treePath = RadixTree.generatePath(id, 1000);
		Path path = treePath.getPath(home);
		/*if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}*/
		return path.toFile();
	}

	class InMemory {

		private HashMap<Integer, Bucket> map = new HashMap<>();
		private int items;
		private int maxItems;
		private int minItems;

		public InMemory(int maxItems) {
			this.maxItems = maxItems;
			this.minItems = maxItems / 2;
		}

		public int size() {
			return items;
		}

		public boolean full() {
			return items >= maxItems;
		}

		public void add(int index, T t) {
			Bucket bucket = map.get(index);
			if (bucket == null) {
				bucket = new Bucket(index);
				map.put(index, bucket);
			}
			bucket.add(t);
			items++;
		}

		public Collection<Bucket> getBuckets() {
			return map.values();
		}

		/**
		 * Remove from larges until enough memory is saved. Work in progress. Questionable if needed.
		 */
		public List<Bucket> partialClear() {
			List<Bucket> removed = new ArrayList<>();

			List<Bucket> all = new ArrayList<>();
			all.addAll(map.values());
			Collections.sort(all);

			for (Bucket bucket : all) {
				if (size() <= minItems) {
					break;
				}
				removed.add(bucket);
				remove(bucket);
			}

			return removed;
		}

		private void remove(Bucket bucket) {
			map.remove(bucket.getId());
			items -= bucket.size();
		}

	}

	class Bucket implements Comparable<Bucket>, Iterable<T> {

		private int id;
		private List<T> list = new ArrayList<>();

		public Bucket(int id) {
			this.id = id;
		}

		public void add(T t) {
			list.add(t);
		}

		public int size() {
			return list.size();
		}

		public int getId() {
			return id;
		}

		@Override
		public int compareTo(Bucket other) {
			return Integer.compare(other.size(), size());
		}

		@Override
		public Iterator<T> iterator() {
			return list.iterator();
		}

		public void clear() {
			list.clear();
		}

	}
}
