import java.util.*;

public class PSSMapInst extends PSSInst {
    private static int M_KEY = 0;
    private static int M_VAL = 1;

    PSSModel m_key_type_model;
    PSSModel m_val_type_model;
    HashMap<String, ArrayList<PSSInst>> m_map;

    PSSMapInst(String id, PSSModel key_type_model, PSSModel val_type_model,
            boolean rand) {
        super(id, "map<"+key_type_model.m_id+","+val_type_model.m_id+">", rand);
        super.m_rand = false;
        m_key_type_model = key_type_model;
        m_val_type_model = val_type_model;
        m_map = new HashMap<String, ArrayList<PSSInst>>();
    }

    private String get_map_id_from_key_id(String key) { return m_id+"["+key+"]"; }

    public void assign(PSSVal val) {
        if (!PSSSetVal.class.isInstance(val))
            PSSMessage.Fatal("The map type should be assigned as a set of map_literal.");

        m_map.clear();
        PSSSetVal setval = (PSSSetVal) val;
        for (PSSVal mapVal: setval.getValList()) {
            if (!PSSSetVal.class.isInstance(mapVal))
                PSSMessage.Fatal("A map_literal should be evaluated as a PSSSetVal.");
            PSSSetVal mapSetVal = (PSSSetVal) mapVal;
            if (mapSetVal.getValList().size() != 2)
                PSSMessage.Fatal("A map_literal should have two items.");

            PSSVal keyVal = mapSetVal.indexOf(0);
            PSSInst keyInst = m_key_type_model.declInst("", m_rand);
            keyInst.assign(keyVal);

            PSSVal valVal = mapSetVal.indexOf(1);
            PSSInst valInst = m_val_type_model.declInst("", m_rand);
            valInst.assign(valVal);

            ArrayList<PSSInst> map = new ArrayList<PSSInst>();
            map.add(keyInst);
            map.add(valInst);
            m_map.put(get_map_id_from_key_id(keyVal.getText()), map);
        }
    }

    public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The list collection type is not randomizable.
    }

    public PSSInst get(String t) {
        ArrayList<PSSInst> map = m_map.get(get_map_id_from_key_id(t));
        if (map == null)
			PSSMessage.Fatal("Failed to get the element of "+get_map_id_from_key_id(t)+".");
        return map.get(M_VAL);
    }
}
