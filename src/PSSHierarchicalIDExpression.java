import java.util.*;

public class PSSHierarchicalIDExpression extends PSSExpression {

    List<PSSMemberPathElemExpression> m_member_list;

    public PSSHierarchicalIDExpression() {
        m_member_list = new ArrayList<PSSMemberPathElemExpression>();
    }

    public void addMemberElement(PSSMemberPathElemExpression m) {
        m_member_list.add(m);
    }

    public PSSInst getInst(PSSInst var) {
        PSSInst inst = var;
        for (PSSMemberPathElemExpression m : m_member_list)
            inst = m.getInst(inst);
        return inst;
    }

    @Override
    public String getText() {
        List<String> strs = new ArrayList<String>();
        for (PSSMemberPathElemExpression e : m_member_list)
            strs.add(e.toString());
        return String.join(".", strs);
    }

    @Override
    public String toString() {
        return getText();
    }

}
