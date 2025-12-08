import java.math.*;

public class PSSIntInst extends PSSInst implements PSSIScalarInst {

	PSSIntVal m_val;

	int m_width;

	boolean m_sign;

	PSSIntDomain m_domain; // Solution domain

	int m_BitSelect;	// Bit-selects

	public PSSIntInst(String id, boolean rand, int width, boolean sign) {
		super(id, "int", rand);
		m_val = new PSSIntVal(0, width, sign); // PSS v2.0 8.2.1: the default value of the bit and int types is 0
		m_width = width;
		m_sign = sign;
		m_domain = new PSSIntDomain(width, sign);
		m_BitSelect = -1;	// clear mask
	}

	public PSSIntDomain getInitDomain() {
		return new PSSIntDomain(m_width, m_sign);
	}

	public PSSVal toVal () {
		PSSVal ret = (0 <= m_BitSelect) ? m_val.extract(m_BitSelect, m_BitSelect) : m_val;
		m_BitSelect = -1;	// clear mask
		return ret;
	}

	@Override
	public PSSInst indexOf (PSSVal index) {
		int i = index.toInt();
		/** [9.6.1 Bit-selects]: It shall be illegal for a bit-select to access on out-of-bounds bit index. */
		if (i > m_width) {
			String errObj = "'" + getHierarchyId() + "[" + index.getText() + "]" + "'";
			String errMsg = "Bit-selects access out-of-bounds bit index.";
			String errExt = "sizeof(" + getHierarchyId() + ") = " + m_width;
			PSSMessage.Error("ILLEGAL USAGE", errObj + " " + errMsg + " //" + errExt);
		}

		m_BitSelect = i;	// set mask
		return this;
	}

	public boolean isAccessBitSelect () {
		return (0 <= m_BitSelect);
	}

	public PSSIntVal getBitSelect () {
		return new PSSIntVal(m_BitSelect);
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
			// PSSMessage.Debug("randomize iteration: " + getHierarchyId() + " = " + String.valueOf(retry));
			m_initialized = true;
		}
	}

	public void randomize_one() {
		BigInteger rand_val = m_domain.random().toBigInteger();
		m_val = new PSSIntVal(rand_val);
		if (PSSMessage.isPrintRandEn()) PSSMessage.Debug("randomize_one: '" + getHierarchyId() + "' = " + m_val.toInt());
	}

	public void assignDomain(PSSDomain domain) {
		m_domain = (PSSIntDomain) domain;
		if (m_domain.isEmpty()) {
			PSSMessage.Error("OVERCONSTRAINT", "'" + getHierarchyId() + "' is over-constrained");
		}
	}

	public void assign(PSSVal val) {
		m_initialized = true;
		BigInteger in_val = val.toBigInteger();
		PSSVal result = val;
		if (m_BitSelect != -1) {
			if (in_val.testBit(0)) {
				result = m_val.BitwiseOr(BigInteger.ONE.shiftLeft(m_BitSelect));
			} else {
				PSSVal mask = m_val.BitwiseAnd(BigInteger.ONE.shiftLeft(m_BitSelect));
				result = m_val.BitwiseXor(mask);
			}
			m_BitSelect = -1;	// clear mask
		}
		m_val = (PSSIntVal)result;
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
