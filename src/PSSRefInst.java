
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
     * Returns the type of the target.
     *
     *
     * @returns the type of the target
     */
    public PSSModel getTargetTypeModel() {
        PSSRefModel m = getTypeModel();
        return m == null ? null : m.getTargetTypeModel();
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
            PSSRefVal r = (PSSRefVal) val;
            if (!getTargetTypeModel().isCompatible(r.getTargetTypeModel()))
                PSSMessage.Error("PSSRefInst",
                        "The types of " + m_id + " (" + getTypeModel().m_id + ") and the type of " + val.getText()
                                + " (" + val.getTypeModel().m_id + ") are incompatible.");
            m_ref = r;
        } else
            PSSMessage.Error("", "Cannot assign a non-reference type value to a reference.");
    }

}