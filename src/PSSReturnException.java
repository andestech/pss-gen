
public class PSSReturnException extends RuntimeException {

    PSSVal m_res = null;

    public PSSReturnException() {
    }

    public PSSReturnException(PSSVal v) {
        m_res = v;
    }

    public PSSVal getReturnValue() {
        return m_res;
    }

}