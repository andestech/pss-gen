import java.math.*;

public class PSSIntInst extends PSSInst implements PSSIScalarInst {

	/** the default value of bit and int types */
	public static final PSSIntVal DEFAULT_VALUE = new PSSIntVal(0);

	PSSIntVal m_val;

	int m_width;

	boolean m_sign;

	PSSIntDomain m_domain; // Solution domain

	PSSIntVal m_BitSelect;	// Bit-selects

	public PSSIntInst(String id, boolean rand, int width, boolean sign) {
		super(id, "int", rand);
		m_val = DEFAULT_VALUE; // PSS v2.0 8.2.1: the default value of the bit and int types is 0
		m_width = width;
		m_sign = sign;
		m_domain = new PSSIntDomain(width, sign);
		m_BitSelect = null;
	}

	public PSSIntDomain getInitDomain() {
		return new PSSIntDomain(m_width, m_sign);
	}

	public PSSVal toVal () {
		if (null != m_BitSelect) {
			BigInteger bi_val = m_val.toBigInteger();
			int i_BitSelect = m_BitSelect.toInt();
			if (bi_val.testBit(i_BitSelect)) {
				m_BitSelect = null;	// Clear mask
				return new PSSIntVal(BigInteger.ONE);
			}
			else {
				m_BitSelect = null;	// Clear mask
				return new PSSIntVal(BigInteger.ZERO);
			}
		}
		else {
			m_BitSelect = null;	// Clear mask
			return m_val;
		}
	}

	@Override
	public PSSInst indexOf (PSSVal index) {
		/** [9.6.1 Bit-selects]: It shall be illegal for a bit-select to access on out-of-bounds bit index. */
		if (index.toInt() > m_width) {
			String errObj = "'" + getHierarchyId() + "[" + index.getText() + "]" + "'";
			String errMsg = "Bit-selects access out-of-bounds bit index.";
			String errExt = "sizeof(" + getHierarchyId() + ") = " + m_width;
			PSSMessage.Error("ILLEGAL USAGE", errObj + " " + errMsg + " //" + errExt);
		}

		m_BitSelect = new PSSIntVal(index.toBigInteger());
		return this;
	}

	public boolean isAccessBitSelect () {
		return (null != m_BitSelect) ? true : false;
	}

	public PSSIntVal getBitSelect () {
		return m_BitSelect;
	}

	public boolean validate() {
		if (!m_domain.InRange(m_val).toBool()) {
			return false;
		}
		return true;
	}

	public void randomize() {
		if (m_rand) {
			boolean validated = false;
			int retry = 0;

			while (!validated) {
				PSSRandom.limitRetry(retry);
				randomize_one();
				validated = validate();
				retry++;
			}
			m_initialized = true;
		}
	}

	public void randomize_one() {
		BigInteger rand_val = m_domain.random().toBigInteger();
		m_val = new PSSIntVal(rand_val);
		// PSSMessage.Debug("randomize_one: val = " + m_val.toInt());
	}

	public void assignDomain(PSSDomain domain) {
		m_domain = (PSSIntDomain) domain;
		if (m_domain.isEmpty()) {
			PSSMessage.Error("OVERCONSTRAINT", "'" + getHierarchyId() + "' is over-constrained");
		}
	}

	public void assign(PSSVal val) {
		m_initialized = true;
		BigInteger inVal = val.toBigInteger();
		if (null != m_BitSelect) {
			BigInteger bi_val = m_val.toBigInteger();
			int i_BitSelect = m_BitSelect.toInt();
			bi_val = bi_val.clearBit(i_BitSelect);
			if (inVal.compareTo(BigInteger.ZERO) != 0) {
				bi_val = bi_val.setBit(i_BitSelect);
			}
			m_val = new PSSIntVal(bi_val);
		}
		else {
			m_val = new PSSIntVal(inVal);
		}
		m_BitSelect = null;	// Clear mask
	}

	public String toTargetCode() {
		if (!m_initialized) {
			PSSMessage.Error("INTVAR-0001", "variable '" + getHierarchyId() + "' is not initialized");
		}
		PSSVal val = toVal();
		PSSIntVal intVal = new PSSIntVal(val.toBigInteger());
		return intVal.toTargetCode();
	}

	public void dump(String indent) {
		System.out.println(indent + m_id + " <" + m_type + ">" + "\"" + m_val.getText() + "\"");
	}

}
