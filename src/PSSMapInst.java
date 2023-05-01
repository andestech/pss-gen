import java.util.*;

public class PSSMapInst extends PSSInst {

    PSSModel key_type;

    PSSModel val_type;

    boolean rand;

    Map<PSSInst, PSSInst> map = new HashMap<PSSInst, PSSInst>();

    public PSSMapInst(String id, PSSModel key_type, PSSModel val_type,
            boolean rand) {
        super(id, "map<"+key_type.m_id+","+val_type.m_id+">", rand);
        super.m_rand = false;
        PSSMessage.Debug("[" + this.getClass().getName() + "] Creating MapInst: key_type = " + key_type.getId() + ", value = " + val_type.getId());
        this.key_type = key_type;
        this.val_type = val_type;
        this.rand = rand;
    }

    private String get_map_id_from_key_id(String key) { return m_id+"["+key+"]"; }

    public void assign(PSSVal val) {
        if (!(val instanceof PSSMapVal))
            PSSMessage.Fatal("The map type should be assigned a map_literal." + val.getClass());

        PSSMessage.Debug("[" + this.getClass().getName() + "] Assigning value " + val.getText() + " to " + this.m_id);

        /* TODO: check the type of PSSVal */
        map.clear();
        PSSMapVal m = (PSSMapVal)val;
        for (PSSVal k : m.keys().getValList()) {
            PSSVal v = m.get(k);
            PSSInst ki = key_type.declInst(m_id + ":key=" + k.getText(), rand);
            PSSInst vi = val_type.declInst(m_rand + ":val=" + v.getText(), rand);
            ki.assign(k);
            vi.assign(v);
            map.put(ki, vi);
        }
    }

    public PSSInst get(String kstr) {
        PSSInst res = null;
        for (PSSInst k : map.keySet()) {
            if (k.toVal().getText().equals(kstr)) {
                res = map.get(k);
                break;
            }
        }
        return res;
    }

    @Override
    public PSSMapVal toVal() {
        PSSMapVal res = new PSSMapVal();
        for (PSSInst k : map.keySet()) {
            res.insert(k.toVal(), map.get(k).toVal());
        }
        return res;
    }

    public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The list collection type is not randomizable.
    }

}
