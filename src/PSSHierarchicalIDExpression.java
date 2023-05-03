import java.util.*;

public class PSSHierarchicalIDExpression extends PSSExpression {

    ArrayList<PSSMemberPathElemExpression> m_member_list;

    public PSSHierarchicalIDExpression() {
        m_member_list = new ArrayList<PSSMemberPathElemExpression>();
    }

    public void addMemberElement(PSSExpression m) {
        PSSMemberPathElemExpression _m = (PSSMemberPathElemExpression) m;
        m_member_list.add(_m);
    }

    public PSSInst getInst(PSSInst var) {
        PSSInst inst = var;
        for (PSSMemberPathElemExpression m: m_member_list) {
            inst = m.getInst(inst);
        }
        return inst;
    }

    @Override
    public String toString() {
        List<String> strs = new ArrayList<String>();
        for (PSSMemberPathElemExpression e : m_member_list)
            strs.add(e.toString());
        return String.join(".", strs);
    }

}
