package altaite.collection.buffer;

import altaite.collection.TestResource;
import altaite.collection.dummy.Dummy;
import altaite.collection.dummy.DummyFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class BigIntegrationTest {

	private Path dir = TestResource.getTemporaryDir();
	private BigResource resource = new BigResource(dir);

	private final int size = 100;
	private final int elementSize = 100;

	public BigIntegrationTest() {
		clean();
	}

	@Test
	public void testGet() {
		resource.clear();

		List<Dummy> list = DummyFactory.createList(size, elementSize);

		BigOut<Dummy> out = new BigOut<>(resource);
		for (Dummy dummy : list) {
			out.add(dummy);
		}
		out.close();

		BigIn<Dummy> in = new BigIn<>(resource);
		Random random = new Random(1);
		for (int i = 0; i < size; i++) {
			int r = random.nextInt(list.size());
			Dummy x = in.get(r);
			Dummy o = list.get(r);
			assert x.equals(o) : i + " " + x.getId() + " != " + o.getId();
		}
		in.close();
	}

	@Test
	public void testMultistepAdd() {
		resource.clear();

		BigOut<Integer> a = new BigOut<>(resource);
		a.add(0);
		a.add(1);
		a.add(2);
		a.close();

		BigIn<Integer> b = new BigIn<>(resource);
		assert b.size() == 3 : b.size();
		assert b.get(0) == 0 : b.get(0);
		assert b.get(1) == 1 : b.get(1);
		assert b.get(2) == 2 : b.get(2);
		b.close();

		BigOut<Integer> c = new BigOut<>(resource);
		c.add(3); // this without clear will cause error
		c.close();

		BigIn<Integer> d = new BigIn<>(resource);
		assert d.size() == 4 : d.size();
		assert d.get(0) == 0 : d.get(0);
		assert d.get(1) == 1 : d.get(1);
		assert d.get(2) == 2 : d.get(2);
		assert d.get(3) == 3 : d.get(3);
		d.close();
	}

	private void clean() {
		try {
			FileUtils.deleteDirectory(dir.toFile());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void write() {
		clean();
		List<Dummy> list = DummyFactory.createList(size, elementSize);
		BigOut<Dummy> out = new BigOut<>(resource);
		out.addAll(list);
		out.close();
	}
}
