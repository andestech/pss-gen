
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * A {PSSMapInst} is an instance of {@link PSSMapModel}.
 */
public class PSSMapInst extends PSSInst implements PSSIAggregate {

    /** A map for keys */
    Map<PSSVal, PSSInst> m_keys = new HashMap<PSSVal, PSSInst>();

    /** A map for values */
    Map<PSSVal, PSSInst> m_values = new HashMap<PSSVal, PSSInst>();

    NativeMethod m_method_size = new NativeMethod("size", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return size();
        }
    };

    NativeMethod m_method_clear = new NativeMethod("clear", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            clear();
            return null;
        }
    };

    NativeMethod m_method_delete = new NativeMethod("delete", 1) {
        protected PSSInst doEval(List<PSSVal> args) {
            return delete(args.get(0));
        }
    };

    NativeMethod m_method_insert = new NativeMethod("insert", 2) {
        protected PSSInst doEval(List<PSSVal> args) {
            insert(args.get(0), args.get(1));
            return null;
        }
    };

    NativeMethod m_method_keys = new NativeMethod("keys", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return keys();
        }
    };

    NativeMethod m_method_values = new NativeMethod("values", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return values();
        }
    };

    /**
     * Constructs an instance of map.
     *
     * @param id       the name of the map
     * @param key_type the key type
     * @param val_type the value type
     */
    public PSSMapInst(String id, PSSMapModel map_type) {
        super(id, "map<" + map_type.getKeyType().m_id + ", " + map_type.getValueType().m_id + ">", map_type, false);

        /* register native methods */
        addNativeMethod(m_method_size);
        addNativeMethod(m_method_clear);
        addNativeMethod(m_method_delete);
        addNativeMethod(m_method_insert);
        addNativeMethod(m_method_keys);
        addNativeMethod(m_method_values);
    }

    @Override
    public PSSMapModel getTypeModel() {
        return (PSSMapModel) super.getTypeModel();
    }

    /**
     * Returns the key type of this map.
     *
     * @return the key type of this map
     */
    public PSSModel getKeyTypeModel() {
        PSSMapModel m = getTypeModel();
        return m == null ? null : m.getKeyType();
    }

    /**
     * Returns the value type of this map.
     *
     * @return the value type of this map
     */
    public PSSModel getValueTypeModel() {
        PSSMapModel m = getTypeModel();
        return m == null ? null : m.getValueType();
    }

    @Override
    public void assign(PSSVal val) {
        m_keys.clear();
        m_values.clear();

        // PSS 2.0 Section 22.3.3
        // Parameters of aggregate data types are passed as a handle.
        // Assigning a reference to an aggregate variable is possible in a function.
        if (val instanceof PSSRefVal) {
            PSSRefVal r = (PSSRefVal) val;
            val = r.getInst().toVal();
        }

        if (val instanceof PSSListVal) {
            PSSListVal v = (PSSListVal) val;
            if (!v.isEmpty()) {
                PSSMessage.Error("PSSMapInst", "Cannot assign a map a non-empty list value.");
            }
        } else if (val instanceof PSSMapVal) {
            PSSMapVal m = (PSSMapVal) val;
            for (PSSVal k : m.keys().getValList()) {
                PSSVal v = m.get(k);
                insert(k, v);
            }
        } else
            PSSMessage.Error("PSSMapInst", "A map can only be assigned a map_literal.");
    }

    @Override
    public PSSInst indexOf(PSSVal key) {
        PSSMapModel map_type = getTypeModel();

        if (!map_type.getKeyType().isCompatible(key.getTypeModel()))
            PSSMessage.Error("", "The key type " + key.getTypeModel().getText() + " of " + key.getText()
                    + " is incompatible with map " + m_id);

        PSSInst vi = m_values.get(key);
        if (vi == null) {
            PSSInst ki = map_type.getKeyType().declInst("", false);
            ki.assign(key);
            vi = map_type.getValueType().declInst("", false);
            m_keys.put(key, ki);
            m_values.put(key, vi);
        }

        return vi;
    }

    /**
     * Returns the size of this map instance.
     *
     * @return the size of this map instance
     */
    public PSSIntInst size() {
        PSSVal sv = new PSSIntVal(m_keys.size());
        PSSIntInst si = new PSSIntInst("", false, PSSIntModel.DEFAULT_INT_SIZE, false);
        si.setReadOnly(true);
        si.assign(sv);
        return si;
    }

    /**
     * Clears this map.
     */
    public void clear() {
        m_keys.clear();
        m_values.clear();
    }

    /**
     * Removes the value associated with the specified key and returns the value.
     *
     * @param key a key
     * @return the value associated with the key before being removed
     */
    public PSSInst delete(PSSVal key) {
        PSSMapModel map_type = getTypeModel();

        if (!map_type.getKeyType().isCompatible(key.getTypeModel()))
            PSSMessage.Error("", "The key type " + key.getTypeModel().getText() + " of " + key.getText()
                    + " is incompatible with map " + m_id);

        m_keys.remove(key);
        PSSInst vi = m_values.remove(key);
        if (vi == null)
            PSSMessage.Fatal("Accessing a non-existing key " + key.getText() + " of map " + m_id);
        return vi;
    }

    /**
     * Inserts a key-value pair to this map.
     *
     * @param k a key
     * @param v a value
     */
    public void insert(PSSVal k, PSSVal v) {
        PSSMapModel map_type = getTypeModel();

        if (!map_type.getKeyType().isCompatible(k.getTypeModel()))
            PSSMessage.Error("", "The key type " + k.getTypeModel().getText() + " of " + k.getText()
                    + " is incompatible with map " + m_id);
        if (!map_type.getValueType().isCompatible(v.getTypeModel()))
            PSSMessage.Error("", "The value type " + v.getTypeModel().getText() + " of " + v.getText()
                    + " is incompatible with map " + m_id);

        PSSInst vi = m_values.get(k);
        if (vi == null) {
            PSSInst ki = map_type.getKeyType().declInst("", false);
            ki.assign(k);
            vi = map_type.getValueType().declInst("", false);
            m_keys.put(k, ki);
            m_values.put(k, vi);
        }
        vi.assign(v);
    }

    /**
     * Returns the key set.
     *
     * @return the key set
     */
    public PSSSetInst keys() {
        PSSMapModel map_type = getTypeModel();

        PSSSetInst res = PSSSetInst.ofElementDataType("", map_type.getKeyType());
        for (PSSInst k : m_keys.values()) {
            if (k instanceof PSSIScalarInst)
                res.insert(k.toVal());
            else
                res.insert(k);
        }
        return res;
    }

    /**
     * Returns the value list
     *
     * @return the value list
     */
    public PSSListInst values() {
        PSSMapModel map_type = getTypeModel();

        PSSListInst res = PSSListInst.ofElementDataType("", map_type.getValueType());
        for (PSSInst v : m_values.values()) {
            if (v instanceof PSSIScalarInst)
                res.pushBack(v.toVal());
            else
                res.pushBack(v);
        }
        return res;
    }

    @Override
    public PSSMapVal toVal() {
        PSSMapVal res = new PSSMapVal(getTypeModel());
        for (PSSVal k : m_keys.keySet()) {
            res.insert(m_keys.get(k).toVal(), m_values.get(k).toVal());
        }
        return res;
    }

    public void randomize() {
        // PSS V2.0 - 8.1 General
        // The map collection type is not randomizable.
    }

}
