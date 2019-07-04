package altaite.collection.buffer;

import altaite.collection.buffer.Flag;
import java.io.File;
import org.junit.Test;

public class FlagTest {

	@Test
	public void testIsOn() {
		Flag flag = new Flag(new File("test.flag"));
		assert flag.isOn() == false;
		for (int i = 0; i < 100; i++) {
			flag.on();
			assert flag.isOn() == true;
			flag.off();
			assert flag.isOn() == false;
		}
		flag.close();
	}

}
