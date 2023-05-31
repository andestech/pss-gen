
import java.util.List;

/**
 * A @{code PSSRefInst} is a reference to a target instance.
 */
public class PSSRefInst extends PSSInst {

    /** the reference value */
    PSSRefVal m_ref;

    /**
     * Constructs this reference.
     *
     * @param id        the name of this reference
     * @param type_decl the model of this reference
     */
    public PSSRefInst(String id, PSSRefModel type_decl) {
        super(id, type_decl.m_id, type_decl, false);
        m_ref = null;
    }

    @Override
    public PSSRefModel getTypeModel() {
        return (PSSRefModel) super.getTypeModel();
    }

    /**
     * Constructs this reference.
     *
     * @param id   the name of this reference
     * @param type the type of this reference
     */
    public PSSRefInst(String id, String type) {
        super(id, type, false);
    }

    /**
     * Makes this reference refer to a specified instance.
     *
     * @param inst an instance
     */
    public void setReference(PSSInst inst) {
        m_ref = new PSSRefVal(getTypeModel(), inst);
    }

    /**
     * Returns the instance referred to by this reference.
     *
     * @return the instance referred to by this reference
     */
    public PSSInst getReference() {
        return m_ref.getInst();
    }

    @Override
    public PSSInst addInst(PSSInst inst) {
        return getReference().addInst(inst);
    }

    @Override
    public void removeInst(PSSInst inst) {
        getReference().removeInst(inst);
    }

    @Override
    public boolean isComponent() {
        return getReference().isComponent();
    }

    @Override
    protected PSSInst findChild(String id) {
        return getReference().findChild(id);
    }

    @Override
    public PSSInst findInstanceUnder(String hierarchy_id) {
        return getReference().findInstanceUnder(hierarchy_id);
    }

    @Override
    public PSSInst findInstance(String hierarchy_id) {
        return getReference().findInstance(hierarchy_id);
    }

    @Override
    public PSSInst indexOf(PSSVal index) {
        return getReference().indexOf(index);
    }

    @Override
    public PSSInst evalMethod(String name, List<PSSVal> args) throws NoSuchMethodException {
        return getReference().evalMethod(name, args);
    }

    @Override
    public PSSRefVal toVal() {
        return m_ref;
    }

    @Override
    public void assign(PSSVal val) {
        if (val instanceof PSSRefVal) {
            // Pass the input reference
            PSSRefVal r = (PSSRefVal) val;
            PSSRefModel m = getTypeModel();
            if (!m.isCompatible(r.getTypeModel()))
                PSSMessage.Error("PSSRefInst",
                        "The types of " + m_id + " (" + m.m_id + ") and the type of " + val.getText()
                                + " (" + val.getTypeModel().m_id + ") are incompatible.");
            m_ref = r;
        } else {
            // Create a new reference
            PSSModel m = val.getTypeModel();
            PSSInst inst = m.declInst("", false);
            inst.assign(val);
            m_ref = new PSSRefVal(new PSSRefModel(m), inst);
        }
    }

}