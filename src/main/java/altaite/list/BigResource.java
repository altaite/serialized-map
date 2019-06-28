package altaite.list;

import altaite.io.FileOperation;
import altaite.util.Flag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BigResource {

	private Path path;
	private File dataFile;
	private File pointerFile;
	private Flag pointerWritingFlag; // label is on when writting, for detection of interrupted writting

	public BigResource(Path dir) {
		this.path = dir;
		initializePaths(dir);
	}

	public void initializePaths(Path path) {
		this.path = path;
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		this.dataFile = path.resolve("big_list.kryo").toFile();
		this.pointerFile = path.resolve("big_list.pointers").toFile();
		this.pointerWritingFlag = new Flag(path.resolve("big_list.flag").toFile());
	}

	public void delete() {
		try {
			FileOperation.checkedDelete(dataFile);
			FileOperation.checkedDelete(pointerFile);
			pointerWritingFlag.deleteFile();
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

	public Flag getPointerWritingFlag() {
		return pointerWritingFlag;
	}
}
