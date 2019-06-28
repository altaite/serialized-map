package altaite.collection.types;

import altaite.BigCollectionResource;
import altaite.collection.dummy.Dummy;
import altaite.collection.dummy.DummyFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.junit.Test;

public class BigListTest {

	//private List<Dummy> list;
	//private BigList<Dummy> bigList;
	private final int size = 100;
	private final int elementSize = 100;

	/*private void clean() {
		try {
			FileUtils.deleteDirectory(BigCollectionResource.getBigListTestPath().toFile());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}*/

 /*private void createBigList() {
		clean();
		List<Dummy> list = DummyFactory.createList(size, elementSize);
		BigList<Dummy> bigList = new BigList<>(BigCollectionResource.getBigListTestPath());
		bigList.addAll(list);
		bigList.closeWriter();
	}*/
	/*@Test
	public void testGet() {
		//clean();
		List<Dummy> list = DummyFactory.createList(size, elementSize);
		Path dir = BigCollectionResource.getBigListTestPath();
		BigList<Dummy> bigList = BigList.<Dummy>clearAndOpen(dir);
		for (Dummy dummy : list) {
			bigList.add(dummy);
		}
		bigList.closeWriter();
		Random random = new Random(1);
		for (int i = 0; i < size; i++) {
			int r = random.nextInt(list.size());
			Dummy x = bigList.get(r);
			Dummy o = list.get(r);
			assert x.equals(o) : i + " " + x.getId() + " != " + o.getId();
		}
		bigList.closeReader();
	}*/

	/*@Test
	public void testIterator() {
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

	@Test
	public void testLoading() {
		Path dir = BigCollectionResource.getBigListTestPath();
		BigList<Integer> a = BigList.<Integer>clearAndOpen(dir);

		a.add(0);
		a.add(1);
		a.add(2);
		a.closeWriter();
		System.out.println(a.size());

		System.out.println(a.get(0) + " ***");
		System.out.println(a.get(1) + " ***");
		System.out.println(a.get(2) + " ***");
		assert a.get(0) == 0 : a.get(0);
		assert a.get(1) == 1;
		assert a.get(2) == 2;
		a.closeReader();
		a = BigList.<Integer>open(dir);
		a.add(3); // this without clear will cause error
		assert a.get(0) == 0;
		assert a.get(1) == 1;
		assert a.get(2) == 2;
		a.closeWriter();
		a.closeReader();
	}

	/*	@Test
	public void testCorruption() {
		clean();
		BigList<Integer> a = new BigList<>(BigCollectionResource.getBigListTestPath());
		a.clear();
		a = new BigList<>(BigCollectionResource.getBigListTestPath());

		a.add(0);
		a.add(1);
		a.add(2);
		a.closeWriter();

		Random random = new Random(1);

		File data = a.getDataFile();
		File pointers = a.getPointerFile();

		long dataLength = data.length();
		long truncatedDataLength = Math.round(random.nextDouble() * dataLength);

		FileOperation.truncate(data, truncatedDataLength);
		// create list
		// shorten files
		// load it from hdd

		clean();
	}*/
}
