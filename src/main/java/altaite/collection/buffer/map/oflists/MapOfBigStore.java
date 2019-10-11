package altaite.collection.buffer.map.oflists;

import altaite.collection.util.RadixTree;
import altaite.collection.util.TreePath;
import java.nio.file.Path;

public class MapOfBigStore {

	private Path dir;

	public MapOfBigStore(Path dir) {
		this.dir = dir;
	}

	public Path getSubdir(TreePath treePath) {
		return treePath.getPath(dir);
	}

	public RadixTree getRadixTree() {
		return new RadixTree(500);
	}
}
