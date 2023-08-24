import java.util.List;
import java.util.ArrayList;

/**
 * A {@code PSSArrayInst} is an instance of an array.
 */
public class PSSArrayInst extends PSSInst implements PSSIAggregate {

    /** the data type of array elements */
    PSSModel m_elem_type_model;

    /** the declared dimension of this array */
    int m_dim = 0;

    /** the array elements */
    List<PSSInst> m_array = new ArrayList<PSSInst>();

    NativeMethod m_method_size = new NativeMethod("size", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return size();
        }
    };

    NativeMethod m_method_sum = new NativeMethod("sum", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return sum();
        }
    };

    NativeMethod m_method_to_list = new NativeMethod("to_list", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return toList();
        }
    };

    NativeMethod m_method_to_set = new NativeMethod("to_set", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return toSet();
        }
    };

    /**
     * Constructs this array instance
     *
     * @param id         the name of this array
     * @param type_model the data type of this array
     * @param rand       {@code true} if this array can be randomized
     */
    public PSSArrayInst(String id, PSSArrayModel type_model, boolean rand) {
        super(id, type_model.m_id, type_model, rand);

        m_elem_type_model = type_model.getElementTypeModel();
        m_dim = type_model.getArraySize();

        if (m_dim <= 0)
            PSSMessage.Error("ArrayInst", "'" + id + "' is a zero-length array.");

        for (int i = 0; i < m_dim; i++) {
            PSSInst inst = m_elem_type_model.declInst(id + "[" + i + "]", m_rand);
            m_array.add(inst);
        }

        addNativeMethod(m_method_size);
        addNativeMethod(m_method_sum);
        addNativeMethod(m_method_to_list);
        addNativeMethod(m_method_to_set);
    }

    /**
     * Returns an array with elements of a specified type and a specified array
     * size.
     *
     * @param id         the name of this array
     * @param type_model the data type of array elements
     * @param dim        the array dimension
     * @param rand       {@code true} if this array can be randomized
     */
    public static PSSArrayInst ofElementDataType(String id, PSSModel type_model, int dim, boolean rand) {
        return new PSSArrayInst(id, new PSSArrayModel(type_model, new PSSPrimaryExpression(new PSSIntVal(dim))), rand);
    }

    @Override
    public PSSArrayModel getTypeModel() {
        return (PSSArrayModel) super.getTypeModel();
    }

    /**
     * Returns the data type of array elements.
     *
     * @return the data type of array elements
     */
    public PSSModel getElementTypeModel() {
        return m_elem_type_model;
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
            PSSMessage.Fatal("The array type should be assigned as a PSSArrayVal.");

        PSSListVal arrayVal = (PSSListVal) val;

        if (!m_elem_type_model.isCompatible(arrayVal.getElementTypeModel()))
            PSSMessage.Error("ArrayInst", "The element type " +
                    arrayVal.getTypeModel().getText() + " of " +
                    arrayVal.getText() + " is incompatible with array " + m_id);

        if (arrayVal.size() != m_dim)
            PSSMessage.Error("ArrayInst", "The size of the array is " +
                    "different to the assigned aggregate literal.");

        for (int i = 0; i < m_dim; i++) {
            PSSInst elemInst = m_array.get(i);
            PSSVal elemVal = arrayVal.getValList().get(i);
            elemInst.assign(elemVal);
        }
    }

    @Override
    public void randomize() {
        if (m_rand) {
            for (PSSInst elem : m_array)
                elem.randomize();
        }
    }

    @Override
    public PSSInst indexOf(PSSVal index) {
        if (!(index instanceof PSSIntVal))
            PSSMessage.Error("ArrayInst",
                    "The index of an array must be a PSSIntVal.");

        int idx = index.toInt();
        if (idx < 0 || idx >= m_dim)
            PSSMessage.Error("ArrayInst", "'" + getHierarchyId() + "'" +
                    "index '" + idx + "' is out of range.");
        return m_array.get(idx);
    }

    /**
     * Returns the size of this array.
     *
     * @return the size of this array
     */
    public PSSIntInst size() {
        PSSIntInst res = new PSSIntInst("", false, PSSIntModel.DEFAULT_INT_SIZE, false);
        res.setReadOnly(true);
        res.assign(new PSSIntVal(m_dim));
        return res;
    }

    /**
     * Returns the summation of the elements in this array.
     *
     * @return the summation of the array elements
     */
    public PSSIntInst sum() {
        if (!(m_elem_type_model instanceof PSSIntModel))
            PSSMessage.Error("ArrayInst",
                    "Array.sum() is only used on `int` or `bit` type.");

        int sum = 0;
        for (PSSVal e : toVal().getValList())
            sum += e.toInt();

        PSSIntInst res = new PSSIntInst("", false, PSSIntModel.DEFAULT_INT_SIZE, false);
        res.setReadOnly(true);
        res.assign(new PSSIntVal(sum));

        return res;
    }

    /**
     * Converts this array to a set.
     *
     * @return the set view of this array
     */
    public PSSSetInst toSet() {
        PSSSetInst set = PSSSetInst.ofElementDataType("", m_elem_type_model);
        for (PSSInst e : m_array) {
            if (e instanceof PSSIScalarInst)
                set.insert(e.toVal());
            else
                set.insert(e);
        }
        return set;
    }

    /**
     * Converts this array to a list.
     *
     * @return the list view of this array
     */
    public PSSListInst toList() {
        PSSListInst res = PSSListInst.ofElementDataType("", m_elem_type_model);
        for (PSSInst e : m_array) {
            if (e instanceof PSSIScalarInst)
                res.pushBack(e.toVal());
            else
                res.pushBack(e);
        }
        return res;
    }

    @Override
    public PSSListVal toVal() {
        PSSListVal res = PSSListVal.ofElementDataType(m_elem_type_model);
        for (PSSInst elem : m_array)
            res.add(elem.toVal());
        return res;
    }

};
