package altaite.collection.types;

import altaite.collection.TestResource;
import java.util.Random;
import org.junit.Test;

public class ListOfListsTest {

	private ListOfLists<Integer> list;

	public ListOfListsTest() {
		this.list = new ListOfLists(TestResource.getTemporaryDir());
	}

	@Test
	public void testAdd() {
		Random random = new Random(1);
		for (int i = 0; i < 100; i++) {
			list.add(random.nextInt(100), i);
		}
	}

	@Test
	public void testGetReader() {
	}

}
