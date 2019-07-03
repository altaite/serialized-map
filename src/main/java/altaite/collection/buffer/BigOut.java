package altaite.collection.buffer;

import altaite.kryo.KryoWriter;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BigOut<T> {

	private BigStore store;
	private KryoWriter<T> dataWriter;
	private DataOutputStream pointerWriter;

	public BigOut(BigResource resource) {
		this.store = new BigStore(resource); // fix corruption,  TODO static better name
		openForWriting();
	}

	/**
	 * Appends object to the end.
	 *
	 * @param t To be added at the end.
	 */
	public void add(T t) {
		long written = dataWriter.write(t);
		//dataWriter.flush(); // TEMPTORARY FOR ASSERT
		long position = store.getLastPointer() + written;
		addPosition(position);
		//assert position == store.getResources().getDataFile().length() :
		//	position + " <> " + store.getResources().getDataFile().length();
	}

	public void addAll(Iterable<T> ts) {
		for (T t : ts) {
			add(t);
		}
	}

	private void openForWriting() {
		dataWriter = new KryoWriter(store.getResources().getDataFile(), true);
		try {
			File pointerFile = store.getResources().getPointerFile();
			pointerWriter = new DataOutputStream(
				new BufferedOutputStream(
					new FileOutputStream(pointerFile, true)));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void flush() { // probably not needed, close should do it anyway
		dataWriter.flush();
		try {
			pointerWriter.flush();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void addPosition(long position) {
		store.addPointer(position);
		try {
			store.setFlagOn(); // if flag is missing, last long will be considered corrupted
			pointerWriter.writeLong(position);
			store.setFlagOff();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void close() {
		flush();
		try {
			pointerWriter.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		dataWriter.close();
		store.close();
	}
}
