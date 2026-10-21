package pssgen.model;

import pssgen.inst.PSSBoolInst;
import pssgen.inst.PSSInst;

/**
 * A {@code PSSBoolModel} is the type model for Booleans. {@link #getInstance()} is
 * the type of every Boolean value; each run's frontend declares fields with a model
 * of its own, because declaring a field adds its type model to the declaring scope.
 */
public class PSSBoolModel extends PSSModel {

	/** never added to a model tree or otherwise modified, so all runs can share it */
	private static final PSSBoolModel VALUE_TYPE = new PSSBoolModel();

	public PSSBoolModel() {
		super("bool");
	}

	@Override
	public PSSInst declInst(String id, boolean rand) {
		PSSBoolInst var = new PSSBoolInst(id, rand);
		return var;
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		return model instanceof PSSBoolModel;
	}

	@Override
	public String getText() {
		return m_id;
	}

	/**
	 * Returns the type model of Boolean values.
	 *
	 * @return the type model of Boolean values
	 */
	public static PSSBoolModel getInstance() {
		return VALUE_TYPE;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PSSBoolModel;
	}

}
