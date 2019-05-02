package altaite.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class FileOperation {

	public static void truncate(File file, long length) {
		if (file.exists()) {
			try (FileChannel outChan = new FileOutputStream(file, true).getChannel()) {
				outChan.truncate(length);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static void checkedDelete(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		Files.delete(file.toPath());
		//if (!file.delete()) {
		//	throw new IOException("Cannot delete " + file);
		//}
	}

}
