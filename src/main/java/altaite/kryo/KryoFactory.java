package altaite.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoFactory {

	public static Kryo createKryo() {
		Kryo kryo = new Kryo();
		kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(
			new StdInstantiatorStrategy()));
		return kryo;
	}

	public static Input createInput(File file) {
		try {
			return new Input(new FileInputStream(file));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Output createOutput(File file) {
		try {
			return new Output(new FileOutputStream(file));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
