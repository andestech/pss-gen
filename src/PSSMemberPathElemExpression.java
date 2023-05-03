import java.util.*;

public class PSSMemberPathElemExpression extends PSSExpression {

    String identifier;
    List<PSSExpression> function_parameter_list = null;
    PSSExpression array_index = null;

    public PSSMemberPathElemExpression(String id) {
        identifier = id;
    }

    public void setArguments(List<PSSExpression> args) {
        function_parameter_list = args;
    }

    public void setArrayIndex(PSSExpression idx) {
        array_index = idx;
    }

    // TODO: how to improve this method? Function pointer? getMethod.invoke()?
    private PSSInst evalMethod(PSSInst var) {
		/* Evaluate arguments */
		List<PSSVal> vals = new ArrayList<PSSVal>();
		for (PSSExpression arg : function_parameter_list) {
			vals.add(arg.eval(var));
		}

        /* Invoke method */
        PSSInst res = var.evalMethod(identifier, vals);

        return res;
    }

    public PSSInst getInst(PSSInst var) {
        PSSInst res =  null;
        if (function_parameter_list == null) {
            res = var.findInstance(identifier);
        } else {
            res = evalMethod(var);
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(identifier);
        if (function_parameter_list != null) {
            sb.append("(");
            List<String> strs = new ArrayList<String>();
            for (PSSExpression e : function_parameter_list) {
                strs.add(e.getText());
            }
            sb.append(String.join(", ", strs));
            sb.append(")");
        }
        if (array_index != null) {
            sb.append("[" + array_index.getText() + "]");
        }
        return sb.toString();
    }

}
