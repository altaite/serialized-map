package altaite.util;

import altaite.io.FileOperation;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Flag {

	private File file;
	private RandomAccessFile raf;

	public Flag(File file) {
		this.file = file;
		try {
			raf = new RandomAccessFile(file, "rw");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void on() {
		try {
			raf.seek(0);
			raf.writeBoolean(true);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

	public void off() {
		try {
			raf.seek(0);
			raf.writeBoolean(false);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isOff() {
		return !isOn();
	}

	public boolean isOn() {
		try {
			if (!file.exists() || raf.length() == 0) {
				return false;
			} else {
				raf.seek(0);
				return raf.readBoolean();
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void close() {
		try {
			raf.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void deleteFile() throws IOException {
		try {
			raf.close();
		} catch (Exception ex) {
		}
		FileOperation.checkedDelete(file);
	}
}
