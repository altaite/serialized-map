package altaite.collection.types;

import altaite.BigCollectionResource;
import altaite.io.FileOperation;
import java.io.File;
import java.nio.file.Path;

public class BigListTestTemp {

	private void testCorruption() {
		BigList<Integer> a = createClear();
		initialize(a, 10);

		System.out.println(a.toString());

		//shortenFile(a.getDataFile(), 0.5);
	}

	private BigList<Integer> createClear() {
		Path dir = BigCollectionResource.getBigListTestPath();
		BigList<Integer> a = BigList.<Integer>clearAndOpen(dir);
		return a;
	}

	private void initialize(BigList<Integer> list, int n) {
		for (int i = 0; i < n; i++) {
			list.add(i);
		}
		list.closeWriter();
	}

	private void shortenFile(File file, double percentage) {
		FileOperation.truncate(file, Math.round(file.length() * percentage));
	}

	/*public static void main(String[] args) throws Exception {
		BigListTestTemp m = new BigListTestTemp();
		m.testCorruption();
	}*/
	public static void main(String[] args) throws Exception {
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

		//BigList<Integer> b = BigList.<Integer>open(dir);
		BigList<Integer> b = BigList.<Integer>open(dir);
		b.closeWriter();
		assert b.size() == 3;
		assert b.get(0) == 0;
		assert b.get(1) == 1;
		assert b.get(2) == 2;
		b.closeReader();

		BigList<Integer> c = BigList.<Integer>open(dir);
		c.add(3); // this without clear will cause error
		c.closeWriter();
		// TODO split BigListWriter, BigListReader
		assert c.get(0) == 0 : c.get(0); // initialize pointer end? seek datawriter towards end?
		assert c.get(1) == 1;
		assert c.get(2) == 2;
		assert c.get(3) == 3;
		b.closeReader();
	}
}
