import java.util.*;
import java.math.*;

public class PSSVal {

	public PSSVal() {
		;
	}

	public String getText() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getText is not implemented");
		return "";
	}
	public String toTargetCode() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toTargetCode is not implemented");
		return "";
	}
	public int toInt() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toInt is not implemented");
		return 0;
	}

	public boolean isRangeVal() {
		return false;
	}
	public boolean isBound() {
		return true;
	}
	public PSSVal join(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::join is not implemented");
		return null;
	}

	public BigInteger toBigInteger() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toBigInteger is not implemented");
		return null;
	}
	public String toString() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toString is not implemented");
		return "";
	}
	public boolean toBool() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toBool is not implemented");
		return false;
	}
	public PSSVal randomIn() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::randomIn is not implemented");
		return null;
	}

	// Binary Operators
	public PSSBoolVal Equal(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Equal is not implemented");
		return null;
	}
	public PSSBoolVal NotEqual(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::NotEqual is not implemented");
		return null;
	}
	public PSSBoolVal GreaterThan(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::GreaterThan is not implemented");
		return null;
	}
	public PSSBoolVal GreaterEqual(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::GreaterEqual is not implemented");
		return null;
	}
	public PSSBoolVal LessEqual(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LessEqual is not implemented");
		return null;
	}
	public PSSBoolVal LessThan(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LessThan is not implemented");
		return null;
	}
	public PSSVal Add(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Add is not implemented");
		return null;
	}
	public PSSVal Sub(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Sub is not implemented");
		return null;
	}
	public PSSVal Mul(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Mul is not implemented");
		return null;
	}
	public PSSVal Div(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Div is not implemented");
		return null;
	}
	public PSSVal Mod(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Mod is not implemented");
		return null;
	}
	public PSSVal LeftShift(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LeftShift is not implemented");
		return null;
	}
	public PSSVal RightShift(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::RightShift is not implemented");
		return null;
	}
	public PSSBoolVal LogicalAnd(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LogicalAnd is not implemented");
		return null;
	}
	public PSSBoolVal LogicalOr(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LogicalOr is not implemented");
		return null;
	}
	public PSSBoolVal InRange(PSSVal lhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::InRange is not implemented");
		return null;
	}
	public PSSBoolVal LogicalNot() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LogicalOr is not implemented");
		return null;
	}
	public PSSVal BitwiseAnd(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::BitwiseAnd is not implemented");
		return null;
	}
	public PSSVal BitwiseOr(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::BitwiseOr is not implemented");
		return null;
	}
	public PSSVal BitwiseNot() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::BitwiseNot is not implemented");
		return null;
	}
	public PSSVal UnaryMinus() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::UnaryMinus is not implemented");
		return null;
	}
	// 
	public PSSVal getMin() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getMin is not implemented");
		return null;
	}
	public PSSVal getMax() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getMax is not implemented");
		return null;
	}
	public PSSVal indexOf(int index) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::indexOf is not implemented");
		return null;
	}
	// Adjust Range
	public PSSVal reduceLessEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceLessEqual is not implemented");
		return null;
	}
	public PSSVal reduceGreaterEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceGreaterEqual is not implemented");
		return null;
	}
}
