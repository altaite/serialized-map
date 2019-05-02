package altaite.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Serializes one or more objects of the same type. Class is written at the start of the file.
 *
 * @param <T> Class of objects to be serialized.
 */
public class KryoWriter<T> {

	private Kryo kryo;
	private Output output;
	private boolean isClosed;

	public KryoWriter(File file) {
		this(file, false);
	}

	public KryoWriter(File file, boolean append) {
		kryo = KryoFactory.createKryo();
		try {
			output = new Output(new FileOutputStream(file, append));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void flush() {
		output.flush();
	}

	/**
	 * Returns file size after write.
	 */
	public long write(T t) {
		kryo.writeClass(output, t.getClass());
		kryo.writeObject(output, t);
		long position = output.total();
		return position;
	}

	public void close() {
		isClosed = true;
		output.close();
	}

	public boolean isClosed() {
		return isClosed;
	}
}
