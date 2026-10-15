
public class PSSContinueProcStmt extends PSSProcStmt {

    @Override
    public void eval(PSSInst inst) throws PSSContinueException {
        throw new PSSContinueException();
    }

    @Override
    public String getText() {
        return "continue";
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
