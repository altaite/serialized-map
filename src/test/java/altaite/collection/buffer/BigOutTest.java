package altaite.collection.buffer;

import altaite.collection.TestResource;
import java.nio.file.Path;
import org.junit.Test;

public class BigOutTest {

	private Path dir = TestResource.getTemporaryPath("BigOutTest");

	@Test
	public void testAppend() {
		BigResource resource = new BigResource(dir);

		resource.clear();

		BigOut<Integer> out = new BigOut<>(resource);
		out.add(3);
		out.close();

		out = new BigOut<>(resource);
		out.add(2);
		out.close();

		BigIn<Integer> in = new BigIn<>(resource);
		assert in.size() == 2;
		in.close();
	}

}
