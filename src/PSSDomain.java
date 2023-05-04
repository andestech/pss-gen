import java.util.*;

public class PSSDomain {
	public PSSDomain () {
		;
	}
	public boolean isEmpty() {
		return true;
	}
	public PSSDomain join(PSSDomain dom) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::join is not impelemented");
		return null;
	}
	public PSSDomain union(PSSDomain dom) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::union is not impelemented");
		return null;
	}
	public PSSDomain reduceDomainEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainEqual is not impelemented");
		return null;
	}
	public PSSDomain reduceDomainNotEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainNotEqual is not impelemented");
		return null;
	}
	public PSSDomain reduceDomainGreaterThan(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainGreaterThan is not impelemented");
		return null;
	}
	public PSSDomain reduceDomainGreaterEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainGreaterEqual is not impelemented");
		return null;
	}
	public PSSDomain reduceDomainLessThan(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainLessThan is not impelemented");
		return null;
	}
	public PSSDomain reduceDomainLessEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainLessEqual is not impelemented");
		return null;
	}
	public PSSDomain reduceDomainIn(PSSSetVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainIn is not impelemented");
		return null;
	}
	public String getText() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getText is not impelemented");
		return "";
	}
}
