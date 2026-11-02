package pssgen.inst;

import pssgen.model.PSSDataInstModel;
import pssgen.model.PSSModel;

/**
 * A static constant of a type or package (PSS 2.0 Sections 8.1.1, 21.1.1.1 b)). Its
 * initializer is a constant expression (PSS 2.0 Section 9.2) that may name enum items and
 * other static constants, which exist only once the model is instantiated, so the
 * constant takes its value when it is first looked up.
 */
public class PSSStaticConstInst extends PSSInst {

	private final PSSModel m_scope;
	private final PSSDataInstModel m_decl;
	private final PSSModel m_data_type;
	private PSSInst m_value;
	private boolean m_evaluating;

	/**
	 * Constructs a static constant.
	 *
	 * @param scope     the type or package that declares it
	 * @param decl      its name and initializer
	 * @param data_type its data type
	 */
	public PSSStaticConstInst(PSSModel scope, PSSDataInstModel decl, PSSModel data_type) {
		super(decl.getId(), "", false);
		m_scope = scope;
		m_decl = decl;
		m_data_type = data_type;
	}

	@Override
	public PSSInst resolveStatic() {
		if (m_value == null && !m_evaluating) {
			// The names of the initializer are static members of the declaring scope and
			// of the scopes around it; a constant defined by itself stays undefined.
			m_evaluating = true;
			PSSNamespaceInst context = new PSSNamespaceInst("", m_scope);
			m_decl.eval(context, m_data_type, false);
			PSSInst value = context.getInsts().get(context.getInsts().size() - 1);
			value.setReadOnly(true);
			m_value = value;
			m_evaluating = false;
		}
		return m_value;
	}
}
