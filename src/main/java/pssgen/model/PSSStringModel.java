package pssgen.model;

import pssgen.inst.PSSInst;
import pssgen.inst.PSSStringInst;

/**
 * A {@code PSSStringModel} is the type model for strings. {@link #getInstance()} is
 * the type of every string value; each run's frontend declares fields with a model
 * of its own, because declaring a field adds its type model to the declaring scope.
 */
public class PSSStringModel extends PSSModel {

	/** never added to a model tree or otherwise modified, so all runs can share it */
	private static final PSSStringModel VALUE_TYPE = new PSSStringModel();

	public PSSStringModel() {
		super("string");
	}

	@Override
	public PSSInst declInst(String id, boolean rand) {
		PSSStringInst var = new PSSStringInst(id, rand);
		return var;
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		return model instanceof PSSStringModel;
	}

	@Override
	public String getText() {
		return m_id;
	}

	/**
	 * Returns the type model of string values.
	 *
	 * @return the type model of string values
	 */
	public static PSSStringModel getInstance() {
		return VALUE_TYPE;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PSSStringModel;
	}

}
