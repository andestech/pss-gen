import java.util.*;

public class PSSSetInst extends PSSInst {

    PSSModel m_type_model;
    PSSSetVal m_set = new PSSSetVal();

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
            return to_list();
        }
    };

    public PSSSetInst(String id, PSSModel type_model) {
        super(id, "set<"+type_model.m_id+">", false);
        m_type_model = type_model;

        /* register native methods */
        addNativeMethod(m_method_size);
        addNativeMethod(m_method_clear);
        addNativeMethod(m_method_delete);
        addNativeMethod(m_method_insert);
        addNativeMethod(m_method_to_list);
    }

    private PSSInst getElemInst(PSSVal e) {
        PSSInst elem = m_type_model.declInst(m_id + ":elem=" + e.getText(), m_rand);
        elem.assign(e);
        return elem;
    }

    @Override
    public void assign(PSSVal val) {
        if (!(val instanceof PSSArrayVal))
            PSSMessage.Fatal("The set type should be assigned as a value_list_literal.");
        PSSArrayVal s = (PSSArrayVal) val;

        m_set.clear();
        for (PSSVal e: s.getValList())
            insert(e);
    }

    @Override
    public PSSInst indexOf(PSSVal idx) {
        PSSMessage.Fatal("According to PSS Standard V2.0 - " +
                "section 8.8.5.1, Set has no index operator");
        return null;
    }

    public PSSIntInst size() {
        PSSIntInst res = new PSSIntInst(m_id + ".size()", false, PSSIntModel.DEFAULT_INT_SIZE, false);
        res.assign(new PSSIntVal(m_set.size()));
        return res;
    }

    public void clear() {
        m_set.clear();
    }

    public void delete(PSSVal elem) {
        m_set.delete(elem);
    }

    public void insert(PSSVal e) {
        m_set.insert(e);
    }

    public PSSInst to_list() {
        PSSMessage.Fatal("[" + getClass().getName() + "] To be implement");
        return null;
    }

    @Override
    public PSSSetVal toVal() {
        PSSSetVal collectedVal = new PSSSetVal();
        for (PSSVal elem: m_set.getValList())
            collectedVal.insert(elem);
        return collectedVal;
    }

    @Override
    public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The set collection type is not randomizable.
    }

}
