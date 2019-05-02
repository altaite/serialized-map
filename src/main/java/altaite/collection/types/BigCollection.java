package altaite.collection.types;

import java.io.Serializable;
import java.nio.file.Path;

public interface BigCollection<T extends Serializable> extends Iterable<T> {

	/*public static BigCollection create() {
		return null;
	}*/
	void initializePaths(Path path);

	Path getPath();

	public void closeWriter();

	public void closeReader();
}
