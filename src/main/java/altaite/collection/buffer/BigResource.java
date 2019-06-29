package altaite.collection.buffer;

import altaite.io.FileOperation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BigResource {

	private Path path;
	private File dataFile;
	private File pointerFile;
	private File pointerWritingFlagFile; // label is on when writting, for detection of interrupted writting

	public BigResource(Path dir) {
		this.path = dir;
		initializePaths(dir);
	}

	public void makeDirIfAbsent() {
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public void clear() {
		try {
			FileOperation.checkedDelete(dataFile);
			FileOperation.checkedDelete(pointerFile);
			FileOperation.checkedDelete(pointerWritingFlagFile);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public File getDataFile() {
		return dataFile;
	}

	public File getPointerFile() {
		return pointerFile;
	}

	public File getPointerWritingFlagFile() {
		return pointerWritingFlagFile;
	}

	private void initializePaths(Path path) {
		this.path = path;
		makeDirIfAbsent();
		this.dataFile = path.resolve("big_list.kryo").toFile();
		this.pointerFile = path.resolve("big_list.pointers").toFile();
		this.pointerWritingFlagFile = path.resolve("big_list.flag").toFile();
	}

}
