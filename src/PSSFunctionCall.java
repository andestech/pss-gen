import java.util.*;

public class PSSFunctionCall {
	String m_path;
	String m_id;

	PSSFunctionCall(String path, String id) {
		m_path = path;
		m_id = id;
	}

	public PSSVal eval(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".eval() is not implemented");
		return null;
	}
}

