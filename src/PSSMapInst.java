
import java.util.List;

/**
 * A {PSSMapInst} is an instance of {@link PSSMapModel}.
 */
public class PSSMapInst extends PSSInst {

    /** The key type */
    PSSModel m_key_type;

    /** The value type */
    PSSModel m_val_type;

    PSSMapVal m_map = new PSSMapVal();

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
    public PSSMapInst(String id, PSSModel key_type, PSSModel val_type) {
        super(id, "map<" + key_type.m_id + "," + val_type.m_id + ">", false);
        PSSMessage.Debug("[" + this.getClass().getName() + "] Creating MapInst: m_key_type = " + key_type.getId()
                + ", value = " + val_type.getId());
        m_key_type = key_type;
        m_val_type = val_type;

        /* register native methods */
        addNativeMethod(m_method_size);
        addNativeMethod(m_method_clear);
        addNativeMethod(m_method_delete);
        addNativeMethod(m_method_insert);
        addNativeMethod(m_method_keys);
        addNativeMethod(m_method_values);
    }

    private PSSInst getKeyInst(PSSVal k) {
        PSSInst ki = m_key_type.declInst(m_id + ":key=" + k.getText(), m_rand);
        ki.assign(k);
        return ki;
    }

    private PSSInst getValueInst(PSSVal v) {
        PSSInst vi = m_val_type.declInst(m_rand + ":val=" + v.getText(), m_rand);
        vi.assign(v);
        return vi;
    }

    @Override
    public void assign(PSSVal val) {
        if (!(val instanceof PSSMapVal))
            PSSMessage.Fatal("A map can only be assigned a map_literal.");
        PSSMapVal m = (PSSMapVal) val;

        PSSMessage.Debug("[" + this.getClass().getName() + "] Assigning value " + val.getText() + " to " + this.m_id);

        /* TODO: check the type of PSSVal */
        m_map.clear();
        for (PSSVal k : m.keys().getValList()) {
            PSSVal v = m.get(k);
            insert(k, v);
        }
    }

    /**
     * Returns the value associated with the specified key.
     *
     * @param kstr a key
     * @return the value associated with {@code kstr}
     */
    public PSSInst get(String kstr) {
        /* TODO: can we detect the type of the key? */
        PSSInst res = null;
        for (PSSVal k : m_map.keys().getValList()) {
            if (k.getText().equals(kstr)) {
                res = getValueInst(m_map.get(k));
                break;
            }
        }
        return res;
    }

    /**
     * Returns the size of this map instance.
     *
     * @return the size of this map instance
     */
    public PSSIntInst size() {
        PSSIntInst res = new PSSIntInst(m_id + ".size()", false, 32, false);
        res.assign(new PSSIntVal(m_map.size()));
        return res;
    }

    /**
     * Clears this map.
     */
    public void clear() {
        m_map.clear();
    }

    /**
     * Removes the value associated with the specified key and returns the value.
     *
     * @param key a key
     * @return the value associated with the key before being removed
     */
    public PSSInst delete(PSSVal key) {
        PSSMessage.Debug("deleting: " + key.getText());
        PSSVal v = m_map.delete(key);
        PSSMessage.Debug("deleted: " + key.getText());
        return v == null ? null : getValueInst(v);
    }

    /**
     * Inserts a key-value pair to this map.
     *
     * @param k a key
     * @param v a value
     */
    public void insert(PSSVal k, PSSVal v) {
        m_map.insert(k, v);
    }

    /**
     * Returns the key set.
     *
     * @return the key set
     */
    public PSSInst keys() {
        /* TODO: implement this */
        PSSMessage.Fatal("[" + getClass().getName() + "] To be implemented when set implementation is done");
        return null;
    }

    /**
     * Returns the value list
     *
     * @return the value list
     */
    public PSSListInst values() {
        PSSListInst res = new PSSListModel(m_val_type).declInst(m_id + ".values()", m_rand);
        for (PSSVal v : m_map.values())
            ; /* TODO: add to res */
        PSSMessage.Fatal("[" + getClass().getName() + "] To be implemented when set implementation is done");
        return null;
    }

    @Override
    public PSSMapVal toVal() {
        PSSMapVal res = new PSSMapVal();
        for (PSSVal k : m_map.keys().getValList()) {
            res.insert(k, m_map.get(k));
        }
        return res;
    }

    public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The list collection type is not randomizable.
    }

}
