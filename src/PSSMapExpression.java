import java.util.*;

public class PSSMapExpression extends PSSExpression {
    public PSSExpression m_key;
    public PSSExpression m_val;

    public PSSMapExpression(PSSExpression key, PSSExpression val) {
        m_key = key;
        m_val = val;
    }

    public PSSVal eval(PSSInst var) {
        PSSSetVal result = new PSSSetVal();
        PSSVal key = m_key.eval(var);
        PSSVal val = m_val.eval(var);
        result.getValList().add(0, key);
        result.getValList().add(1, val);
        return result;
    }

    public String getText() {
        return "{"+m_key.getText()+":"+m_val.getText()+"}";
    }
}
