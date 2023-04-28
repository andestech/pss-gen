import java.util.*;
import java.lang.reflect.*;

public class PSSMemberPathElemExpression extends PSSExpression {

    String identifier;
    boolean isFunctionCall;
    ArrayList<PSSVal> function_parameter_list;

    public PSSMemberPathElemExpression(String id, boolean isCall) {
        identifier = id;
        isFunctionCall = isCall;
        function_parameter_list = new ArrayList<PSSVal>();
    }

    public void addPara(PSSVal val) {
        function_parameter_list.add(val);
    }

    // TODO: how to improve this method? Function pointer? getMethod.invoke()?
    private PSSInst evalMethodIdentifier(PSSInst var) {
        PSSInst inst = null;
        if (var instanceof PSSArrayInst)
            inst = PSSArrayInst.class.cast(var).evalMethod(identifier);
        else
            PSSMessage.Fatal("Unsupported data type");
        return inst;
    }

    public PSSInst getInst(PSSInst var) {
        PSSInst inst = null;
        if (isFunctionCall) {
            inst = evalMethodIdentifier(var);
        }
        return inst;
    }

}
