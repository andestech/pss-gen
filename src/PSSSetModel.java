import java.util.*;

public class PSSSetModel extends PSSModel {

    PSSModel m_data_type;

    public PSSSetModel (PSSModel data_type) {
        super("set<"+data_type.m_id+">");
        m_data_type = data_type;
    }

    @Override
    public PSSSetInst declInst(String id, boolean rand) {
        return new PSSSetInst(id, m_data_type);
    }

    @Override
    public String getText() {
        return m_id;
    }

    @Override
    public void dump (String indent) {
        System.out.println(indent + getText());
    }

}
