
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * A {@code PSSMapVal} is an evaluation of a {@link PSSMapExpression}.
 *
 * TODO: keep the types of the keys and values (make this class generic?)
 */
public class PSSMapVal extends PSSVal {

	private Map<PSSVal, PSSVal> m_map = new HashMap<PSSVal, PSSVal>();

	public PSSMapVal() {
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
		/* TODO: verify the types of the inputs */
		m_map.put(key, value);
	}

	/**
	 * Returns a set containing the map keys.
	 *
	 * @return a set containing the map keys
	 */
	public PSSSetVal keys() {
		PSSSetVal keys = new PSSSetVal();
		for (PSSVal k : m_map.keySet())
			keys.addVal(k);
		return keys;
	}

	/**
	 * Returns a list containing the map element values.
	 *
	 * @return a list containing the map element values
	 */
	public List<PSSVal> values() {
		/* TODO: use class like PSSListVal */
		List<PSSVal> vals = new ArrayList<PSSVal>();
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
	public String getText() {
		List<String> strs = new ArrayList<String>();

		for (PSSVal k : m_map.keySet()) {
			PSSVal v = m_map.get(k);
			strs.add(k.getText() + ": " + v.getText());
		}
		return "{ " + String.join(", ", strs) + " }";
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
