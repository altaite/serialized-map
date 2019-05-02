package altaite.collection.dummy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Dummy implements Serializable {

	private int[] array;
	private int id;

	public Dummy(int id, int size) {
		this.id = id;
		this.array = new int[size];
		Random random = new Random(1);
		for (int i = 0; i < array.length; i++) {
			array[i] = random.nextInt();
		}
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		Dummy dummy = (Dummy) o;
		if (id != dummy.id) {
			return false;
		}
		if (!Arrays.equals(array, dummy.array)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Arrays.hashCode(this.array);
		hash = 97 * hash + Objects.hashCode(this.id);
		return hash;
	}

}
