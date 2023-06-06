
import java.util.Map;

public class PSSForeachActivity extends PSSActivity {

    String m_iterator_identifier = null;

    /**
     * An expression that should be of a collection type (array, list, map, or set)
     */
    PSSExpression m_expression = null;

    String m_index_identifier = null;

    PSSActivity m_activity_stmt = null;

    public PSSForeachActivity(String iterator, PSSExpression exp, String index, PSSActivity stmt) {
        super();
        m_iterator_identifier = iterator;
        m_expression = exp;
        m_index_identifier = index;
        m_activity_stmt = stmt;
        m_activity_stmt.m_parent = this;
    }

    @Override
    public void evalActivity(PSSInst inst) {
        PSSVal e = m_expression.eval(inst);

        if (!(e instanceof PSSICollection))
            PSSMessage.Error("", m_expression.getText() + " is not a collection type");

        PSSICollection c = (PSSICollection) e;
        PSSIIterator it = c.iterator();
        while (it.hasNext()) {
            Map.Entry<PSSVal, PSSVal> entry = it.next();
            PSSVal key = entry.getKey();
            PSSVal val = entry.getValue();

            /* Create a new namespace to shadow the key and value variables */
            PSSNamespaceInst foreach_inst = new PSSNamespaceInst("");
            inst.addInst(foreach_inst);
            /* Create one more namespace for the foreach body */
            PSSNamespaceInst body_inst = new PSSNamespaceInst("");
            foreach_inst.addInst(body_inst);

            if (m_index_identifier != null) {
                if (!c.isIndexable())
                    PSSMessage.Error("", e.getText() + " is not indexable.");
                if (key == null)
                    PSSMessage.Error("", "Expression " + m_expression.getText() + " does not have index variables");

                key.getTypeModel().declInst(foreach_inst, m_index_identifier, false);
                PSSInst key_inst = foreach_inst.findInstance(m_index_identifier);
                key_inst.setReadOnly(true);
                key_inst.assign(key);
            }
            if (m_iterator_identifier != null) {
                val.getTypeModel().declInst(foreach_inst, m_iterator_identifier, false);
                PSSInst val_inst = foreach_inst.findInstance(m_iterator_identifier);
                val_inst.setReadOnly(true);
                val_inst.assign(val);
            }
            m_activity_stmt.evalActivity(body_inst);
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
        m_activity_stmt.dump("  " + indent);
    }

}
