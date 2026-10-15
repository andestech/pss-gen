import java.util.*;

public class PSSBindActivity extends PSSActivity {

	String m_bind_id;
	public ArrayList<String> m_items;


	public PSSBindActivity(String bind_id) {
		m_bind_id = bind_id;
		m_items = new ArrayList<String>();
	}

	public void addBindItem(String item) {
		m_items.add(item);
	}

	public void evalActivity(PSSInst action_inst) {
		PSSFlowRefInst src_inst = (PSSFlowRefInst) action_inst.findInstance(m_bind_id);

		if (src_inst == null) {
			PSSMessage.Error("BIND-0001", "Cannot find source instance'" + m_bind_id + "'");
		}
		// Decleare Child Insts (input/out instance should be random)
		PSSModel type_model = src_inst.getTypeModel();
		PSSInst flowobj = type_model.declInst("", true);

		action_inst.addInst(flowobj);

		src_inst.bind(flowobj);

		for (int i=0; i<m_items.size(); i++) {
			String dst_id = m_items.get(i);
			PSSFlowRefInst dst_inst = (PSSFlowRefInst) action_inst.findInstance(dst_id);
			if (dst_inst == null) {
				PSSMessage.Error("BIND-0002", "Cannot find destination instance'" + dst_id + "'");
			}
			dst_inst.bind(flowobj);
		}
	}

	public void dump (String indent) {

		for (int i=0; i<m_items.size(); i++) {
			String item = m_items.get(i);
			System.out.println(indent + "bind " +  m_bind_id + " " + item);
		}
	}
}
