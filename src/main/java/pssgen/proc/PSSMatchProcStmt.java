package pssgen.proc;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.expr.PSSOpenRangeListExpression;
import pssgen.inst.PSSInst;
import pssgen.util.PSSMessage;
import pssgen.value.PSSListVal;
import pssgen.value.PSSVal;

public class PSSMatchProcStmt extends PSSProcStmt {

    private PSSExpression m_cond;
    private List<PSSExpression> m_ranges;
    private List<PSSProcStmt> m_stmts;
    private PSSProcStmt d_stmt;

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
            PSSListVal range_vals = (PSSListVal) choice(m_ranges.get(i), inst);
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

	public final PSSExpression getCond() {
		return m_cond;
	}

	public final List<PSSExpression> getRanges() {
		return m_ranges;
	}

	public final List<PSSProcStmt> getStmts() {
		return m_stmts;
	}

	public final PSSProcStmt getDefaultStmt() {
		return d_stmt;
	}

    /** PSS 2.0 Section 21.3 a): the expected type of a choice is that of the match expression. */
    private PSSVal choice(PSSExpression range, PSSInst inst) {
        if (range instanceof PSSOpenRangeListExpression)
            return ((PSSOpenRangeListExpression) range).eval(inst, m_cond.plainInst(inst));
        return range.eval(inst);
    }
}
