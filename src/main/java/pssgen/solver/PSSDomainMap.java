package pssgen.solver;

import java.util.*;
import java.util.LinkedHashMap;

import pssgen.inst.PSSInst;
import pssgen.util.PSSLog;

public class PSSDomainMap {
	/** insertion-ordered, because PSSInst keys hash by identity (see PSSSolver) */
	public LinkedHashMap<PSSInst, PSSDomain> m_map;

	public PSSDomainMap() {
		m_map = new LinkedHashMap<PSSInst, PSSDomain>();
	}

	public LinkedHashMap<PSSInst, PSSDomain> getMap() {
		return m_map;
	}

	public void add(PSSInst id, PSSDomain domain) {
		m_map.put(id, domain);
	}

	public PSSDomainMap union(PSSDomainMap dom) {
		/** @brief union two domain maps
		 */
		PSSDomainMap result = new PSSDomainMap();

		LinkedHashMap<PSSInst, PSSDomain> dom_map = dom.getMap();

		for (PSSInst var0: m_map.keySet()) {
			PSSDomain dom0 = m_map.get(var0);
			for (PSSInst var1: dom_map.keySet()) {
				PSSDomain dom1 = dom_map.get(var1);
				if (var0 == var1) {
					dom0 = dom0.union(dom1);
				}
			}
			if (dom0 != null) {
				result.add(var0, dom0);
			}
		}
		for (PSSInst var0: dom_map.keySet()) {
			PSSDomain dom0 = dom_map.get(var0);
			for (PSSInst var1: m_map.keySet()) {
				PSSDomain dom1 = m_map.get(var1);
				if (var0 == var1) {
					dom0 = null;
				}
			}
			if (dom0 != null) {
				result.add(var0, dom0);
			}
		}

		return result;
	}
	public PSSDomainMap join(PSSDomainMap dom) {
		/** @brief join two domain maps
		 */
		PSSDomainMap result = new PSSDomainMap();

		LinkedHashMap<PSSInst , PSSDomain> dom_map = dom.getMap();

		for (PSSInst var0: m_map.keySet()) {
			PSSDomain dom0 = m_map.get(var0);
			for (PSSInst var1: dom_map.keySet()) {
				PSSDomain dom1 = dom_map.get(var1);
				if (var0.equals(var1)) {
					dom0 = dom0.join(dom1);
				}
			}
			if (dom0 != null) {
				result.add(var0, dom0);
			}
		}
		for (PSSInst var0: dom_map.keySet()) {
			PSSDomain dom0 = dom_map.get(var0);
			for (PSSInst var1: m_map.keySet()) {
				PSSDomain dom1 = m_map.get(var1);
				if (var0 == var1) {
					dom0 = null;
				}
			}
			if (dom0 != null) {
				result.add(var0, dom0);
			}
		}

		return result;
	}

	public void dump(String indent, PSSLog log) {
		for (PSSInst var : m_map.keySet()) {
			PSSDomain val = m_map.get(var);
			log.Debug(indent + var.m_id + ": " + val.getText());
		}
	}
}
