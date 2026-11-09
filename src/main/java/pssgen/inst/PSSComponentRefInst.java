package pssgen.inst;

import pssgen.model.PSSModel;
import pssgen.model.PSSRefModel;
import pssgen.model.PSSTypeCategory;
import pssgen.solver.PSSDomain;
import pssgen.solver.PSSRefDomain;
import pssgen.value.PSSRefVal;

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
    public PSSComponentRefInst(String id) {
        super(id, new PSSRefModel(PSSTypeCategory.COMPONENT));
        m_domain = null;
    }

    private PSSInst findComponentInst() {
        // Find the default component instance when there is no constraint.
        PSSInst action = getParent();
        while (!(action instanceof PSSActionInst) && action != null) {
            action = action.getParent();
        }
        if (action != null) {
            PSSInst comp = action.getComponentInst();
            if (comp != null)
                return ofContextType(comp, action.getTypeDecl().getParent());
        }
        return null;
    }

    /**
     * PSS 2.0 Section 10.6: the static type of comp is the action's context component,
     * and Section 17.4.4: comp is assigned a component instance of that type. Returns
     * comp when it is of type context, else the first instance of type context in its
     * sub-tree (depth first, in declaration order), else comp.
     */
    private static PSSInst ofContextType(PSSInst comp, PSSModel context) {
        if (comp.getTypeModel() == context)
            return comp;
        PSSInst found = findOfType(comp, context);
        return found != null ? found : comp;
    }

    private static PSSInst findOfType(PSSInst comp, PSSModel type) {
        for (PSSInst child : comp.getInsts()) {
            if (child instanceof PSSComponentInst) {
                if (child.getTypeModel() == type)
                    return child;
                PSSInst found = findOfType(child, type);
                if (found != null)
                    return found;
            }
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
            m_ref = m_domain.random(run().random());
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

    @Override
    public PSSDomain getDomain() {
        return m_domain;
    }

    @Override
    public void restoreDomain(PSSDomain domain) {
        m_domain = (PSSRefDomain) domain;
    }

}