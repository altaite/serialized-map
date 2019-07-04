package altaite.collection.performance;

import java.util.Random;

public class Measurement {

	private int numberOfItems = 10000;
	private int maxArraySize = 10_000;
	private Random random;
	private int[] chunk = new int[maxArraySize];

	private void run() {
		measureRandomAccessStorage();
	}

	private void measureRandomAccessStorage() {
	/*	BigList storage = BigList.create(BigCollectionResource.getBigListMeasurementPath());
		Timer.start();
		write(storage);
		Timer.stop();
		System.out.println("write ras " + Timer.get());
		Timer.start();
		read(storage);
		Timer.stop();
		System.out.println("read ras " + Timer.get());

		BigIterable iterable = BigIterable.create(BigCollectionResource.getBigIterableMeasurementPath());
		Timer.start();
		write(iterable);
		Timer.stop();
		System.out.println("write ite " + Timer.get());
		Timer.start();
		read(iterable);
		Timer.stop();
		System.out.println("read ite " + Timer.get());*/
	}

	/*private void write(BigList storage) {
		initialize();
		for (int i = 0; i < numberOfItems; i++) {
			int[] item = next();
			storage.add(item);
		}
		storage.closeWriter();
	}

	private void read(BigList storage) {
		initialize();
		for (int i = 0; i < numberOfItems; i++) {
			//int r = random.nextInt(numberOfItems);
			storage.get(i);
		}
		storage.closeReader();
	}*/

	/*private void write(BigIterable<int[]> storage) {
		initialize();
		for (int i = 0; i < numberOfItems; i++) {
			int[] item = next();
			storage.add(item);
		}
		storage.closeForAdding();
	}

	private void read(BigIterable<int[]> storage) {
		initialize();
		for (int[] a : storage) {
			int r = random.nextInt(numberOfItems);
		}
	}*/

	private void initialize() {
		random = new Random(1);
	}

	private int[] next() {
		if (chunk != null) {
			return chunk;
		} else {
			return new int[random.nextInt(maxArraySize)];
		}
	}

	public static void main(String[] args) {
		Measurement m = new Measurement();
		m.run();
	}
}
