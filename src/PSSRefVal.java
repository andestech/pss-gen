import java.math.BigInteger;

/**
 * {@code PSSRefVal} is a reference to a {@code PSSInst}.
 */
public class PSSRefVal extends PSSVal {

    PSSInst m_inst = null;

    public PSSRefVal(PSSInst inst) {
		super(inst.getTypeModel());
        m_inst = inst;
    }

    public PSSInst getInst() {
        return m_inst;
    }

    @Override
    public String getText() {
        return m_inst.toVal().getText();
	}

    @Override
	public String toTargetCode() {
        return m_inst.toVal().toTargetCode();
	}

    @Override
	public boolean isRangeVal() {
        return m_inst.toVal().isRangeVal();
	}

    @Override
	public boolean isBound() {
		return m_inst.toVal().isBound();
	}

    @Override
	public PSSVal join(PSSVal val) {
        return m_inst.toVal().join(val);
	}

    @Override
	public BigInteger toBigInteger() {
        return m_inst.toVal().toBigInteger();
	}

    @Override
	public int toInt() {
        return m_inst.toVal().toInt();
	}

    @Override
	public String toStr() {
        return m_inst.toVal().toStr();
	}

    @Override
	public boolean toBool() {
		return m_inst.toVal().toBool();
	}

    @Override
	public PSSVal randomIn() {
		return m_inst.toVal().randomIn();
	}

    @Override
	public PSSBoolVal Equal(PSSVal rhs) {
        return m_inst.toVal().Equal(rhs);
	}

    @Override
	public PSSBoolVal NotEqual(PSSVal rhs) {
		return m_inst.toVal().NotEqual(rhs);
	}

    @Override
	public PSSBoolVal GreaterThan(PSSVal rhs) {
		return m_inst.toVal().GreaterThan(rhs);
	}

    @Override
	public PSSBoolVal GreaterEqual(PSSVal rhs) {
		return m_inst.toVal().GreaterEqual(rhs);
	}

    @Override
	public PSSBoolVal LessEqual(PSSVal rhs) {
		return m_inst.toVal().LessEqual(rhs);
	}

    @Override
	public PSSBoolVal LessThan(PSSVal rhs) {
		return m_inst.toVal().LessThan(rhs);
	}

    @Override
	public PSSVal Add(PSSVal rhs) {
		return m_inst.toVal().Add(rhs);
	}

    @Override
	public PSSVal Sub(PSSVal rhs) {
		return m_inst.toVal().Sub(rhs);
	}

    @Override
	public PSSVal Mul(PSSVal rhs) {
		return m_inst.toVal().Mul(rhs);
	}

    @Override
	public PSSVal Div(PSSVal rhs) {
		return m_inst.toVal().Div(rhs);
	}

    @Override
	public PSSVal Mod(PSSVal rhs) {
		return m_inst.toVal().Mod(rhs);
	}

    @Override
	public PSSVal LeftShift(PSSVal rhs) {
		return m_inst.toVal().LeftShift(rhs);
	}

    @Override
	public PSSVal RightShift(PSSVal rhs) {
		return m_inst.toVal().RightShift(rhs);
	}

    @Override
	public PSSBoolVal LogicalAnd(PSSVal rhs) {
		return m_inst.toVal().LogicalAnd(rhs);
	}

    @Override
	public PSSBoolVal LogicalOr(PSSVal rhs) {
		return m_inst.toVal().LogicalOr(rhs);
	}

    @Override
	public PSSBoolVal InRange(PSSVal lhs) {
		return m_inst.toVal().InRange(lhs);
	}

    @Override
	public PSSBoolVal LogicalNot() {
		return m_inst.toVal().LogicalNot();
	}

    @Override
	public PSSVal BitwiseAnd(PSSVal rhs) {
		return m_inst.toVal().BitwiseAnd(rhs);
	}

    @Override
	public PSSVal BitwiseOr(PSSVal rhs) {
		return m_inst.toVal().BitwiseOr(rhs);
	}

    @Override
	public PSSVal BitwiseNot() {
		return m_inst.toVal().BitwiseNot();
	}

    @Override
	public PSSVal UnaryMinus() {
		return m_inst.toVal().UnaryMinus();
	}

    @Override
	public PSSVal getMin() {
		return m_inst.toVal().getMin();
	}

    @Override
	public PSSVal getMax() {
		return m_inst.toVal().getMax();
	}

    @Override
	public PSSVal indexOf(PSSVal index) {
		return m_inst.toVal().indexOf(index);
	}

    @Override
	public PSSVal reduceLessEqual(PSSVal val) {
		return m_inst.toVal().reduceLessEqual(val);
	}

    @Override
	public PSSVal reduceGreaterEqual(PSSVal val) {
		return m_inst.toVal().reduceGreaterEqual(val);
	}

}
