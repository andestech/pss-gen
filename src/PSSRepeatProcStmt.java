
/**
 * This class models procedural repeat statements.
 */
public class PSSRepeatProcStmt extends PSSProcStmt {

    /** an optional index identifier */
    String m_index_identifier = null;

    /** the count expression */
    PSSExpression m_count = null;;

    /** the procedural expression */
    PSSProcStmt m_stmt = null;

    /**
     * Construct a procedural repeat statement.
     *
     * @param index an index string
     * @param count an expression
     * @param stmt  a procedural statement
     */
    public PSSRepeatProcStmt(String index, PSSExpression count, PSSProcStmt stmt) {
        super();
        m_index_identifier = index;
        m_count = count;
        m_stmt = stmt;
        m_stmt.m_parent = this;
    }

    @Override
    public void eval(PSSInst inst) {
        PSSNamespaceInst repeat_inst = new PSSNamespaceInst("");
        inst.addInst(repeat_inst);

        PSSInst index_inst = null;
        if (m_index_identifier != null) {
            index_inst = new PSSIntInst(m_index_identifier, false,
                    PSSIntModel.DEFAULT_INT_SIZE, false);
            repeat_inst.addInst(index_inst);
        }

        int count = m_count.eval(inst).toInt();
        try {
            for (int i = 0; i < count; i++) {
                if (index_inst != null) {
                    index_inst.assign(new PSSIntVal(i));
                }
                try {
                    m_stmt.eval(repeat_inst);
                } catch (PSSContinueException dontcare) {
                    // continue
                }
            }
        } catch (PSSBreakException dontcare) {
            // break
        }

        inst.removeInst(repeat_inst);
    }

    @Override
    public void dump(String indent) {
        String str_count = m_count.getText();

        System.out.println(indent + "repeat (" + str_count + ") {");
        m_stmt.dump(indent + "\t");
        System.out.println(indent + "}");
    }

}
