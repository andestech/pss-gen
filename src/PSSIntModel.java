import java.util.*;

public class PSSIntModel extends PSSModel {
	int m_width;
	boolean m_sign;

	public PSSIntModel (int width, boolean sign) {
		super("bit[" + width + "]");
		m_width = width;
		m_sign = sign;
	}

	public PSSInst declInst(String id, boolean rand) {
		PSSIntInst var = new PSSIntInst(id, rand, m_width, m_sign);
		return var;
	}

	public String getText() {
		if (m_sign) {
			if (m_width == 32) {
				return new String("int");
			}
			else {
				return new String("int [" + m_width + "]");
			}
			
		}
		else {
			if (m_width == 1) {
				return new String("bit");
			}
			else {
				return new String("bit [" + m_width + "]");
			}
		}
	}
}
