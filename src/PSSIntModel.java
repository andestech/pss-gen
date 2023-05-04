import java.util.*;

public class PSSIntModel extends PSSModel {

	/** the default bit size for data type {@code int} */
	public static final int DEFAULT_INT_SIZE = 32;

	/** the default bit size for data type {@code bit} */
	public static final int DEFAULT_BIT_SIZE = 1;

	/** the bit size */
	int m_width;

	/** the signedness */
	boolean m_sign;

	public PSSIntModel(int width, boolean sign) {
		super(sign ? "int[" + width + "]" : "bit[" + width + "]");
		m_width = width;
		m_sign = sign;
	}

	public PSSInst declInst(String id, boolean rand) {
		PSSIntInst var = new PSSIntInst(id, rand, m_width, m_sign);
		return var;
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

}
