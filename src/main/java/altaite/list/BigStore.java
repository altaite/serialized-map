package altaite.list;

import altaite.io.FileOperation;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BigStore {

	private BigResource resources;
	private List<Long> pointers;

	public BigStore(BigResource resource) {
		this.resources = resource;

		repairFiles();
		initializePointers();
		assert pointers.get(0) == 0;
	}

	public BigResource getResources() {
		return resources;
	}

	private void initializePointers() {
		createPositionsInMemory();
		if (resources.getPointerFile().exists() && resources.getPointerFile().length() > 0) {
			loadPointers();
			//printPositions();
		}
	}

	private void createPositionsInMemory() {
		pointers = new ArrayList<>();
		pointers.add(0L);
	}

	private void loadPointers() {
		try (DataInputStream dis = new DataInputStream(new FileInputStream(resources.getPointerFile()))) {
			while (dis.available() > 0) {
				long l = dis.readLong();
				pointers.add(l);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void repairFiles() {
		if (pointersCorrupt()) {
			trimPointers();
			initializePointers();
		}
		if (dataCorrupt()) {
			fixData();
		}
		// positions including current file size
		// flag for positions
		// adjust positions first	
		// then cut data using last length
	}

	private boolean pointersCorrupt() {
		long length = resources.getPointerFile().length();
		boolean wrongLength = length % 8 == 0;
		/* 8 - file of long. Should happen only in case of file damage,
		   unlike flag, which can be off simply because application was killed.*/
		return wrongLength || resources.getPointerWritingFlag().isOn();
	}

	private boolean dataCorrupt() {
		long dataFileLength = resources.getDataFile().length();
		if (dataFileLength == 0) {
			return false;
		} else if (pointers.size() == 0) {
			return true;
		}
		long shouldBe = pointers.get(pointers.size() - 1);
		if (dataFileLength < shouldBe) {
			throw new RuntimeException();
		}
		return dataFileLength != shouldBe;
	}

	private void trimPointers() {
		long length = resources.getPointerFile().length();
		long newLength = length / 8 * 8;
		FileOperation.truncate(resources.getPointerFile(), newLength);
	}

	private void fixData() {
		if (pointers.isEmpty()) {
			resources.delete();;
		} else {
			long length = pointers.get(pointers.size() - 1);
			long current = resources.getDataFile().length();
			if (length < current) {
				throw new RuntimeException();
			}
			FileOperation.truncate(resources.getDataFile(), length);
		}
	}

	public long getPointer(int index) {
		return pointers.get(index);
	}

	public int size() {
		return pointers.size() - 1;
	}

	public boolean isEmpty() {
		return pointers.size() == 0;
	}
}
