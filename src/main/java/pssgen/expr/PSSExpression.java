package pssgen.expr;

import java.util.ArrayList;

import pssgen.inst.PSSEnumInst;
import pssgen.inst.PSSInst;
import pssgen.model.PSSDataTypeModel;
import pssgen.model.PSSEnumModel;
import pssgen.model.PSSModel;
import pssgen.solver.PSSDomainMap;
import pssgen.util.PSSMessage;
import pssgen.value.PSSIntVal;
import pssgen.value.PSSVal;

public class PSSExpression {

	public PSSExpression() {
	}

	/**
	 * Returns {@code true} if this instance is random variable.
	 * Warning user if both sides of constraint expression are not random variables.
	 *
	 * @return {@code true} if this instance is random variable
	 */
	public boolean isRandomable (PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".isRandomable() is not implemented");
		return false;
	}

	/**
	 * Override left expression.
	 * CAUTION: this API carries potential risks. Ensure you understand the implications before use.
	 * 
	 * @param left
	 * @return void
	 */
	public void setLeftExpression (PSSExpression left) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".setLeftExpression() is not implemented");
	}

	/**
	 * Infers the domains of variables that satisfy this expression.
	 *
	 * @param var
	 * @return domains successfully inferred
	 */
	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".deduceDomain() is not implemented");
		return null;
	}

	/**
	 * Evaluates this expression.
	 *
	 * @param var
	 * @return the evaluation result
	 */
	public PSSVal eval(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".eval() is not implemented");
		return null;
	}

	/**
	 * Returns all instances from left to right (if exist) expression.
	 *
	 * @param var
	 * @return all instances of the expression, or {@code null} if the expression
	 *         is not a variable
	 */
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		return ret;
	}

	/**
	 * Returns the instance of this expression.
	 *
	 * @param var
	 * @return the instance of this expression, or {@code null} if this expression
	 *         is not a variable
	 */
	public PSSInst getInst(PSSInst var) {
		return null;
	}

	/**
	 * Returns the name, when this expression is one name without qualifier, path, index,
	 * call or select.
	 *
	 * @return the name, or null
	 */
	public String bareName() {
		return null;
	}

	/**
	 * Returns the field this expression reads, when the path has no index, call or
	 * select, so that resolving it has no side effect.
	 *
	 * @param var the instance the expression is evaluated in
	 * @return the field, or null
	 */
	public PSSInst plainInst(PSSInst var) {
		return null;
	}

	/**
	 * PSS 2.0 Section 21.3 a): in an expression whose expected type is an enum type, a
	 * name is first searched among the items of that type. Returns the item's value as
	 * an expression when e is a bare name and expected is an enum field with an item of
	 * that name; else e.
	 *
	 * @param e        an expression
	 * @param expected the field whose type is the expected type of e, or null
	 * @return the expression to evaluate for e
	 */
	public static PSSExpression expecting(PSSExpression e, PSSInst expected) {
		String name = e.bareName();
		if (name != null && expected instanceof PSSEnumInst) {
			PSSVal item = ((PSSEnumInst) expected).itemValue(name);
			if (item != null)
				return new PSSPrimaryExpression(item);
		}
		return e;
	}

	/**
	 * As {@link #expecting(PSSExpression, PSSInst)}, where the expected type is type.
	 *
	 * @param e    an expression
	 * @param type the expected type of e, or null
	 * @return the expression to evaluate for e
	 */
	public static PSSExpression expecting(PSSExpression e, PSSModel type) {
		String name = e.bareName();
		if (name == null)
			return e;
		if (type instanceof PSSDataTypeModel)
			type = ((PSSDataTypeModel) type).resolve();
		if (type instanceof PSSEnumModel && ((PSSEnumModel) type).hasEnumItem(name))
			return new PSSPrimaryExpression(new PSSIntVal(((PSSEnumModel) type).getEnumItemValue(name)));
		return e;
	}

	/**
	 * As {@link #expecting(PSSExpression, PSSInst)}, where the expected type is the type
	 * of the field that other reads.
	 */
	public static PSSExpression expecting(PSSExpression e, PSSExpression other, PSSInst var) {
		if (e.bareName() == null)
			return e;
		return expecting(e, other.plainInst(var));
	}

	/**
	 * Returns the text representation of this expression.
	 *
	 * @return the text representation of this expression
	 */
	public String getText() {
		return "";
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

	/** Returns this expression as a comparison 'left op right', or null when it is not one. */
	public PSSRelation relation() {
		return null;
	}
}
