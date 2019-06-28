package altaite.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

/**
 * Allows access to any object stored in a single file. For iterating through the whole file, use KryoReader instead.
 *
 * @param <T> type of objects to be returned.
 */
public class KryoReaderPositional<T> {

	private Kryo kryo;
	private RandomAccessFile raf;

	private File file;

	public KryoReaderPositional(File file) {
		if (!file.exists()) {
			throw new RuntimeException("Cannot read from nonexistent file " + file);
		}
		this.file = file;
		this.kryo = KryoFactory.createKryo();

	}

	public long getFileLength() {
		return file.length();
	}

	public T get(long position) {
		try {
			raf = new RandomAccessFile(file, "r");
			raf.seek(position);
			InputStream inputStream = Channels.newInputStream(raf.getChannel());
			Input input = new Input(inputStream);
			Registration registration = kryo.readClass(input);
			Object o = kryo.readObject(input, registration.getType());
			input.close();
			return (T) o;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/*public T get(long position) {
		try {
			//this.raf = new RandomAccessFile(file, "r");
			channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		try {
			channel.position(position);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		InputStream inputStream = Channels.newInputStream(channel);
		Input input = new Input(inputStream);
		Registration registration = kryo.readClass(input);
		Object o = kryo.readObject(input, registration.getType());
		input.close(); /// seems necesay, what with close()  ??????????????????????
		return (T) o;
	}*/
	public void close() {
		try {
			raf.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	/*try {
			//channel.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	 */


 /*public static void main(String[] args) {
		File file = new File("d:/t/data/KryoReaderPositional.file");
		if (file.exists()) {
			file.delete();
		}

		try {
			channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		try {
			channel.position(position);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		InputStream inputStream = Channels.newInputStream(channel);
		Input input = new Input(inputStream);
		Registration registration = kryo.readClass(input);
		Object o = kryo.readObject(input, registration.getType());
		input.close(); ///

		if (file.exists()) {
			file.delete();
		}
	}*/
}
