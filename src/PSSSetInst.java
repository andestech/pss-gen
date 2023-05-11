import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class PSSSetInst extends PSSInst {

    PSSModel m_type_model;

    Set<PSSVal> m_set = new HashSet<PSSVal>();

    NativeMethod m_method_size = new NativeMethod("size", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return size();
        }
    };

    NativeMethod m_method_clear = new NativeMethod("clear", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            clear();
            return null;
        }
    };

    NativeMethod m_method_delete = new NativeMethod("delete", 1) {
        protected PSSVal doEval(List<PSSVal> args) {
            delete(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_insert = new NativeMethod("insert", 1) {
        protected PSSVal doEval(List<PSSVal> args) {
            insert(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_to_list = new NativeMethod("to_list", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return to_list();
        }
    };

    public PSSSetInst(String id, PSSModel type_model) {
        super(id, "set<" + type_model.m_id + ">", false);
        m_type_model = type_model;

        /* register native methods */
        addNativeMethod(m_method_size);
        addNativeMethod(m_method_clear);
        addNativeMethod(m_method_delete);
        addNativeMethod(m_method_insert);
        addNativeMethod(m_method_to_list);
    }

    @Override
    public void assign(PSSVal val) {
        m_set.clear();

        if (val instanceof PSSListVal) {
            PSSListVal s = (PSSListVal) val;
            for (PSSVal e: s.getValList())
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

    public PSSIntVal size() {
        return new PSSIntVal(m_set.size());
    }

    public void clear() {
        m_set.clear();
    }

    public void delete(PSSVal elem) {
        if (!m_set.remove(elem))
            PSSMessage.Error("SetInst", "Deleting \"" + elem.getText() + "\" " +
                    "is illegal because it does not exist in \"" + m_id + "\".");
    }

    public void insert(PSSVal e) {
        if (m_type_model.isCompatible(e.getTypeModel()))
            m_set.add(e);
        else
            PSSMessage.Error("SetInst", "The data type of the inserted " +
                    "element shall be the same as the set element.");
    }

    public PSSVal to_list() {
        PSSSetVal set = toVal();
        PSSListVal list = new PSSListVal(m_type_model);
        for (PSSVal e: set.getSet())
            list.add(e);
        return list;
    }

    @Override
    public PSSSetVal toVal() {
        PSSSetVal collectedVal = new PSSSetVal(m_type_model);
        for (PSSVal elem: m_set)
            collectedVal.add(elem);
        return collectedVal;
    }

    @Override
    public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The set collection type is not randomizable.
    }

}
