import java.util.*;

public class PSSMatchActivity extends PSSActivity {

    public PSSExpression m_cond;
    public List<PSSExpression> m_ranges;
    public List<PSSActivity> m_stmts;
    public PSSActivity m_default;

    public PSSMatchActivity(PSSExpression cond) {
        m_cond = cond;
        m_ranges = new ArrayList<PSSExpression> ();
        m_stmts = new ArrayList<PSSActivity> ();
        m_default = null;
    }

    public void addChoice(PSSExpression r, PSSActivity s) {
        m_ranges.add(r);
        s.m_parent = this;
        m_stmts.add(s);
    }
    public void addDefaultChoice(PSSActivity s) {
        m_default = s;
        m_default.m_parent = this;
    }
    public void evalActivity(PSSInst inst) {
        boolean matched = false;
        int size = m_ranges.size();
        PSSVal cond = m_cond.eval(inst);

        for (int i = 0; i < size && !matched; i++) {
            PSSListVal range_vals = (PSSListVal)m_ranges.get(i).eval(inst);
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
