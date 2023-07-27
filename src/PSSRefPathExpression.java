import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * A {@code PSSRefPathExpression} is a simple or hierarchical reference.
 */
public class PSSRefPathExpression extends PSSExpression {

	/** the optional type identifier */
	String m_type_identifier_elems;

	/** the static_ref_path plus an optional hierarchical_id */
	PSSMemberPathElemExpression m_ref_path;

	/** the optional beginning index of bit slice */
	PSSExpression m_bit_slice_from = null;

	/** the optional ending index of bit slice */
	PSSExpression m_bit_slice_to = null;

	/**
	 * Constructs this reference.
	 *
	 * @param type_identifier_elems the optional type identifier
	 * @param ref_path              the static reference path
	 * @param bit_slice_from        the optional beginning index of bit slice
	 * @param bit_slice_to          the optional ending index of bit slice
	 */
	public PSSRefPathExpression(String type_identifier_elems, PSSMemberPathElemExpression ref_path,
			PSSExpression bit_slice_from, PSSExpression bit_slice_to) {
		if (type_identifier_elems != null) {
			if (type_identifier_elems.endsWith("::"))
				type_identifier_elems = type_identifier_elems.substring(0, type_identifier_elems.length() - 2);
		}
		m_type_identifier_elems = type_identifier_elems;
		m_ref_path = ref_path;
		m_bit_slice_from = bit_slice_from;
		m_bit_slice_to = bit_slice_to;
	}

	/**
	 * Parses a reference from a string.
	 *
	 * @param root the model where the string is evaluated
	 * @param str  a string to be parsed
	 * @return the reference denoted by the string
	 */
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
		PSSInst inst = null;
		if (m_type_identifier_elems != null && !m_type_identifier_elems.equals("")) {
			PSSModel m = var.getTypeModel().findDeclaration(m_type_identifier_elems);
			inst = m_ref_path.getInst(m, var);
		} else {
			inst = m_ref_path.getInst(var);
		}

		if (m_bit_slice_from != null && m_bit_slice_to != null)
			PSSMessage.Fatal("[" + getClass().getName() + "] bit_slice is not implemented");

		return inst;
	}

	@Override
	public PSSVal eval(PSSInst var) {
		PSSVal res = null;
		if (m_type_identifier_elems != null && !m_type_identifier_elems.equals("")) {
			PSSModel m = var.getTypeModel().findDeclaration(m_type_identifier_elems);
			res = m_ref_path.eval(m, var);
		} else {
			res = m_ref_path.eval(var);
		}
		if (m_bit_slice_from != null && m_bit_slice_to != null)
			PSSMessage.Fatal("[" + getClass().getName() + "] bit_slice is not implemented");

		return res;
	}

	@Override
	public String getText() {
		return (m_type_identifier_elems == null || m_type_identifier_elems.equals("") ? ""
				: (m_type_identifier_elems + "::")) + m_ref_path.getText()
				+ (m_bit_slice_from == null || m_bit_slice_to == null ? ""
						: "[" + m_bit_slice_from.getText() + ":" + m_bit_slice_to.getText());
	}

	@Override
	public String toString() {
		return getText();
	}

}
