package altaite.collection.util;

import altaite.collection.types.BigCollection;
import altaite.kryo.KryoFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;

public class BigSerializer<C extends BigCollection<?>> {

	public void serialize(C bigCollection) {
		File file = getFileForSerialization(bigCollection.getPath());
		Kryo kryo = KryoFactory.createKryo();
		try {
			Output output = new Output(new FileOutputStream(file));
			kryo.writeClass(output, bigCollection.getClass());
			kryo.writeObject(output, bigCollection);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public C deserialize(Path path) {
		Kryo kryo = KryoFactory.createKryo();
		Input input = KryoFactory.createInput(getFileForSerialization(path));
		Registration registration = kryo.readClass(input);
		C collection =  (C) kryo.readObject(input, registration.getType());
		collection.initializePaths(path);
		return collection;
	}

	public static File getFileForSerialization(Path path) {
		return path.resolve("big_collection.kryo").toFile();
	}

}
