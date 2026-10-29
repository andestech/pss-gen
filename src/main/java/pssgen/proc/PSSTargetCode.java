package pssgen.proc;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import pssgen.expr.PSSRefPathExpression;
import pssgen.expr.PSSRefPathParser;
import pssgen.inst.PSSEnumInst;
import pssgen.inst.PSSInst;
import pssgen.model.PSSExecKind;
import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;

public class PSSTargetCode extends PSSModel {

	PSSModel m_root;
	PSSExecKind m_exec_kind;
	String m_language;
	String m_code;
	/** parses the {{...}} references of the code when it is expanded */
	private final PSSRefPathParser m_ref_parser;

	public PSSTargetCode (PSSModel root, PSSExecKind exec_kind, String language, String code, PSSRefPathParser ref_parser) {
		super("");
		m_root = root;
		m_ref_parser = ref_parser;
		m_exec_kind = exec_kind;
		m_language = language;
		m_code = code.replace("\"\"\"", "");
	}

	public void execute_kind (PSSInst inst, PSSExecKind exec_kind) {
		if (exec_kind.equals(m_exec_kind)) {
			execute(inst);
		}
	}

	private void execute(PSSInst inst) {
		Pattern pattern = Pattern.compile("\\{\\{([\\w\\.\\[\\]]+)\\}\\}");

		int begin = 0;
		String residual = m_code;

		String result = "";

		while (residual.length() != 0) {
			Matcher m = pattern.matcher(residual);
			if (m.find()) {
				int start = m.start();
				int end = m.end();

				String prefix = residual.substring(0, start);
				String ref = m.group(1);

				PSSRefPathExpression ref_exp = m_ref_parser.fromString(m_root, ref, inst.run().log());

				PSSInst var = ref_exp.getInst(inst);

				if (var == null) {
					PSSMessage.Error("EXEC-0001", "Cannot find variable '" + ref + "'");
				}



				result = result.concat(prefix);
				result = result.concat(toTargetCode(var));
				residual = residual.substring(end);
			}
			else {
				result = result.concat(residual);
				residual= "";
			}
		}

		if (result.length() > 0) {
			inst.run().test().println(result);
		}

	}


	public void dump (String indent) {
		System.out.println(indent + "exec body " + m_language + " = \"\"\"");
		System.out.println(indent + "\t" + m_code);
		System.out.println(indent + "\"\"\";");
	}

	/**
	 * The text of a {@code {{...}}} reference. PSS 2.0 Section 22.5.3, Example 326,
	 * prints an enum field as the name of its item; ASM templates print the item's
	 * value, which the generated assembly uses.
	 */
	private String toTargetCode(PSSInst var) {
		boolean item = var instanceof PSSEnumInst && !((PSSEnumInst) var).isAccessBitSelect();
		String text = var.toTargetCode();
		if (item && !"ASM".equals(m_language)) {
			String name = ((PSSEnumInst) var).getItemName();
			if (name != null)
				return name;
		}
		return text;
	}
}
