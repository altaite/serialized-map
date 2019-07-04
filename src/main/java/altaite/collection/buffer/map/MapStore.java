package altaite.collection.buffer.map;

import altaite.collection.buffer.BigResource;
import altaite.collection.buffer.BigStore;
import java.nio.file.Path;

public class MapStore {

	private BigResource keyResource, valueResource;

	public MapStore(Path dir) {
		this.keyResource = new BigResource(dir.resolve("keys"));
		this.valueResource = new BigResource(dir.resolve("values"));
		fixDifferentLengths();
	}

	private void fixDifferentLengths() {
		BigStore keyStore = new BigStore(keyResource);
		BigStore valueStore = new BigStore(valueResource);
		if (keyStore.size() < valueStore.size()) {
			valueStore.truncate(keyStore.size());
		} else if (valueStore.size() < keyStore.size()) {
			keyStore.truncate(valueStore.size());
		}
		keyStore.close();
		valueStore.close();
	}

	public BigResource getValueResource() {
		return valueResource;
	}

	public BigResource getKeyResource() {
		return keyResource;
	}

	public void clear() {
		keyResource.clear();
		valueResource.clear();
	}
}
