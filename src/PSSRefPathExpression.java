import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;

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
		if (m_bit_slice_from != null ^ m_bit_slice_to != null)
			PSSMessage.Fatal("[" + getClass().getName() + "] bit_slice should have both msb and lsb");
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		return getInst(var).isRandomable();
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
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.add(getInst(var));
		return ret;
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
		return inst;
	}

	@Override
	public PSSVal eval(PSSInst var) {
		PSSVal res = evalOriVal(var);
		if (m_bit_slice_from != null && m_bit_slice_to != null) {
			int msb = m_bit_slice_to.eval(var).toInt();
			int lsb = m_bit_slice_from.eval(var).toInt();
			res = res.extract(msb, lsb);
		}
		return res;
	}

	public void assign (PSSInst var, PSSVal val) {
		PSSInst inst = getInst(var);

		if (val instanceof PSSIntVal || val instanceof PSSBoolVal) {
			PSSVal val_int = new PSSIntVal(val.toBigInteger());
			if (m_bit_slice_from != null && m_bit_slice_to != null) {
				int msb = m_bit_slice_to.eval(var).toInt();
				int lsb = m_bit_slice_from.eval(var).toInt();
				PSSVal ori_val = evalOriVal(var);
				PSSVal invert_mask = ori_val.extract(msb, lsb);
				invert_mask = invert_mask.LeftShift(lsb);
				ori_val = ori_val.BitwiseXor(invert_mask);
	
				val_int = val_int.extract(msb-lsb,0);
				val_int = val_int.LeftShift(lsb);
				val_int = ori_val.BitwiseOr(val_int);
			}
			inst.assign(val_int);
		} else {
			inst.assign(val);
		}
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

	private PSSVal evalOriVal (PSSInst var) {
		PSSVal res = null;
		if (m_type_identifier_elems != null && !m_type_identifier_elems.equals("")) {
			PSSModel m = var.getTypeModel().findDeclaration(m_type_identifier_elems);
			res = m_ref_path.eval(m, var);
		} else {
			res = m_ref_path.eval(var);
		}
		return res;
	}

}
