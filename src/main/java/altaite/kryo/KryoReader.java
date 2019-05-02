package altaite.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Deserializes one or more objects of the same type. Assumes the class is written at the start of the file and objects
 * were serialized using KryoWriter.
 *
 * @param <T> Class of objects to be deserialized.
 */
public class KryoReader<T> {

	private Kryo kryo;
	private Input input;

	public KryoReader(File file) {
		kryo = KryoFactory.createKryo();
		try {
			input = new Input(new FileInputStream(file));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean hasNext() {
		return !input.eof();
	}

	public T read() {
		if (input.eof()) {
			return null;
		}
		Registration registration = kryo.readClass(input);
		Object o = kryo.readObject(input, registration.getType());
		return (T) o;
	}

	public void close() {
		input.close();
	}
}
