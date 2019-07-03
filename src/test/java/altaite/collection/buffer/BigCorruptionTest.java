package altaite.collection.buffer;

import altaite.collection.TestResource;
import altaite.collection.dummy.Dummy;
import altaite.collection.dummy.DummyFactory;
import altaite.io.FileOperation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class BigCorruptionTest {

	private Path dir = TestResource.getTemporaryDir();
	private BigResource resource = new BigResource(dir);

	private final int size = 100;
	private final int elementSize = 100;
	private List<Dummy> list = DummyFactory.createList(size, elementSize);

	public BigCorruptionTest() {
		clean();
	}

	@Test
	public void testCorruption() {
		create();
		corruptData();
		assert resource.getDataFile().exists();
		assert resource.getPointerFile().exists();
		testGet();
		testIterator();
	}

	private void testIterator() {
		BigIterable<Dummy> it = new BigIterable<>(resource);
		int i = 0;
		for (Dummy dummy : it) {
			Dummy other = list.get(i);
			assert dummy.equals(other);
			i++;
		}
	}

	private void testGet() {
		BigIn<Dummy> in = new BigIn<>(resource);
		System.out.println("Fixed to length " + in.size() + " from " + list.size());

		for (int i = 0; i < in.size(); i++) {
			Dummy x = in.get(i);
			Dummy o = list.get(i);
			assert x.equals(o) : i + " " + x.getId() + " != " + o.getId();
		}
		in.close();
	}

	private void create() {
		resource.clear();

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

	private void corruptData() {
		Random random = new Random(1);

		File data = resource.getDataFile();

		long dataLength = data.length();
		double corruptedEndPercent = 0.3;
		long truncatedDataLength = Math.round((1 - random.nextDouble() * corruptedEndPercent) * dataLength);

		FileOperation.truncate(data, truncatedDataLength);
	}

	private void clean() {
		try {
			FileUtils.deleteDirectory(dir.toFile());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
