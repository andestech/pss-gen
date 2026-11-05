package pssgen.activity;

import java.util.*;

import pssgen.inst.PSSActionInst;
import pssgen.inst.PSSFlowRefInst;
import pssgen.inst.PSSInst;
import pssgen.inst.PSSScope;
import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;

public class PSSBindActivity extends PSSActivity {

	String m_bind_id;
	private ArrayList<String> m_items;


	public PSSBindActivity(String bind_id) {
		m_bind_id = bind_id;
		m_items = new ArrayList<String>();
	}

	public void addBindItem(String item) {
		m_items.add(item);
	}

	public void evalActivity(PSSInst action_inst) {
		PSSFlowRefInst src_inst = (PSSFlowRefInst) action_inst.findInstance(m_bind_id, PSSScope.Context.FLOW_REF);

		if (src_inst == null) {
			PSSMessage.Error("BIND-0001", "Cannot find source instance'" + m_bind_id + "'");
		}
		// Decleare Child Insts (input/out instance should be random)
		PSSModel type_model = src_inst.getTypeModel();
		PSSInst flowobj = type_model.declInst("", true);

		action_inst.addInst(flowobj);

		bindTraversals(src_inst, flowobj);

		for (int i=0; i<m_items.size(); i++) {
			String dst_id = m_items.get(i);
			PSSFlowRefInst dst_inst = (PSSFlowRefInst) action_inst.findInstance(dst_id, PSSScope.Context.FLOW_REF);
			if (dst_inst == null) {
				PSSMessage.Error("BIND-0002", "Cannot find destination instance'" + dst_id + "'");
			}
			bindTraversals(dst_inst, flowobj);
		}
	}

	/**
	 * Binds ref, and the same reference of every traversal instance its action handle has
	 * recorded: a bind statement names the handle, which each traversal of it shares.
	 *
	 * @param ref     a flow object reference of an action
	 * @param flowobj the flow object to bind
	 */
	private static void bindTraversals(PSSFlowRefInst ref, PSSInst flowobj) {
		ref.bind(flowobj);
		if (ref.getParent() instanceof PSSActionInst) {
			for (PSSActionInst t : ((PSSActionInst) ref.getParent()).getTraversals()) {
				PSSInst r = t.findInstanceUnder(ref.getId());
				if (r instanceof PSSFlowRefInst) {
					((PSSFlowRefInst) r).bind(flowobj);
				}
			}
		}
	}

	public void dump (String indent) {

		for (int i=0; i<m_items.size(); i++) {
			String item = m_items.get(i);
			System.out.println(indent + "bind " +  m_bind_id + " " + item);
		}
	}

	public final ArrayList<String> getItems() {
		return m_items;
	}
}
