package altaite.collection.buffer.map;

import altaite.collection.TestResource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class MapTest {

	private Random random = new Random(1);
	private Path dir = TestResource.getTemporaryDir();

	public MapTest() {
		clean();
	}

	@Test
	public void testBasic() {
		testNormal();
		testCorruption();
	}

	public void testNormal() {
		Map<String, String> map = createMap();

		MapOut<String, String> out = new MapOut<>(dir);
		for (String key : map.keySet()) {
			String value = map.get(key);
			out.put(key, value);
		}
		out.close();

		MapIn<String, String> in = new MapIn<>(dir);
		for (String key : in.keySet()) {
			String value = in.get(key);
			String otherValue = map.get(key);
			assert otherValue.equals(value);
		}

		for (String key : map.keySet()) {
			String value = map.get(key);
			String otherValue = in.get(key);
			assert otherValue.equals(value);
		}
		in.close();
	}

	private void testCorruption() {
		MapOut<String, String> out = new MapOut<>(dir);
		out.TEST_ONLY_ADD_KEY("key");
		out.close();

		MapIn<String, String> in = new MapIn<>(dir);
		assert in.size() == 0;
		in.close();

	}

	private Map<String, String> createMap() {
		Map<String, String> map = new HashMap<>();
		map.put("1", "one");
		map.put("2", "two");
		map.put("3", "three");
		map.put("4", "four");
		return map;
	}

	private void clean() {
		try {
			FileUtils.deleteDirectory(dir.toFile());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Test
	public void testAppendFalse() {
		testAppend(false, 1);
	}

	@Test
	public void testAppendTrue() {
		testAppend(true, 2);
	}

	private void testAppend(boolean append, int size) {
		MapOut<String, String> out;

		out = new MapOut<>(dir);
		out.put("a", "aa");
		out.close();

		out = new MapOut<>(dir, append);
		out.put("bb", "bbb");
		out.close();

		MapIn<String, String> in = new MapIn<>(dir);
		assert in.size() == size;
		in.close();
	}

}
