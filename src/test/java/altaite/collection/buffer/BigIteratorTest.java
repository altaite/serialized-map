package altaite.collection.buffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class BigIteratorTest {

	public BigIteratorTest() {
	}

	@Test
	public void testHasNext() {
	}

	@Test
	public void testNext() {
	}

	@Test
	public void testClose() {
	}

	/*public void testIterator() {
		System.out.println(BigCollectionResource.getBigListTestPath().toFile());
		List<Dummy> list = DummyFactory.createList(size, elementSize);
		BigList<Dummy> bigList = BigList.<Dummy>clearAndOpen(BigCollectionResource.getBigListTestPath());
		for (Dummy dummy : list) {
			bigList.add(dummy);
		}
		bigList.closeWriter();
		for (int i = 0; i < 2; i++) {
			for (Dummy x : bigList) {
				Dummy y = list.get(x.getId());
				assert x.equals(y);
			}
		}
		// iterator closes reader automatically
	}*/
}
