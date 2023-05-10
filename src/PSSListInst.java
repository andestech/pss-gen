import java.util.List;
import java.util.ArrayList;

public class PSSListInst extends PSSInst {
    PSSModel m_type_model;
    List<PSSInst> m_list = new ArrayList<PSSInst>();

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
            return delete(args.get(0));
        }
    };

    NativeMethod m_method_insert = new NativeMethod("insert", 2) {
        protected PSSVal doEval(List<PSSVal> args) {
            insert(args.get(0), args.get(1));
            return null;
        }
    };

    NativeMethod m_method_pop_front = new NativeMethod("pop_front", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return pop_front();
        }
    };

    NativeMethod m_method_push_front = new NativeMethod("push_front", 1) {
        protected PSSVal doEval(List<PSSVal> args) {
            push_front(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_pop_back = new NativeMethod("pop_back", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return pop_back();
        }
    };

    NativeMethod m_method_push_back = new NativeMethod("push_back", 1) {
        protected PSSVal doEval(List<PSSVal> args) {
            push_back(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_to_set = new NativeMethod("to_set", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return to_set();
        }
    };

    public PSSListInst(String id, PSSModel type_model) {
        super(id, "list<" + type_model.m_id + ">", false);
        m_type_model = type_model;

        addNativeMethod(m_method_size);
        addNativeMethod(m_method_clear);
        addNativeMethod(m_method_delete);
        addNativeMethod(m_method_insert);
        addNativeMethod(m_method_pop_front);
        addNativeMethod(m_method_push_front);
        addNativeMethod(m_method_pop_back);
        addNativeMethod(m_method_push_back);
        addNativeMethod(m_method_to_set);
    }

    private PSSInst getNewElementInst(PSSVal element) {
        PSSInst elemInst = m_type_model.declInst(false);
        elemInst.assign(element);
        return elemInst;
    }

    private int getIndexInteger(PSSVal index) {
        if (!(index instanceof PSSIntVal))
            PSSMessage.Error("ListInst", "An index should be an integer value.");
        return index.toInt();
    }

	@Override
    public void assign(PSSVal val) {
        if (!(val instanceof PSSListVal))
            PSSMessage.Fatal("The list type should be assigned as a list_literal.");
        PSSListVal listval = (PSSListVal) val;

        m_list.clear();
        for (PSSVal elem: listval.getValList())
            m_list.add(getNewElementInst(elem));
    }

	@Override
    public PSSListVal toVal() {
        PSSListVal collectedVal = new PSSListVal(m_type_model);
        for (PSSInst elem: m_list)
            collectedVal.add(elem.toVal());
        return collectedVal;
    }

    @Override
    public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The list collection type is not randomizable.
    }

    @Override
    public PSSInst indexOf(PSSVal index) {
        int idx = getIndexInteger(index);
        if (idx >= m_list.size())
            PSSMessage.Error("ListInst", "'" + getHierarchyId() + "'" +
                    "index '" + idx + "' is out of range");
        return m_list.get(idx);
    }

    public PSSIntVal size() {
        return new PSSIntVal(m_list.size());
    }

    public void clear() {
        m_list.clear();
    }

    public PSSVal delete(PSSVal index) {
        int idx = getIndexInteger(index);
        PSSInst deletedInst = m_list.remove(idx);
        if (deletedInst == null)
            PSSMessage.Error("ListInst", "Accessing a non-existing index " +
                    index.getText() + " of set " + m_id);
        return deletedInst.toVal();
    }

    public void insert(PSSVal index, PSSVal element) {
        if (!m_type_model.isCompatible(element.getTypeModel()))
            PSSMessage.Error("ListInst", "The element type " +
                    element.getTypeModel().getText() + " of " +
                    element.getText() + " is incompatible with set " + m_id);

        int idx = getIndexInteger(index);
        int list_size = m_list.size();
        if (idx == list_size)
            m_list.add(getNewElementInst(element));
        else if (idx < list_size)
            m_list.add(idx, getNewElementInst(element));
        else
            PSSMessage.Error("ListInst", "Set.Insert() is illegal because " +
                    "the index " + index.getText() + " is greater than the " +
                    "size of " + m_id);
    }

    public PSSVal pop_front() {
        return delete(PSSIntInst.DEFAULT_VALUE);
    }

    public void push_front(PSSVal element) {
        insert(PSSIntInst.DEFAULT_VALUE, element);
    }

    public PSSVal pop_back() {
        PSSIntVal last = new PSSIntVal(m_list.size() - 1);
        return delete(last);
    }

    public void push_back(PSSVal element) {
        PSSIntVal end = new PSSIntVal(m_list.size());
        insert(end, element);
    }

    public PSSVal to_set() {
        PSSSetVal collectedVal = new PSSSetVal(m_type_model);
        for (PSSInst elem: m_list)
            collectedVal.add(elem.toVal());
        return collectedVal;
    }

};

