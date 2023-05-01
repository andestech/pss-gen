import java.util.*;

public class PSSMapModel extends PSSModel {

    PSSModel m_key_type_model;
    PSSModel m_val_type_model;

    public PSSMapModel (PSSModel key_type_model, PSSModel val_type_model) {
        super("map<"+key_type_model.m_id+","+val_type_model.m_id+">");
        PSSMessage.Debug("[" + this.getClass().getName() + "] Creating MapModel: key = " + key_type_model.getId() + ", value = " + val_type_model.getId());
        m_key_type_model = key_type_model;
        m_val_type_model = val_type_model;
    }

    public PSSMapInst declInst(String id, boolean rand) {
        PSSMessage.Debug("[" + this.getClass().getName() + "] Instantiating MapModel: id = " + id + ", rand = " + rand);
        return new PSSMapInst(id, m_key_type_model, m_val_type_model, rand);
    }

    public String getText() {
        return m_id;
    }

    public void dump (String indent) {
        System.out.println(indent + getText());
    }
}
