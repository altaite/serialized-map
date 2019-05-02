package altaite;

import java.io.File;
import java.nio.file.Path;

public class BigCollectionResource {

	private static Path home = new File("d:/t/data/serialized-map").toPath();

	public static Path getHome() {
		return home;
	}

	public static Path getTest() {
		return getHome().resolve("test");
	}

	public static File getTestFile() {
		return getTest().resolve("file").toFile();
	}

	public static Path getTemporaryTestPath() {
		return getTest().resolve("path");
	}

	public static File getTemporaryTestFile() {
		return getTest().resolve("file").toFile();
	}

	public static Path getBigListMeasurementPath() {
		return getTest().resolve("big_list");
	}

	public static Path getBigIterableMeasurementPath() {
		return getTest().resolve("measure_iterable");
	}

	public static Path getBigListTestPath() {
		return getTest().resolve("big_list_test");
	}

}
