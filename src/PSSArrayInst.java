import java.util.*;

public class PSSArrayInst extends PSSInst {
    PSSModel m_type_model;
    int m_dim = 0;
    ArrayList<PSSInst> m_array = new ArrayList<PSSInst>();

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
            return to_list();
        }
    };

    NativeMethod m_method_to_set = new NativeMethod("to_set", 0) {
        protected PSSInst doEval(List<PSSVal> args) {
            return to_set();
        }
    };

	PSSArrayInst(String id, PSSModel type_model, int dim, boolean rand) {
        super(id, "array<"+type_model.m_id+">", rand);
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
        if (!(val instanceof PSSSetVal)) // FIXME
            PSSMessage.Fatal("The array type should be assigned as a PSSSetVal");
        PSSSetVal arrayVal = (PSSSetVal) val;

        if (arrayVal.size() != m_dim)
            PSSMessage.Fatal("The size of the array is different to " +
                    "the assigned aggregate literal");

        for (int i = 0; i < m_dim; i++) {
            PSSInst elemInst = m_array.get(i);
            PSSVal elemVal = arrayVal.getValList().get(i); // FIXME
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

    public PSSIntInst size() {
        PSSIntInst res = new PSSIntInst(m_id + ".size()", false,
                PSSIntModel.DEFAULT_INT_SIZE, false);
        res.assign(new PSSIntVal(m_dim));
        return res;
    }

    public PSSIntInst sum() {
        int sum = this.toVal().sum();
        PSSIntInst res = new PSSIntInst(m_id + ".sum()", false,
                PSSIntModel.DEFAULT_INT_SIZE, false);
        res.assign(new PSSIntVal(sum));
        return res;
    }

    public PSSListInst to_list() {
        PSSMessage.Fatal("Yet to be implemented");
        return null;
    }

    public PSSSetInst to_set() {
        PSSMessage.Fatal("Yet to be implemented");
        return null;
    }

    @Override
    public PSSArrayVal toVal() {
        PSSArrayVal res = new PSSArrayVal();
        for (PSSInst elem: m_array)
            res.insert(elem.toVal());
        return res;
    }

};

