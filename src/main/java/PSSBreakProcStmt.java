
public class PSSBreakProcStmt extends PSSProcStmt {

    public PSSBreakProcStmt() {
    }

    @Override
    public void eval(PSSInst inst) throws PSSBreakException {
        throw new PSSBreakException();
    }

    @Override
    public String getText() {
        return "break";
    }

    @Override
    public void dump(String indent) {
        System.out.println(indent + getText());
    }

    @Override
    public String toString() {
        return getText();
    }

}
