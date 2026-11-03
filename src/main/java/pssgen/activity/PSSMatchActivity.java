package pssgen.activity;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.expr.PSSOpenRangeListExpression;
import pssgen.inst.PSSInst;
import pssgen.util.PSSMessage;
import pssgen.value.PSSListVal;
import pssgen.value.PSSVal;

public class PSSMatchActivity extends PSSActivity {

    protected PSSExpression m_cond;
    protected List<PSSExpression> m_ranges;
    protected List<PSSActivity> m_stmts;
    protected PSSActivity m_default;

    public PSSMatchActivity(PSSExpression cond) {
        m_cond = cond;
        m_ranges = new ArrayList<PSSExpression> ();
        m_stmts = new ArrayList<PSSActivity> ();
        m_default = null;
    }

    public void addChoice(PSSExpression r, PSSActivity s) {
        m_ranges.add(r);
        s.setParent(this);
        m_stmts.add(s);
    }
    public void addDefaultChoice(PSSActivity s) {
        m_default = s;
        m_default.setParent(this);
    }
    public void evalActivity(PSSInst inst) {
        boolean matched = false;
        int size = m_ranges.size();
        PSSVal cond = m_cond.eval(inst);

        for (int i = 0; i < size && !matched; i++) {
            PSSListVal range_vals = (PSSListVal) choice(m_ranges.get(i), inst);
            if (range_vals.InRange(cond).toBool()) {
                m_stmts.get(i).evalActivity(inst);
                matched = true;
                break;
            }
        }
        if (!matched) {
            if (m_default != null)
                m_default.evalActivity(inst);
            else
                PSSMessage.Error("MatchActivity", "Unmatched expression " + m_cond.getText() + " is illegal.");
        }
    }

    /** PSS 2.0 Section 21.3 a): the expected type of a choice is that of the match expression. */
    private PSSVal choice(PSSExpression range, PSSInst inst) {
        if (range instanceof PSSOpenRangeListExpression)
            return ((PSSOpenRangeListExpression) range).eval(inst, m_cond.plainInst(inst));
        return range.eval(inst);
    }

    public void dump(String indent) {
        String str_cond = m_cond.getText();
        System.out.println(indent + "match (" + str_cond + ") {");
        for (int i=0; i<m_ranges.size(); i++) {
            System.out.print(indent + "\t" + m_ranges.get(i).getText());
            System.out.println(" : ");
            m_stmts.get(i).dump(indent + "\t");
        }
        if (m_default != null) {
            System.out.println(indent + "\tdefault : ");
            m_default.dump(indent + "\t");
        }
        System.out.println(indent + "}");
    }
}
