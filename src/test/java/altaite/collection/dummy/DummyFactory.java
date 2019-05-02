package altaite.collection.dummy;

import java.util.ArrayList;
import java.util.List;

public class DummyFactory {

	public static List<Dummy> createList(int size, int elementSize) {
		List<Dummy> list = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			list.add(new Dummy(i, elementSize));
		}
		return list;
	}

}
