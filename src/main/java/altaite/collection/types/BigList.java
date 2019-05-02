package altaite.collection.types;

import altaite.BigCollectionResource;
import altaite.io.FileOperation;
import altaite.kryo.KryoReader;
import altaite.kryo.KryoReaderPositional;
import altaite.kryo.KryoWriter;
import altaite.util.Flag;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// TODO is it not closing somewhre?
// TODO delete files in clearFiles one by one
// TEST by damaging files randomly
// TODO autoclosable version, close writer after each
public class BigList<T extends Serializable> implements BigCollection<T>, Serializable, Collection<T> {
// TODO simple transaction, store both posititions and entries separatelly

	private transient Path path;
	private transient File dataFile;
	private transient File pointerFile;
	private transient Flag pointerWritingFlag;
	private KryoWriter<T> dataWriter;
	private DataOutputStream pointerWriter;
	private KryoReaderPositional<T> dataReader;
	private List<Long> pointers; // including the one yet unused, the total file size (always including 0L as first, but this is not serialized)

	public BigList(Path dir) {
		initializePaths(dir);
		repairFiles();
		initializePointers();
		assert pointers.get(0) == 0;
	}

	private void initializePointers() {
		createPositionsInMemory();
		if (pointerFile.exists() && pointerFile.length() > 0) {
			loadPointers();
			//printPositions();
		}
	}

	private void createPositionsInMemory() {
		pointers = new ArrayList<>();
		pointers.add(0L);
	}

	private void loadPointers() {
		try (DataInputStream dis = new DataInputStream(new FileInputStream(pointerFile))) {
			while (dis.available() > 0) {
				long l = dis.readLong();
				pointers.add(l);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void initializePaths(Path path) {
		this.path = path;
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		this.dataFile = path.resolve("big_list.kryo").toFile();
		this.pointerFile = path.resolve("big_list.pointers").toFile();
		this.pointerWritingFlag = new Flag(path.resolve("big_list.flag").toFile());
	}

	private void repairFiles() {
		if (pointersCorrupt()) {
			trimPointers();
			initializePointers();
		}
		if (dataCorrupt()) {
			fixData();
		}
		// positions including current file size
		// flag for positions
		// adjust positions first	
		// then cut data using last length
	}

	private boolean pointersCorrupt() {
		long length = pointerFile.length();
		boolean wrongLength = length % 8 == 0;
		/* 8 - file of long. Should happen only in case of file damage,
		   unlike flag, which can be off simply because application was killed.*/
		return wrongLength || pointerWritingFlag.isOn();
	}

	private boolean dataCorrupt() {
		long dataFileLength = dataFile.length();
		if (dataFileLength == 0) {
			return false;
		} else if (pointers.size() == 0) {
			return true;
		}
		long shouldBe = pointers.get(pointers.size() - 1);
		if (dataFileLength < shouldBe) {
			throw new RuntimeException();
		}
		return dataFileLength != shouldBe;
	}

	private void trimPointers() {
		long length = pointerFile.length();
		long newLength = length / 8 * 8;
		FileOperation.truncate(pointerFile, newLength);
	}

	private void fixData() {
		if (pointers.isEmpty()) {
			deleteFiles();
		} else {
			long length = pointers.get(pointers.size() - 1);
			long current = dataFile.length();
			if (length < current) {
				throw new RuntimeException();
			}
			FileOperation.truncate(dataFile, length);
		}
	}

	/// TODO delte all the files
	private void deleteFiles() {
		String msg = " not deleted.";
		closeReader();
		closeWriter();
		try {
			FileOperation.checkedDelete(dataFile);
			FileOperation.checkedDelete(pointerFile);
			pointerWritingFlag.deleteFile();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		//FileOperation.truncate(dataFile, 0);
		//FileOperation.truncate(pointerFile, 0);

	}

	public void flush() {
		dataWriter.flush();
		try {
			pointerWriter.flush();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Opens writer that should be closed later by closeWriter().
	 */
	@Override
	public boolean add(T t) {
		assert pointers.get(0) == 0;
		lazyOpenForWriting();
		long position = dataWriter.write(t);
		addPosition(position);
		assert pointers.get(0) == 0;
		return true;
	}

	private void addPosition(long position) {
		pointers.add(position);
		try {
			pointerWritingFlag.on();// if flag is missing, last long will be considered corrupted
			pointerWriter.writeLong(position);
			pointerWritingFlag.off();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Opens reader that should be closed later by closeReader().
	 *
	 * @param i
	 * @return
	 */
	public T get(int i) {
		assert pointers.get(0) == 0;
		if (dataReader == null) {
			dataReader = new KryoReaderPositional<>(dataFile);
		}
		printPositions();
		return dataReader.get(pointers.get(i));
	}

	public List<T> toList() {
		List<T> list = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			list.add(get(i));
		}
		return list;
	}

	private void printPositions() {
		System.out.println("Data file length: " + dataFile.length());
		System.out.println("Pointer file length: " + pointerFile.length());
		System.out.println("Pointers: " + pointers.size());
		for (long l : pointers) {
			System.out.print(l + " ");
		}
		System.out.println();
	}
	
	private void lazyOpenForWriting() {
		if (dataReader != null) {
			throw new RuntimeException("Cannot add after reading was initiated.");
		}
		if (dataWriter == null) {
			dataWriter = new KryoWriter(dataFile);
		}
		if (pointerWriter == null) {
			try {
				System.out.println("opening pointer writer");
				pointerWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(pointerFile)));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}


	public void close() {
		closeWriter();
		closeReader();
	}

	@Override
	public void closeWriter() {
		try {
			if (pointerWriter != null) {
				System.out.println("closing pointer writer");
				pointerWriter.close();
			}
			if (pointerWritingFlag != null) {
				pointerWritingFlag.close();
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (dataWriter != null) {
			dataWriter.close();
		}
	}

	@Override
	public int size() {
		return pointers.size() - 1;
	}

	@Override
	public boolean isEmpty() {
		return pointers.size() == 0;
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		for (T t : collection) {
			add(t);
		}
		return true;
	}

	@Override
	public Iterator<T> iterator() {
		return new InstanceIterator<>(dataFile);
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public void closeReader() {
		if (dataReader != null) {
			dataReader.close();
		}
	}

	private long[] toArray(List<Long> list) {
		long[] array = new long[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		System.out.println("bbbb");
		for (T t : this) {
			System.out.println("aaa");
			sb.append(t.toString());
			sb.append(", ");
		}
		System.out.println("sss " + size());
		sb.delete(sb.length() - 3, sb.length());
		return sb.toString();
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> clctn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> clctn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		close();
		deleteFiles();
	}

	protected File getDataFile() {
		return dataFile;
	}

	protected File getPointerFile() {
		return pointerFile;
	}

}

class InstanceIterator<T> implements Iterator<T> {

	private KryoReader<T> reader;

	 public InstanceIterator(File file) {
		reader = new KryoReader<>(file);
	}

	@Override
	public boolean hasNext() {
		boolean hasNext = reader.hasNext();
		if (!hasNext) {
			reader.close();
		}
		return hasNext;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return reader.read();
	}

}
