package altaite.collection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Resource {

	private static Path home = Paths.get("d:/t/data/serialized-map");

	public static Path getTemporaryDir() {
		Path temp = home.resolve("temporary");
		if (!Files.exists(temp)) {
			try {
				Files.createDirectories(temp);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		return temp;
	}
}
