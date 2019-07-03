package altaite.kryo;

import altaite.BigCollectionResource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class KryoReaderPositionalTest {

	private File file = BigCollectionResource.getTemporaryTestFile();
	private long[] positions;
	private List<Something> objects;

	@Test
	public void testClass() {
		//initialize();
		//write();
		//read();
	}

	private void initialize() {
		objects = createObjects();
		positions = new long[objects.size() + 1];
	}

	private void write() {
		KryoWriter<Something> writer = new KryoWriter<>(file);
		int i = 0;
		positions[0] = 0L;
		for (Something o : objects) {
			positions[++i] = writer.write(o); // WRONG, returns last write only
		}
		writer.close();
	}

	private List<Something> createObjects() {
		List<Something> list = new ArrayList<>();
		list.add(new A("a"));
		list.add(new B("bb", 3));
		list.add(new B("bbb", 34));
		return list;
	}

	private void read() {
		KryoReaderPositional<Something> kryoReader = new KryoReaderPositional<>(file);
		for (int i = objects.size() - 1; i >= 0; i--) {
			Something x = kryoReader.get(positions[i]);
			Something o = objects.get(i);
			assert x.getValue().equals(o.getValue()) :
				x.getValue() + " " + o.getValue();
		}
	}

}

interface Something {

	public String getValue();
}

interface TextWrap extends Something {

}

interface ArrayWrap extends Something {

	public int[] getArray();

}

class A implements TextWrap {

	private String value;

	public A(String s) {
		this.value = s;
	}

	@Override
	public String getValue() {
		return value;
	}
}

class B extends A implements ArrayWrap {

	int[] array;

	public B(String s, int n) {
		super(s);
		array = new int[n];
	}

	@Override
	public int[] getArray() {
		return array;
	}
}
