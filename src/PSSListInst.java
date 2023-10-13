import java.util.*;

/**
 * A {@code PSSListInst} is an instance holding a list of elements.
 */
public class PSSListInst extends PSSInst implements PSSIAggregate {

    /** the data type of the list elements */
    PSSModel m_elem_type_model;

    /** the list elements */
    List<PSSInst> m_list = new ArrayList<PSSInst>();

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

    NativeMethod m_method_pop_front = new NativeMethod("pop_front", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return popFront();
        }
    };

    NativeMethod m_method_push_front = new NativeMethod("push_front", 1) {
        protected PSSInst doEval(List<PSSVal> args) {
            pushFront(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_pop_back = new NativeMethod("pop_back", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return popBack();
        }
    };

    NativeMethod m_method_push_back = new NativeMethod("push_back", 1) {
        protected PSSInst doEval(List<PSSVal> args) {
            pushBack(args.get(0));
            return null;
        }
    };

    NativeMethod m_method_to_set = new NativeMethod("to_set", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return toSet();
        }
    };

    NativeMethod m_method_shuffle = new NativeMethod("shuffle", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            shuffle();
            return null;
        }
    };

    /**
     * Constructs this list.
     *
     * @param id         the name of this list
     * @param type_model the data type of this list elements
     */
    public PSSListInst(String id, PSSListModel type_model) {
        super(id, "list<" + type_model.m_id + ">", type_model, false);
        m_elem_type_model = type_model.getElementTypeModel();

        addNativeMethod(m_method_size);
        addNativeMethod(m_method_clear);
        addNativeMethod(m_method_delete);
        addNativeMethod(m_method_insert);
        addNativeMethod(m_method_pop_front);
        addNativeMethod(m_method_push_front);
        addNativeMethod(m_method_pop_back);
        addNativeMethod(m_method_push_back);
        addNativeMethod(m_method_to_set);
        addNativeMethod(m_method_shuffle);
    }

    /**
     * Returns an empty list with elements of a specified type.
     *
     * @param id the name of this list
     * @param m  the data type of list elements
     * @return an empty list
     */
    public static PSSListInst ofElementDataType(String id, PSSModel m) {
        return new PSSListInst(id, new PSSListModel(m));
    }

    @Override
    public PSSListModel getTypeModel() {
        return (PSSListModel) super.getTypeModel();
    }

    /**
     * Returns the type of list elements.
     *
     * @return the type of list elements
     */
    public PSSModel getElementTypeModel() {
        return getTypeModel().getElementTypeModel();
    }

    private PSSInst getNewElementInst(PSSVal element) {
        PSSInst elemInst = m_elem_type_model.declInst("", false);
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
        // PSS 2.0 Section 22.3.3
        // Parameters of aggregate data types are passed as a handle.
        // Assigning a reference to an aggregate variable is possible in a function.
        if (val instanceof PSSRefVal) {
            PSSRefVal r = (PSSRefVal) val;
            val = r.getInst().toVal();
        }

        if (!(val instanceof PSSListVal))
            PSSMessage.Fatal("The list type should be assigned as a list_literal.");

        PSSListVal listval = (PSSListVal) val;

        m_list.clear();
        for (PSSVal elem : listval.getValList())
            m_list.add(getNewElementInst(elem));
    }

    @Override
    public PSSListVal toVal() {
        PSSListVal collectedVal = new PSSListVal(getTypeModel());
        for (PSSInst elem : m_list)
            collectedVal.add(elem.toVal());
        return collectedVal;
    }

    /**
     * @todo Randomize order list
     * 
     * @note Support after PSSv2.1
     */
    @Override
    public void randomize() {
    }

    @Override
    public PSSInst indexOf(PSSVal index) {
        int idx = getIndexInteger(index);
        PSSInst res = null;
        try {
            res = m_list.get(idx);
        } catch (IndexOutOfBoundsException e) {
            PSSMessage.Error("ListInst", "'" + getHierarchyId() + "'" +
                    "index '" + idx + "' is out of range.");
        }
        return res;
    }

    /**
     * Returns the size of this list.
     *
     * @return the size of this list
     */
    public PSSIntInst size() {
        PSSIntInst res = new PSSIntInst("", false, PSSIntModel.DEFAULT_INT_SIZE, false);
        res.setReadOnly(true);
        res.assign(new PSSIntVal(m_list.size()));
        return res;
    }

    /**
     * Removes all elements from this list.
     */
    public void clear() {
        m_list.clear();
    }

    /**
     * Removes an element at a specified index from this list.
     *
     * @param index an index
     * @return the removed element
     */
    public PSSInst delete(int index) {
        PSSInst res = null;
        try {
            res = m_list.remove(index);
        } catch (IndexOutOfBoundsException e) {
            PSSMessage.Error("ListInst", "'" + getHierarchyId() + "'" +
                    "index '" + index + "' is out of range.");
        }
        return res;
    }

    /**
     * Removes an element at a specified index from this list.
     *
     * @param index an index
     * @return the removed element
     */
    public PSSInst delete(PSSVal index) {
        return delete(getIndexInteger(index));
    }

    /**
     * Inserts an element at a specified index.
     *
     * @param index   an index
     * @param element an element to be inserted
     */
    public void insert(int index, PSSInst element) {
        if (!m_elem_type_model.isCompatible(element.getTypeModel()))
            PSSMessage.Error("ListInst", "The element type " +
                    element.getTypeModel().getText() + " of " +
                    element.toVal().getText() + " is incompatible with list " + m_id);

        try {
            m_list.add(index, element);
        } catch (IndexOutOfBoundsException e) {
            PSSMessage.Error("ListInst", "Set.Insert() is illegal because " +
                    "the index " + index + " is greater than the " +
                    "size of " + m_id);
        }
    }

    /**
     * Inserts an element at a specified index.
     *
     * @param index   an index
     * @param element an element to be inserted
     */
    public void insert(int index, PSSVal element) {
        if (!m_elem_type_model.isCompatible(element.getTypeModel()))
            PSSMessage.Error("ListInst", "The element type " +
                    element.getTypeModel().getText() + " of " +
                    element.getText() + " is incompatible with list " + m_id);

        try {
            m_list.add(index, getNewElementInst(element));
        } catch (IndexOutOfBoundsException e) {
            PSSMessage.Error("ListInst", "List.insert() is illegal because " +
                    "the index " + index + " is greater than the " +
                    "size of " + m_id);
        }
    }

    /**
     * Inserts an element at a specified index.
     *
     * @param index   an index
     * @param element an element to be inserted
     */
    public void insert(PSSVal index, PSSInst element) {
        insert(getIndexInteger(index), element);
    }

    /**
     * Inserts an element at a specified index.
     *
     * @param index   an index
     * @param element an element to be inserted
     */
    public void insert(PSSVal index, PSSVal element) {
        insert(getIndexInteger(index), element);
    }

    /**
     * Removes the first element from this list.
     *
     * @return the first element
     */
    public PSSInst popFront() {
        return m_list.remove(0);
    }

    /**
     * Inserts an element to the front of this list.
     *
     * @param element an element to be inserted
     */
    public void pushFront(PSSInst element) {
        insert(0, element);
    }

    /**
     * Inserts an element to the front of this list.
     *
     * @param element an element to be inserted
     */
    public void pushFront(PSSVal element) {
        insert(0, element);
    }

    /**
     * Removes the last element from this list.
     *
     * @return the last element, or {@code null} if this list is empty
     */
    public PSSInst popBack() {
        if (m_list.isEmpty())
            return null;

        return m_list.remove(m_list.size() - 1);
    }

    /**
     * Inserts an element to the back of this list.
     *
     * @param element an element to be inserted
     */
    public void pushBack(PSSInst element) {
        insert(m_list.size(), element);
    }

    /**
     * Inserts an element to the back of this list.
     *
     * @param element an element to be inserted
     */
    public void pushBack(PSSVal element) {
        insert(m_list.size(), element);
    }

    /**
     * Converts this list to a set.
     *
     * @return the set view of this list
     */
    public PSSSetInst toSet() {
        PSSSetInst collectedVal = PSSSetInst.ofElementDataType("", m_elem_type_model);
        for (PSSInst elem : m_list) {
            if (elem instanceof PSSIScalarInst)
                collectedVal.insert(elem.toVal());
            else
                collectedVal.insert(elem);
        }
        return collectedVal;
    }

    /**
     * Reorders all elements in the list.
     * 
     * @note Support after PSSv2.1
     */
    public void shuffle() {
        Random rnd = PSSRandom.getRandomObj();
        Collections.shuffle(m_list, rnd);
    }

};
