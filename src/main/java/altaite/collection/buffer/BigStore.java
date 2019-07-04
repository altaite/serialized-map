package altaite.collection.buffer;

import altaite.io.FileOperation;
import altaite.util.Flag;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Common core of BigIn and BigOut.
 */
public class BigStore {

	private BigResource resources;
	private List<Long> pointers;
	private Flag flag;

	public BigStore(BigResource resource) {
		this.resources = resource;
		resources.makeDirIfAbsent();
		flag = new Flag(resources.getPointerWritingFlagFile());

		if (pointersUnfinished()) {
			trimPointers();
		}
		initializePointers();
		if (flag.isOn()) {
			if (pointers.size() > 0) {
				pointers.remove(pointers.size() - 1);
			}
			recreatePointers();
		}
		initializePointers();
		if (pointers.isEmpty()) {
			FileOperation.truncate(resources.getPointerFile(), 0);
			FileOperation.truncate(resources.getDataFile(), 0);
		} else {
			resolveEndDifferences();
		}
		flag.off();
		initializePointers();

		assert getLastPointer() == resources.getDataFile().length() :
			getLastPointer() + " == " + resources.getDataFile().length();
		assert pointers.get(0) == 0;
	}

	public void truncate(int size) {
		shortenPointers(size + 1);
		FileOperation.truncate(resources.getDataFile(), getLastPointer());
	}

	public BigResource getResources() {
		return resources;
	}

	private void initializePointers() {
		pointers = new ArrayList<>();
		pointers.add(0L); // beware, not in file
		if (resources.getPointerFile().exists() && resources.getPointerFile().length() > 0) {
			loadPointers();
		}
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

	private boolean pointersUnfinished() {
		long length = resources.getPointerFile().length();
		boolean wrongLength = length % 8 != 0; // 8 - file of long.
		return wrongLength;
	}

	private void trimPointers() {
		long length = resources.getPointerFile().length();
		long newLength = length / 8 * 8;
		FileOperation.truncate(resources.getPointerFile(), newLength);
	}

	private void printPointers() {
		for (int i = 0; i < pointers.size(); i++) {
			System.out.print(pointers.get(i) + " ");
		}
		System.out.println("");
	}

	private void resolveEndDifferences() {
		long reported = pointers.get(pointers.size() - 1);
		long actual = resources.getDataFile().length();
		if (reported < actual) {
			FileOperation.truncate(resources.getDataFile(), reported);
		} else if (actual < reported) {
			int i = pointers.size() - 1;
			while (i > 0 && actual < pointers.get(i)) {
				i--;
			}
			FileOperation.truncate(resources.getDataFile(), pointers.get(i));
			shortenPointers(i + 1);

		}
	}

	private void shortenPointers(int removeFrom) {
		for (int k = pointers.size() - 1; k >= removeFrom; k--) {
			pointers.remove(k);
		}
		recreatePointers();
	}

	private void recreatePointers() {
		try (DataOutputStream pointerWriter = new DataOutputStream(
			new BufferedOutputStream(
				new FileOutputStream(resources.getPointerFile())))) {
			for (int i = 1; i < pointers.size(); i++) { // 0 as first pointer is never written into file
				long p = pointers.get(i);
				pointerWriter.writeLong(p);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public long getPointer(int index) {
		return pointers.get(index);
	}

	public void addPointer(long position) {
		pointers.add(position);
	}

	public int size() {
		return pointers.size() - 1;
	}

	/**
	 *
	 * @return Yet unwritten position in a data file, its length according to pointers in memory.
	 */
	public long getLastPointer() {
		return pointers.get(pointers.size() - 1);
	}

	public boolean isEmpty() {
		return pointers.size() <= 1;
	}

	public void setFlagOn() {
		flag.on();
	}

	public void setFlagOff() {
		flag.off();
	}

	public void close() {
		flag.close();
	}
}
