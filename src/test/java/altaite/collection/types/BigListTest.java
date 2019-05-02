package altaite.collection.types;

import altaite.BigCollectionResource;
import altaite.collection.dummy.Dummy;
import altaite.collection.dummy.DummyFactory;
import altaite.io.FileOperation;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class BigListTest {

	//private List<Dummy> list;
	//private BigList<Dummy> bigList;
	private final int size = 100;
	private final int elementSize = 100;

	private void clean() {
		try {
			FileUtils.deleteDirectory(BigCollectionResource.getBigListTestPath().toFile());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void createBigList() {
		clean();
		List<Dummy> list = DummyFactory.createList(size, elementSize);
		BigList<Dummy> bigList = new BigList<>(BigCollectionResource.getBigListTestPath());
		bigList.addAll(list);
		bigList.closeWriter();
	}

	@Test
	public void testGet() {
		clean();
		List<Dummy> list = DummyFactory.createList(size, elementSize);
		BigList<Dummy> bigList = new BigList<>(BigCollectionResource.getBigListTestPath());
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
	}

	@Test
	public void testIterator() {
		clean();
		System.out.println(BigCollectionResource.getBigListTestPath().toFile());
		List<Dummy> list = DummyFactory.createList(size, elementSize);
		BigList<Dummy> bigList = new BigList<>(BigCollectionResource.getBigListTestPath());
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
	}
	 
	@Test
	public void testLoading() {
		clean();

		BigList<Integer> a = new BigList<>(BigCollectionResource.getBigListTestPath());
		a.clear();
		a = new BigList<>(BigCollectionResource.getBigListTestPath());

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
		a = new BigList<>(BigCollectionResource.getBigListTestPath());

		//a.add(3);
		assert a.get(0) == 0;
		assert a.get(1) == 1;
		assert a.get(2) == 2;
		a.closeWriter();
		a.closeReader();
		clean();
	}

	@Test
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
	}
}
