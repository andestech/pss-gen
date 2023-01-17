import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PSSTargetCode extends PSSModel {
	
	String m_exec_kind;
	String m_language;
	String m_code;

	public PSSTargetCode (String exec_kind, String language, String code) {
		super("");
		m_exec_kind = exec_kind;
		m_language = language;
		m_code = code.replace("\"\"\"", "");
	}

	public void execute_kind (PSSInst inst, String exec_kind) {
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

				PSSRefPathExpression ref_exp = new PSSRefPathExpression(ref);

				PSSInst var = ref_exp.getInst(inst);

				if (var == null) {
					PSSMessage.Error("EXEC-0001", "Cannot find variable '" + ref + "'");
				}



				result = result.concat(prefix);
				result = result.concat(var.toTargetCode());
				residual = residual.substring(end);
			}
			else {
				result = result.concat(residual);
				residual= "";
			}
		}

		if (result.length() > 0) {
			PSSTest.println(result);
		}
	}


	public void dump (String indent) {
		System.out.println(indent + "exec body " + m_language + " = \"\"\"");
		System.out.println(indent + "\t" + m_code);
		System.out.println(indent + "\"\"\";");
	}
}
