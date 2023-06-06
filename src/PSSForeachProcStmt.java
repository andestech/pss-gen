
import java.util.Map;

public class PSSForeachProcStmt extends PSSProcStmt {

    String m_iterator_identifier = null;

    /**
     * An expression that should be of a collection type (array, list, map, or set)
     */
    PSSExpression m_expression = null;

    String m_index_identifier = null;

    PSSProcStmt m_proc_stmt = null;

    public PSSForeachProcStmt(String iterator, PSSExpression exp, String index, PSSProcStmt stmt) {
        super();
        m_iterator_identifier = iterator;
        m_expression = exp;
        m_index_identifier = index;
        m_proc_stmt = stmt;
        m_proc_stmt.m_parent = this;
    }

    @Override
    public void eval(PSSInst inst) {
        PSSVal e = m_expression.eval(inst);

        if (!(e instanceof PSSICollection))
            PSSMessage.Error("", m_expression.getText() + " is not a collection type");

        PSSICollection c = (PSSICollection) e;
        PSSIIterator it = c.iterator();
        PSSInst key_inst = null;
        PSSInst val_inst = null;
        /* Create a new namespace to shadow the key and value variables */
        PSSNamespaceInst foreach_inst = new PSSNamespaceInst("");
        inst.addInst(foreach_inst);
        /* Create one more namespace for local variables declared in the foreach body */
        PSSNamespaceInst body_inst = new PSSNamespaceInst("");
        foreach_inst.addInst(body_inst);
        try {
        while (it.hasNext()) {
            Map.Entry<PSSVal, PSSVal> entry = it.next();
            PSSVal key = entry.getKey();
            PSSVal val = entry.getValue();

            if (m_index_identifier != null) {
                if (!c.isIndexable())
                    PSSMessage.Error("", e.getText() + " is not indexable.");
                if (key == null)
                    PSSMessage.Error("", "Expression " + m_expression.getText() + " does not have index variables");
                if (key_inst == null) {
                    key.getTypeModel().declInst(foreach_inst, m_index_identifier, false);
                    key_inst = foreach_inst.findInstance(m_index_identifier);
                    key_inst.setReadOnly(true);
                }
                key_inst.assign(key);
            }
            if (m_iterator_identifier != null) {
                if (val_inst == null) {
                    val.getTypeModel().declInst(foreach_inst, m_iterator_identifier, false);
                    val_inst = foreach_inst.findInstance(m_iterator_identifier);
                    val_inst.setReadOnly(true);
                }
                val_inst.assign(val);
            }
            try {
                m_proc_stmt.eval(body_inst);
            } catch (PSSContinueException dontcare) {
                // continue
            }
        }
        } catch (PSSBreakException dontcare) {
            // break
        }
    }

    @Override
    public void dump(String indent) {
        System.out.print(indent + "foreach (");
        if (m_iterator_identifier != null && !m_iterator_identifier.equals(""))
            System.out.print(m_iterator_identifier + " : ");
        System.out.print(m_expression.getText());
        if (m_index_identifier != null && !m_index_identifier.equals(""))
            System.out.print("[" + m_index_identifier + "]");
        m_proc_stmt.dump("  " + indent);
    }

}
