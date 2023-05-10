import java.util.List;
import java.util.ArrayList;

public class PSSArrayInst extends PSSInst {
    PSSModel m_type_model;
    int m_dim = 0;
    List<PSSInst> m_array = new ArrayList<PSSInst>();

    NativeMethod m_method_size = new NativeMethod("size", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return size();
        }
    };

    NativeMethod m_method_sum = new NativeMethod("sum", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return sum();
        }
    };

    NativeMethod m_method_to_list = new NativeMethod("to_list", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return toVal();
        }
    };

    NativeMethod m_method_to_set = new NativeMethod("to_set", 0) {
        protected PSSVal doEval(List<PSSVal> args) {
            return to_set();
        }
    };

    public PSSArrayInst(String id, PSSModel type_model, int dim, boolean rand) {
        super(id, "array<" + type_model.m_id + ">", rand);
        if (dim <= 0)
            PSSMessage.Error("ArrayInst", "'"+id+"' is a zero-length array");

        m_type_model = type_model;
        m_dim = dim;
        for (int i = 0; i < m_dim; i++) {
            PSSInst inst = m_type_model.declInst(id + "[" + i + "]", m_rand);
            m_array.add(inst);
        }

        addNativeMethod(m_method_size);
        addNativeMethod(m_method_sum);
        addNativeMethod(m_method_to_list);
        addNativeMethod(m_method_to_set);
    }

	public void assign(PSSVal val) {
        if (!(val instanceof PSSListVal))
            PSSMessage.Fatal("The array type should be assigned as a PSSArrayVal");
        PSSListVal arrayVal = (PSSListVal) val;

        if (!m_type_model.isCompatible(arrayVal.getTypeModel()))
            PSSMessage.Error("ArrayInst", "The element tyep " +
                    arrayVal.getTypeModel().getText() + " of " +
                    arrayVal.getText() + " is incompatible with array " + m_id);

        if (arrayVal.size() != m_dim)
            PSSMessage.Error("ArrayInst", "The size of the array is " +
                    "different to the assigned aggregate literal");

        for (int i = 0; i < m_dim; i++) {
            PSSInst elemInst = m_array.get(i);
            PSSVal elemVal = arrayVal.getValList().get(i);
            elemInst.assign(elemVal);
        }
	}

    @Override
    public void randomize() {
        if (m_rand) {
            for (PSSInst elem: m_array)
                elem.randomize();
        }
    }

    @Override
	public PSSInst indexOf(PSSVal index) {
        if (!(index instanceof PSSIntVal))
            PSSMessage.Error("ArrayInst",
                    "The index of an array must be a PSSIntVal");
        PSSIntVal intIndex = (PSSIntVal) index;
        return m_array.get(intIndex.toInt());
	}

    public PSSIntVal size() {
        return new PSSIntVal(m_dim);
    }

    public PSSIntVal sum() {
        if (!(m_type_model instanceof PSSIntModel))
            PSSMessage.Error("ArrayInst",
                    "Array.sum() is only used on Int type.");

        PSSListVal array = toVal();
        int sum = 0;
        for (PSSVal e: array.getValList())
            sum += e.toInt();
        return new PSSIntVal(sum);
    }

    public PSSSetVal to_set() {
        PSSListVal array  = toVal();
        PSSSetVal set = new PSSSetVal(m_type_model);
        for (PSSVal e: array.getValList())
            set.add(e);
        return set;
    }

    @Override
    public PSSListVal toVal() {
        PSSListVal res = new PSSListVal(m_type_model);
        for (PSSInst elem: m_array)
            res.add(elem.toVal());
        return res;
    }

};

