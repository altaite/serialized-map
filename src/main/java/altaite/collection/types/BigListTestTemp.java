package altaite.collection.types;

import altaite.BigCollectionResource;
import altaite.io.FileOperation;
import java.io.File;

public class BigListTestTemp {

	private void testCorruption() {
		BigList<Integer> a = createClear();
		initialize(a, 10);

		System.out.println(a.toString());

		//shortenFile(a.getDataFile(), 0.5);
	}

	private BigList<Integer> createClear() {
		BigList<Integer> a = new BigList<>(BigCollectionResource.getBigListTestPath());
		a.clear();
		a = new BigList<>(BigCollectionResource.getBigListTestPath());
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

	public static void main(String[] args) throws Exception {
		BigListTestTemp m = new BigListTestTemp();
		m.testCorruption();
	}

	/*public static void main(String[] args) throws Exception {
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
		assert a.get(1) == 1 : a.get(1);
		assert a.get(2) == 2 : a.get(2);
		a.closeReader();

		//clean(BigCollectionResource.getBigListTestPath());
		a = new BigList<>(BigCollectionResource.getBigListTestPath());
		a.clear();
		a = new BigList<>(BigCollectionResource.getBigListTestPath());
		a.add(3);
		//assert a.get(0) == 0;
		//assert a.get(1) == 1;
		//assert a.get(2) == 2;
		a.closeWriter();
		a.closeReader();
		a.clear();
	}*/
}
