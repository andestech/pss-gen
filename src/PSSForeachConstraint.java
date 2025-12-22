
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PSSForeachConstraint extends PSSConstraint {

    String m_iterator_identifier = null;

    PSSExpression m_expression = null;

    String m_index_identifier = null;

    ArrayList<PSSConstraint> m_constraints = null;

    public PSSForeachConstraint(String iterator, PSSExpression exp, String index, List<PSSConstraint> constraints) {
        m_iterator_identifier = iterator;
        m_expression = exp;
        m_index_identifier = index;
        m_constraints = new ArrayList<PSSConstraint>(constraints);
    }

    @Override
    public PSSDomainMap deduceDomain(PSSInst var) {
        PSSDomainMap res = new PSSDomainMap();

        PSSVal e = m_expression.eval(var);

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
            var.addInst(foreach_inst);

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

            for (var constraint : m_constraints) {
                PSSDomainMap m = constraint.deduceDomain(foreach_inst);
                if (m != null)
                    res = res.join(m);
            }

            var.m_insts.remove(foreach_inst);
        }

        return res;
    }

    @Override
    public boolean validate(PSSInst var) {

        PSSVal e = m_expression.eval(var);

        if (!(e instanceof PSSICollection))
            PSSMessage.Error("", m_expression.getText() + " is not a collection type");

        boolean res = true;
        PSSICollection c = (PSSICollection) e;
        PSSIIterator it = c.iterator();
        while (it.hasNext()) {
            Map.Entry<PSSVal, PSSVal> entry = it.next();
            PSSVal key = entry.getKey();
            PSSVal val = entry.getValue();

            /* Create a new namespace to shadow the key and value variables */
            PSSNamespaceInst foreach_inst = new PSSNamespaceInst("");
            var.addInst(foreach_inst);

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

            for (PSSConstraint constraint : m_constraints) {
                if (!constraint.validate(foreach_inst)) {
                    res = false;
                    break;
                }
            }

            var.m_insts.remove(foreach_inst);

        }
        return res;
    }

}
