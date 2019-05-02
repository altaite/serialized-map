package altaite.collection.types;

import altaite.collection.Resource;
import java.util.Random;
import org.junit.Test;

public class ListOfListsTest {

	private ListOfLists<Integer> list;

	public ListOfListsTest() {
		this.list = new ListOfLists(Resource.getTemporaryDir());
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
