import java.util.*;

public class PSSMatchProcStmt extends PSSProcStmt {

    public PSSExpression m_cond;
    public List<PSSExpression> m_ranges;
    public List<PSSProcStmt> m_stmts;
    public PSSProcStmt d_stmt;

    public PSSMatchProcStmt(PSSExpression cond, List<PSSExpression> ranges,
                            List<PSSProcStmt> stmts, PSSProcStmt stmt) {
        assert(ranges.size() == stmts.size());
        m_cond = cond;
        m_ranges = ranges;
        m_stmts = stmts;
        d_stmt = stmt;
    }

    @Override
    public void eval(PSSInst inst) {
        boolean matched = false;
        PSSVal cond = m_cond.eval(inst);
        for (int i = 0; i < m_ranges.size() && !matched; i++) {
            PSSListVal range_vals = (PSSListVal)m_ranges.get(i).eval(inst);
            if (range_vals.InRange(cond).toBool()) {
                m_stmts.get(i).eval(inst);
                matched = true;
                break;
            }
        }
        if (!matched) {
            if (d_stmt != null)
                d_stmt.eval(inst);
            else
                PSSMessage.Error("MatchProcStmt", "Unmatched expression " + m_cond.getText() + " is illegal.");
        }
    }

    @Override
    public String getText() {
        String text = "match (" + m_cond.getText() + ") {";
        for (int i=0; i<m_ranges.size(); i++) {
            text = text.concat(m_ranges.get(i).getText() + " : ");
            text = text.concat(m_stmts.get(i).getText());
        }
        if (d_stmt != null) {
            text = text.concat("default : ");
            text = text.concat(d_stmt.getText());
        }
        return text + "}";
    }

    @Override
    public void dump(String indent) {
        String str_cond = m_cond.getText();
        System.out.println(indent + "match (" + str_cond + ") {");
        for (int i=0; i<m_ranges.size(); i++) {
            System.out.print(indent + "\t" + m_ranges.get(i).getText());
            System.out.println(" : ");
            m_stmts.get(i).dump(indent + "\t");
        }
        if (d_stmt != null) {
            System.out.println(indent + "\tdefault : ");
            d_stmt.dump(indent + "\t");
        }
        System.out.println(indent + "}");
    }

}
