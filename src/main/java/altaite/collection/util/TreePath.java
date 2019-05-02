package altaite.collection.util;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Antonin Pavelka
 */
public class TreePath implements Serializable {

	private List<Integer> list = new ArrayList<>();
	private int leafId;

	public TreePath() {
		// for Kryo
	}

	public TreePath(int leafId) {
		this.leafId = leafId;
	}

	public int getLeafId() {
		return leafId;
	}

	public void add(int item) {
		list.add(item);
	}

	public Path getPath(Path dir) {
		Path path = dir;
		for (int i = list.size() - 1; i >= 0; i--) {
			Integer node = list.get(i);
			String filename;
			if (i > 0) {
				filename = node.toString();
			} else {
				filename = node.toString() + ".f";
			}
			path = path.resolve(filename);
			if (i > 0) {
				conditionalCreate(path);
			}
		}
		return path;
	}

	private void conditionalCreate(Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}
