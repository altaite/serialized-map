
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class EntryFile<Entry> {

	public static final String DATA_EXT = ".dat";
	public static final String INDEX_EXT = ".idx";
	public static final int INDEX_SIZE = Long.SIZE / 8;
	private FileChannel fci;
	private FileChannel fcd;

	public EntryFile(String fileName)
		throws FileNotFoundException, IOException {

		fci = new RandomAccessFile(fileName + EntryFile.INDEX_EXT, "rw").getChannel();
		fci.force(true);
		fcd = new RandomAccessFile(fileName + EntryFile.DATA_EXT, "rw").getChannel();
		fcd.force(true);
	}

	public void close()
		throws IOException {

		fcd.close();
		fci.close();
	}

	public long appendEntry(Entry entry)
		throws IOException {

		// Calculate the data index for append to data
		// file and append its value to the index file.
		long byteOffset = fci.size();
		long index = byteOffset / (long) EntryFile.INDEX_SIZE;
		long dataOffset = (int) fcd.size();
		ByteBuffer bb = ByteBuffer.allocate(EntryFile.INDEX_SIZE);
		bb.putLong(dataOffset);
		bb.flip();
		fci.position(byteOffset);
		fci.write(bb);

		// Append serialized object data to the data file.
		/*byte[] se = Entry.serialize(entry);
		fcd.position(dataOffset);
		fcd.write(ByteBuffer.wrap(se));
*/
		return index;
	}

	public Entry readEntry(long index)
		throws IOException, ClassNotFoundException {

		// Get the data index and -length from the index file.
		long byteOffset = index * (long) EntryFile.INDEX_SIZE;
		ByteBuffer bbi = ByteBuffer.allocate(EntryFile.INDEX_SIZE);
		fci.position(byteOffset);
		if (fci.read(bbi) == -1) {
			throw new IndexOutOfBoundsException("Specified index is out of range");
		}
		bbi.flip();
		long dataOffset = bbi.getLong();
		bbi.rewind();
		long dataOffsetNext;
		if (fci.read(bbi) == -1) {
			dataOffsetNext = fcd.size();
		} else {
			bbi.flip();
			dataOffsetNext = bbi.getLong();
		}
		int dataSize = (int) (dataOffsetNext - dataOffset);

		// Get the serialized object data in a byte array.
		byte[] se = new byte[dataSize];
		fcd.position(dataOffset);
		fcd.read(ByteBuffer.wrap(se));

		// Deserialize the byte array into an instantiated object.
		return null;//Entry.deserialize(se);
	}
}
