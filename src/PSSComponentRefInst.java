
/**
 * A @{code PSSComponentRefInst} is a reference to another component instance.
 * For example, "comp" used in an action is a reference to a component instance.
 */
public class PSSComponentRefInst extends PSSRefInst {

    /** the domain of the value */
    PSSRefDomain m_domain;

    /**
     * Constructs this reference.
     *
     * @param id        the identify of this reference
     * @param type_decl the model of this reference
     */
    public PSSComponentRefInst(String id, PSSRefModel type_decl) {
        super(id, type_decl);
        m_domain = null;
    }

    /**
     * Constructs this reference.
     *
     * @param id   the identify of this reference
     * @param type the type of this reference
     */
    public PSSComponentRefInst(String id) {
        this(id, null);
    }

    private PSSInst findComponentInst() {
        // Find the default component instance when there is no constraint.
        PSSInst action = m_parent;
        while (!(action instanceof PSSActionInst) && action != null) {
            action = action.m_parent;
        }
        if (action != null) {
            PSSInst comp = action.getComponentInst();
            if (comp != null)
                return comp;
        }
        return null;
    }

    @Override
    public PSSInst getReference() {
        if (m_ref == null) {
            // In PSS 2.0 Example 264, b_foo.comp is read before randomized.
            // Allow the access (current implementation)? Return null but make the
            // constraint hold vacuously?
            setReference(findComponentInst());
        }
        return super.getReference();
    }

    @Override
    public void randomize() {
        if (m_domain == null) {
            PSSInst comp = findComponentInst();
            setReference(comp);
        } else {
            m_ref = m_domain.random();
        }
    }

    @Override
    public PSSRefDomain getInitDomain() {
        PSSRefDomain res = new PSSRefDomain();
        PSSInst root = getRoot();
        if (root != null) {
            if (root instanceof PSSComponentInst)
                res.add(new PSSRefVal(getTypeModel(), root));

            // Find all possible component instances.
            for (PSSInst inst : root.getDescendants()) {
                if (inst instanceof PSSComponentInst)
                    res.add(new PSSRefVal(getTypeModel(), inst));
            }
        }
        return res;
    }

    @Override
    public void assignDomain(PSSDomain domain) {
        if (domain instanceof PSSRefDomain)
            m_domain = (PSSRefDomain) domain;
    }

}