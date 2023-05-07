import java.util.*;

public class PSSHierarchicalIDExpression extends PSSExpression {

    List<PSSMemberPathElemExpression> m_member_list;

    public PSSHierarchicalIDExpression() {
        m_member_list = new ArrayList<PSSMemberPathElemExpression>();
    }

    public void addMemberElement(PSSMemberPathElemExpression m) {
        m_member_list.add(m);
    }

    @Override
    public PSSInst getInst(PSSInst var) {
        PSSInst inst = var;
        for (PSSMemberPathElemExpression m : m_member_list)
            inst = m.getInst(inst);
        return inst;
    }

    @Override
    public PSSVal eval(PSSInst var) {
        PSSVal res = null;
        if (m_member_list.size() > 0) {
            PSSInst inst = var;
            for (int i = 0; i < m_member_list.size() - 1; i++)
                inst = m_member_list.get(i).getInst(inst);
            res = m_member_list.get(m_member_list.size() - 1).eval(inst);
        }
        return res;
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
