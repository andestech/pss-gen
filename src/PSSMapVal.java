
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A {@code PSSMapVal} is an evaluation of a {@link PSSMapExpression}.
 *
 */
public class PSSMapVal extends PSSVal implements PSSICollection {

	private Map<PSSVal, PSSVal> m_map = new HashMap<PSSVal, PSSVal>();

	private class MapIterator implements PSSIIterator {

		private Iterator<PSSVal> m_keys;

		private Map<PSSVal, PSSVal> m_map;

		public MapIterator(Map<PSSVal, PSSVal> m) {
			m_map = m;
			m_keys = m_map.keySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return m_keys.hasNext();
		}

		@Override
		public Entry<PSSVal, PSSVal> next() {
			PSSVal k = m_keys.next();
			PSSVal v = m_map.get(k);
			return new AbstractMap.SimpleEntry<PSSVal, PSSVal>(k, v);
		}

	}

	public PSSMapVal(PSSModel type) {
		super(type);
	}

	private PSSMapModel getMapType() {
		PSSMapModel res = null;
		PSSModel m = getTypeModel();
		if (m instanceof PSSMapModel) {
			res = (PSSMapModel) m;
		}
		return res;
	}

	private PSSModel getKeyType() {
		PSSMapModel m = getMapType();
		return m == null ? null : m.getKeyType();
	}

	private PSSModel getValueType() {
		PSSMapModel m = getMapType();
		return m == null ? null : m.getValueType();
	}

	private boolean isKeyTypeCompatible(PSSModel model) {
		PSSModel m = getKeyType();
		return m == null || model == null || m.isCompatible(model);
	}

	private boolean isValueTypeCompatible(PSSModel model) {
		PSSModel m = getValueType();
		return m == null || model == null || m.isCompatible(model);
	}

	/* Map functions in PSS v20 Section 8.8.4.2 */

	/**
	 * Returns the number of elements in the map.
	 *
	 * @return the number of elements in the map
	 */
	public int size() {
		return m_map.size();
	}

	/**
	 * Removes all elements from the map.
	 */
	public void clear() {
		m_map.clear();
	}

	/**
	 * Removes the element associated with the specified key from the map and
	 * returns the element value.
	 *
	 * @param key
	 * @return the element associated with the specified key
	 */
	public PSSVal delete(PSSVal key) {
		if (!isKeyTypeCompatible(key.getTypeModel()))
			PSSMessage.Error("", "The key type " + key.getTypeModel().getText() + " of " + key.getText()
					+ " is incompatible with map " + getText());

		if (m_map.containsKey(key))
			return m_map.remove(key);
		else {
			PSSMessage.Error("", "The key '" + key.getText() + "' does not exist.");
			return null;
		}
	}

	/**
	 * Adds the specified key/value pair to the map.
	 *
	 * @param key   a key
	 * @param value a value
	 */
	public void insert(PSSVal key, PSSVal value) {
		if (getTypeModel() == null)
			setTypeModel(new PSSMapModel(key.getTypeModel(), value.getTypeModel()));

		if (!isKeyTypeCompatible(key.getTypeModel()))
			PSSMessage.Error("", "The key type " + key.getTypeModel().getText() + " of " + key.getText()
					+ " is incompatible with map " + getText());
		if (!isValueTypeCompatible(value.getTypeModel()))
			PSSMessage.Error("", "The value type " + value.getTypeModel().getText() + " of " + value.getText()
					+ " is incompatible with map " + getText());
		m_map.put(key, value);
	}

	/**
	 * Returns a set containing the map keys.
	 *
	 * @return a set containing the map keys
	 */
	public PSSListVal keys() {
		PSSListVal keys = new PSSListVal(getKeyType());
		for (PSSVal k : m_map.keySet())
			keys.add(k);
		return keys;
	}

	/**
	 * Returns a list containing the map element values.
	 *
	 * @return a list containing the map element values
	 */
	public PSSListVal values() {
		PSSListVal vals = new PSSListVal(getValueType());
		for (PSSVal v : m_map.values())
			vals.add(v);
		return vals;
	}

	/* Map operators */

	/**
	 * Returns the value associated with a specified key.
	 *
	 * @param key a key
	 * @return the value associated with a specified key
	 */
	public PSSVal get(PSSVal key) {
		if (!isKeyTypeCompatible(key.getTypeModel()))
			PSSMessage.Error("", "The key type " + key.getTypeModel().getText() + " of " + key.getText()
					+ " is incompatible with map " + getText());

		if (m_map.containsKey(key))
			return m_map.get(key);
		else {
			PSSMessage.Error("", "The key '" + key.getText() + "' does not exist.");
			return null;
		}
	}

	/**
	 * Returns {@code true} if this map is empty.
	 *
	 * @return {@dode true} if this map is empty
	 */
	public boolean isEmpty() {
		return m_map.isEmpty();
	}

	/**
	 * Returns the backend map.
	 *
	 * @return the backend map
	 */
	public Map<PSSVal, PSSVal> getMap() {
		return m_map;
	}

	@Override
	public PSSVal indexOf(PSSVal index) {
		return get(index);
	}

	@Override
	public PSSBoolVal Equal(PSSVal v) {
		if (v instanceof PSSMapVal) {
			PSSMapVal m = (PSSMapVal) v;
			return new PSSBoolVal(m_map.equals(m.m_map));
		}
		return new PSSBoolVal(false);
	}

	@Override
	public PSSBoolVal NotEqual(PSSVal v) {
		return Equal(v).LogicalNot();
	}

	@Override
	public String getText() {
		List<String> strs = new ArrayList<String>();

		for (PSSVal k : m_map.keySet()) {
			PSSVal v = m_map.get(k);
			strs.add(k.getText() + ": " + v.getText());
		}
		return "{ " + String.join(", ", strs) + " }";
	}

	@Override
	public boolean isIndexable() {
		return true;
	}

	@Override
	public PSSIIterator iterator() {
		return new MapIterator(m_map);
	}

	@Override
	public int compareTo(PSSVal o) {
		if (o instanceof PSSMapVal) {
			PSSMapVal m = (PSSMapVal) o;
			if (equals(m))
				return 0;
			List<PSSVal> keys = new ArrayList<PSSVal>();
			keys.addAll(m_map.keySet());
			keys.addAll(m.m_map.keySet());
			Collections.sort(keys);
			for (PSSVal k : keys) {
				int c = Boolean.valueOf(m_map.containsKey(k)).compareTo(Boolean.valueOf(m.m_map.containsKey(keys)));
				if (c != 0)
					return c;
				c = m_map.get(k).compareTo(m.m_map.get(k));
				if (c != 0)
					return c;
			}
		}
		return super.compareTo(o);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PSSMapVal) {
			PSSMapVal m = (PSSMapVal) obj;
			return m.m_map.equals(m_map);
		}
		return false;
	}

}
