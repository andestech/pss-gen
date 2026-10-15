
import java.util.Map;
import java.util.HashMap;

/**
 * A {@code PSSRefDomain} is a domain of references.
 */
public class PSSRefDomain extends PSSDomain {

    /** references */
    Map<String, PSSRefVal> m_refs;

    /**
     * Constructs this empty domain.
     */
    public PSSRefDomain() {
        m_refs = new HashMap<String, PSSRefVal>();
    }

    /**
     * Adds a reference to this domain.
     *
     * @param ref a reference to be added
     */
    public void add(PSSRefVal ref) {
        m_refs.put(ref.getHierarchicalID(), ref);
    }

    /**
     * Returns the reference of a specified hierarchical ID.
     *
     * @return the reference of a specified hierarchical ID, or {@code null} if the
     *         ID is not found in this domain
     */
    public PSSRefVal get(String hid) {
        return m_refs.get(hid);
    }

    @Override
    public boolean isEmpty() {
        return m_refs.isEmpty();
    }

    @Override
    public PSSRefVal random() {
        if (isEmpty())
            return null;

        return m_refs.values().toArray(new PSSRefVal[0])[PSSRandom.nextInt(0, m_refs.size() - 1)];
    }

    @Override
    public PSSBoolVal InRange(PSSVal lhs) {
        if (lhs instanceof PSSRefVal) {
            PSSRefVal r = (PSSRefVal) lhs;
            return PSSBoolVal.valueOf(m_refs.containsKey(r.getHierarchicalID()));
        }
        return PSSBoolVal.FALSE;
    }

    @Override
    public PSSRefDomain join(PSSDomain dom) {
        PSSRefDomain res = new PSSRefDomain();

        if (dom instanceof PSSRefDomain) {
            PSSRefDomain d = (PSSRefDomain) dom;
            for (Map.Entry<String, PSSRefVal> entry : m_refs.entrySet()) {
                PSSRefVal r = d.get(entry.getKey());
                if (r == null)
                    continue;
                if (entry.getValue().Equal(r).toBool())
                    res.add(r);
            }
        }

        return res;
    }

    @Override
    public PSSRefDomain union(PSSDomain dom) {
        PSSRefDomain res = new PSSRefDomain();

        for (Map.Entry<String, PSSRefVal> entry : m_refs.entrySet()) {
            res.m_refs.put(entry.getKey(), entry.getValue());
        }

        if (dom instanceof PSSRefDomain) {
            PSSRefDomain d = (PSSRefDomain) dom;
            for (Map.Entry<String, PSSRefVal> entry : d.m_refs.entrySet()) {
                res.m_refs.put(entry.getKey(), entry.getValue());
            }
        }

        return res;
    }

    @Override
    public PSSRefDomain reduceDomainEqual(PSSVal val) {
        PSSRefDomain res = new PSSRefDomain();
        if (val instanceof PSSRefVal) {
            PSSRefVal r = (PSSRefVal) val;
            PSSRefVal ref = m_refs.get(r.getHierarchicalID());
            if (ref != null && ref.Equal(r).toBool()) {
                res.add(ref);
            }
        }
        return res;
    }

    @Override
    public PSSRefDomain reduceDomainNotEqual(PSSVal val) {
        PSSRefDomain res = new PSSRefDomain();
        if (val instanceof PSSRefVal) {
            PSSRefVal r = (PSSRefVal) val;
            for (PSSRefVal ref : m_refs.values()) {
                if (ref.NotEqual(r).toBool())
                    res.add(ref);
            }
        }
        return res;
    }

    @Override
    public String getText() {
        return "{ " + String.join(", ", m_refs.keySet()) + " }";
    }

    @Override
    public PSSRefDomain clone() {
        PSSRefDomain d = new PSSRefDomain();
        d.m_refs.putAll(m_refs);
        return d;
    }

    @Override
    public String toString() {
        return getText();
    }

}