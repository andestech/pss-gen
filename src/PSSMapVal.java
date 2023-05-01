// Copyright (C) 2021, Andes Technology Corp. Confidential Proprietary
import java.util.*;

/**
 * A {@code PSSMapVal} is an evaluation of a {@link PSSMapExpression}.
 * 
 * TODO: keep the types of the keys and values (make this class generic?)
 */
public class PSSMapVal extends PSSVal {

	private Map<PSSVal, PSSVal> map = new HashMap<PSSVal, PSSVal>();

	public PSSMapVal () {
	}

	/* Map functions in PTSS v20 Section 8.8.4.2 */

	/**
	 * Returns the number of elements in the map.
	 * @return the number of elements in the map
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Removes all elements from the map.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Removes the element associated with the specified key from the map and returns the element value.
	 * @param key
	 * @return the element associated with the specified key
	 */
	public PSSVal delete(PSSVal key) {
		if (map.containsKey(key))
			return map.remove(key);
		else {
			PSSMessage.Error("", "The key '" + key.getText() + "' does not exist.");
			return null;
		}
	}

	/**
	 * Adds the specified key/value pair to the map.
	 * @param key a key
	 * @param value a value
	 */
	public void insert(PSSVal key, PSSVal value) {
		/* TODO: verify the types of the inputs */
		map.put(key, value);
	}

	/**
	 * Returns a set containing the map keys.
	 * @return a set containing the map keys
	 */
	public PSSSetVal keys() {
		PSSSetVal keys = new PSSSetVal();
		for (PSSVal k : map.keySet())
			keys.addVal(k);
		return keys;
	}

	/**
	 * Returns a list containing the map element values.
	 * @return a list containing the map element values
	 */
	public List<PSSVal> values() {
		/* TODO: use class like PSSListVal */
		List<PSSVal> vals = new ArrayList<PSSVal>();
		for (PSSVal v : map.values())
			vals.add(v);
		return vals;
	}

	/* Map operators */

	/**
	 * Returns the value associated with a specified key.
	 * @param key a key
	 * @return the value associated with a specified key
	 */
	public PSSVal get(PSSVal key) {
		if (map.containsKey(key))
			return map.get(key);
		else {
			PSSMessage.Error("", "The key '" + key.getText() + "' does not exist.");
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PSSMapVal) {
			PSSMapVal m = (PSSMapVal) obj;
			return m.map.equals(this.map);
		} else
			return false;
	}

	/**
	 * Returns {@code true} if this map is empty.
	 * @return {@dode true} if this map is empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns the backend map.
	 * @return the backend map
	 */
	public Map<PSSVal, PSSVal> getMap() {
		return map;
	}

	@Override
	public String getText() {
		List<String> strs = new ArrayList<String>();

		for (PSSVal k : map.keySet()) {
			PSSVal v = map.get(k);
			strs.add(k.getText() + ": " + v.getText());
		}
		return "{ " + String.join(", ", strs) + " }";
	}

}
