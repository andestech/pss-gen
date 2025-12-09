import java.util.*;

public class PSSIntModel extends PSSModel {

	/** the default bit size for data type {@code int} */
	public static final int DEFAULT_INT_SIZE = 65;

	/** the default bit size for data type {@code bit} */
	public static final int DEFAULT_BIT_SIZE = 1;

	/** the default signedness for unsized unbased decimal numbers */
	public static final boolean DEFAULT_DECIMAL_SIGNEDNESS = true;

	/** the default signedness for unsized unbased octal numbers */
	public static final boolean DEFAULT_OCTAL_SIGNEDNESS = true;

	/** the default signedness for unsized unbased decimal numbers */
	public static final boolean DEFAULT_HEXADECIMAL_SIGNEDNESS = false;

	/** the bit size */
	int m_width;

	/** the signedness */
	boolean m_sign;

	/** the default type model for unsized unbased decimal numbers */
	private static PSSIntModel m_default_dec_model = null;

	public PSSIntModel(int width, boolean sign) {
		super(sign ? "int[" + width + "]" : "bit[" + width + "]");
		m_width = width;
		m_sign = sign;
	}

	@Override
	public PSSInst declInst(String id, boolean rand) {
		PSSIntInst var = new PSSIntInst(id, rand, m_width, m_sign);
		return var;
	}

	/**
	 * Returns the bit size of this integer type.
	 *
	 * @return the bit size of this integer type
	 */
	public int getSize() {
		return m_width;
	}

	/**
	 * Returns the signedness of this integer type.
	 *
	 * @return the signedness of this integer type
	 */
	public boolean isSigned() {
		return m_sign;
	}

	@Override
	public String getText() {
		if (m_sign) {
			if (m_width == DEFAULT_INT_SIZE) {
				return "int";
			} else {
				return "int [" + m_width + "]";
			}
		} else {
			if (m_width == DEFAULT_BIT_SIZE) {
				return "bit";
			} else {
				return "bit [" + m_width + "]";
			}
		}
	}

	/**
	 * Returns the default model for unsized unbased decimal integers.
	 *
	 * @return the default model for decimal integers
	 */
	public static PSSIntModel getDefaultDecimalModel() {
		if (m_default_dec_model == null)
			m_default_dec_model = new PSSIntModel(DEFAULT_INT_SIZE, true);
		return m_default_dec_model;
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		return model instanceof PSSIntModel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PSSIntModel) {
			PSSIntModel m = (PSSIntModel) obj;
			return m_sign == m.m_sign && m_width == m.m_width;
		}
		return false;
	}

}
