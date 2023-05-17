import java.util.*;

public class PSSComponentModel extends PSSModel {

	PSSComponentDataDeclListModel m_data_list;
	public ArrayList<PSSExecBlock> m_exec_list;

	public PSSComponentModel(String id) {
		super(id);
		m_data_list = new PSSComponentDataDeclListModel();
		m_exec_list = new ArrayList<PSSExecBlock>();
	}
	public void addComponentDataDecl(PSSComponentDataDeclModel data_decl) {
		m_data_list.add(data_decl);
	}
	public void addExecBlock (PSSExecBlock block) {
		String kind = block.getKind();
		if (kind.equals("init_up") || kind.equals("init_down")) {
			m_exec_list.add(block);
			block.m_parent = this;
		}
		else if (kind.equals("header") || kind.equals("declaration") || kind.equals("body")) {
			PSSMessage.Error("ACTION", kind + " is only valid in action");
		}
		else if (kind.equals("pre_solve") || kind.equals("post_solve")) {
			PSSMessage.Error("ACTION", kind + " is only valid in action");
		}
		else if (kind.equals("run_start") || kind.equals("run_end")) {
			PSSMessage.Error("ACTION", kind + " is only valid in action");
		}
	}

	@Override
	public PSSComponentInst declInst(boolean rand) {
		return (PSSComponentInst) super.declInst(rand);
	}

	@Override
	public PSSComponentInst declInst(String id, boolean rand) {
		PSSComponentInst comp = new PSSComponentInst(id, m_id, this);

		m_data_list.declInst(comp);
		declEnumInst(comp);

		return comp;
	}

	public void init_up(PSSInst inst) {
		for (int i=0; i<m_exec_list.size(); i++) {
			PSSExecBlock block = m_exec_list.get(i);
			if (block.m_kind.equals("init_up")) {
				block.eval(inst);
			}
		}
	}
	public void init_down(PSSInst inst) {
		for (int i=0; i<m_exec_list.size(); i++) {
			PSSExecBlock block = m_exec_list.get(i);
			if (block.m_kind.equals("init_down")) {
				block.eval(inst);
			}
		}
	}

	public void dump (String indent) {
		System.out.println(indent + "component " +  m_id + "{");

		m_data_list.dump(indent + "\t");

		for (int i=0; i<children.size(); i++) {
			PSSModel node = children.get(i);
			node.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
}
