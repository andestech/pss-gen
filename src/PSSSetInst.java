import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code PSSSetInst} is an instance of a set.
 */
public class PSSSetInst extends PSSInst implements PSSIAggregate {

    /** the data type of set elements */
    PSSModel m_elem_type_model;

    /** the set elements */
    Map<PSSVal, PSSInst> m_set = new HashMap<PSSVal, PSSInst>();

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
            delete(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_insert = new NativeMethod("insert", 1) {
        protected PSSInst doEval(List<PSSVal> args) {
            insert(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_to_list = new NativeMethod("to_list", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return toList();
        }
    };

    /**
     * Constructs an empty set.
     *
     * @param id         the name of this set
     * @param type_model the data type of this set
     */
    public PSSSetInst(String id, PSSSetModel type_model) {
        super(id, "set<" + type_model.m_id + ">", type_model, false);
        m_elem_type_model = type_model.getElementTypeModel();

        /* register native methods */
        addNativeMethod(m_method_size);
        addNativeMethod(m_method_clear);
        addNativeMethod(m_method_delete);
        addNativeMethod(m_method_insert);
        addNativeMethod(m_method_to_list);
    }

    /**
     * Returns an empty set with elements of a specified data type.
     *
     * @param id              the name of this set
     * @param elem_type_model the type of set elements
     * @return an empty set
     */
    public static PSSSetInst ofElementDataType(String id, PSSModel elem_type_model) {
        return new PSSSetInst(id, new PSSSetModel(elem_type_model));
    }

    @Override
    public PSSSetModel getTypeModel() {
        return (PSSSetModel) super.getTypeModel();
    }

    /**
     * Returns the type of set elements.
     *
     * @return the type of set elements
     */
    public PSSModel getElementTypeModel() {
        PSSSetModel m = getTypeModel();
        return m == null ? null : m.getElementTypeModel();
    }

    @Override
    public void assign(PSSVal val) {
        m_set.clear();

        // PSS 2.0 Section 22.3.3
        // Parameters of aggregate data types are passed as a handle.
        // Assigning a reference to an aggregate variable is possible in a function.
        if (val instanceof PSSRefVal) {
            PSSRefVal r = (PSSRefVal) val;
            val = r.getInst().toVal();
        }

        if (val instanceof PSSListVal) {
            PSSListVal s = (PSSListVal) val;
            for (PSSVal e : s.getValList())
                insert(e);
        } else if (val instanceof PSSSetVal) {
            PSSSetVal s = (PSSSetVal) val;
            for (PSSVal e : s.getSet()) {
                insert(e);
            }
        } else
            PSSMessage.Fatal("The set type should be assigned as a value_list_literal.");
    }

    @Override
    public PSSInst indexOf(PSSVal idx) {
        PSSMessage.Fatal("According to PSS Standard V2.0 - " +
                "section 8.8.5.1, Set has no index operator.");
        return null;
    }

    /**
     * Returns the size of this set.
     *
     * @return the size of this set
     */
    public PSSInst size() {
        PSSIntInst res = new PSSIntInst("", false, PSSIntModel.DEFAULT_INT_SIZE, false);
        res.setReadOnly(true);
        res.assign(new PSSIntVal(m_set.size()));
        return res;
    }

    /**
     * Removes all set elements.
     */
    public void clear() {
        m_set.clear();
    }

    /**
     * Removes an element from this set.
     *
     * @param elem the element to be removed
     */
    public void delete(PSSVal elem) {
        if (m_set.remove(elem) == null)
            PSSMessage.Error("SetInst", "Deleting \"" + elem.getText() + "\" " +
                    "is illegal because it does not exist in \"" + m_id + "\".");
    }

    /**
     * Inserts an element to this set.
     *
     * @param e an element to be inserted
     */
    public void insert(PSSInst e) {
        if (m_elem_type_model.isCompatible(e.getTypeModel())) {
            m_set.put(e.toVal(), e);
        } else
            PSSMessage.Error("SetInst", "The data type of the inserted " +
                    "element shall be the same as the set element.");
    }

    /**
     * Inserts an element to this set.
     *
     * @param e an element to be inserted
     */
    public void insert(PSSVal e) {
        if (m_elem_type_model.isCompatible(e.getTypeModel())) {
            PSSInst i = m_elem_type_model.declInst("", false);
            i.assign(e);
            m_set.put(i.toVal(), i);
        } else
            PSSMessage.Error("SetInst", "The data type of the inserted " +
                    "element shall be the same as the set element.");
    }

    /**
     * Converts this set to a list.
     *
     * @return a list containing all elements in this set.
     */
    public PSSListInst toList() {
        PSSListInst list = PSSListInst.ofElementDataType("", m_elem_type_model);
        for (PSSInst i : m_set.values()) {
            if (i instanceof PSSIScalarInst)
                list.pushBack(i.toVal());
            else
                list.pushBack(i);
        }
        return list;
    }

    @Override
    public PSSSetVal toVal() {
        PSSSetVal collectedVal = new PSSSetVal(getTypeModel());
        for (PSSInst elem : m_set.values())
            collectedVal.insert(elem.toVal());
        return collectedVal;
    }

    @Override
    public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The set collection type is not randomizable.
    }

}
