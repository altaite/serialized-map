package altaite.collection.buffer;

public class BigCorruptionTest {
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
