import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class PSSRefPathExpression extends PSSExpression {
	String m_type_identifier_elems;
	PSSMemberPathElemExpression m_static_ref_path;
	PSSHierarchicalIDExpression m_hierarchical_id;
	PSSExpression m_bit_slice_from = null;
	PSSExpression m_bit_slice_to = null;

	public PSSRefPathExpression(String type_identifier_elems, PSSMemberPathElemExpression static_ref_path,
			PSSHierarchicalIDExpression hierarchical_id, PSSExpression bit_slice_from, PSSExpression bit_slice_to) {
		m_type_identifier_elems = type_identifier_elems;
		m_static_ref_path = static_ref_path;
		m_hierarchical_id = hierarchical_id;
		m_bit_slice_from = bit_slice_from;
		m_bit_slice_to = bit_slice_to;
	}

	public static PSSRefPathExpression fromString(PSSModel root, String str) {
		PSSRefPathExpression res = null;

		try {
			InputStream is = new ByteArrayInputStream(str.getBytes());
			CharStream input = CharStreams.fromStream(is);
			PSSLexer lexer = new PSSLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PSSParser parser = new PSSParser(tokens);

			parser.removeErrorListeners();
			parser.addErrorListener(new VerboseListener());

			PSSGenVisitor eval = new PSSGenVisitor();
			eval.root = root;
			eval.visit(parser.ref_path());
			res = (PSSRefPathExpression) eval.exp_stack.pop();
		} catch (IOException e) {
			PSSMessage.Fatal("Failed parsing " + str);
		}

		return res;
	}

	@Override
	public PSSInst getInst(PSSInst var) {
		if (m_type_identifier_elems != null && !m_type_identifier_elems.equals(""))
			PSSMessage.Fatal("[" + getClass().getName() + "] type_identifier_elems is not implemented");

		PSSInst inst = m_static_ref_path.getInst(var);
		if (m_hierarchical_id != null)
			inst = m_hierarchical_id.getInst(inst);
		if (m_bit_slice_from != null && m_bit_slice_to != null)
			PSSMessage.Fatal("[" + getClass().getName() + "] bit_slice is not implemented");

		return inst;
	}

	@Override
	public PSSVal eval(PSSInst var) {
		if (m_type_identifier_elems != null && !m_type_identifier_elems.equals(""))
			PSSMessage.Fatal("[" + getClass().getName() + "] type_identifier_elems is not implemented");

		PSSInst inst = m_static_ref_path.getInst(var);
		PSSVal res = null;
		if (m_hierarchical_id == null)
			res = inst.toVal();
		else
			res = m_hierarchical_id.eval(inst);
		if (m_bit_slice_from != null && m_bit_slice_to != null)
			PSSMessage.Fatal("[" + getClass().getName() + "] bit_slice is not implemented");

		return res;
	}

	@Override
	public String getText() {
		return (m_type_identifier_elems == null ? "" : m_type_identifier_elems) + m_static_ref_path.getText()
				+ (m_hierarchical_id == null ? "" : "." + m_hierarchical_id.getText())
				+ (m_bit_slice_from == null || m_bit_slice_to == null ? ""
						: "[" + m_bit_slice_from.getText() + ":" + m_bit_slice_to.getText());
	}

	@Override
	public String toString() {
		return getText();
	}

}
