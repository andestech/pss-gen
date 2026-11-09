package pssgen.activity;

import java.util.*;

import pssgen.inst.PSSInst;
import pssgen.model.PSSModel;

public abstract class PSSActivity extends PSSModel {

	public PSSActivity() {
		super("");
	}

	public abstract void evalActivity(PSSInst inst);

	public abstract void dump(String indent);
}
