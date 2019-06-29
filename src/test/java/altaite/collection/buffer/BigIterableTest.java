package altaite.collection.buffer;

import altaite.collection.TestResource;
import altaite.collection.dummy.Dummy;
import altaite.collection.dummy.DummyFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class BigIterableTest {

	private Path dir = TestResource.getTemporaryDir();
	private BigResource resource = new BigResource(dir);
	private final int size = 100;
	private final int elementSize = 100;

	public BigIterableTest() {
		clean();
	}

	public void testIterator() {
		resource.clear();

		List<Dummy> list = DummyFactory.createList(size, elementSize);
		BigOut<Dummy> out = new BigOut<>(resource);
		out.addAll(list);
		out.close();

		BigIterable<Dummy> it = new BigIterable<>(resource);
		int i = 0;
		for (Dummy dummy : it) {
			Dummy other = list.get(i);
			assert dummy.equals(other);
			i++;
		}
	}

	private void clean() {
		try {
			FileUtils.deleteDirectory(dir.toFile());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
