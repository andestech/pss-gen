import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.math.*;

public class PSSNumber {
	public static PSSVal newOctNumber(String text) {
		BigInteger val = new BigInteger(text, 8);
		return new PSSIntVal(val);
	}
	public static PSSVal newDecNumber(String text) {
		int val = Integer.parseInt(text);
		return new PSSIntVal(val);
	}
	public static PSSVal newHexNumber(String text) {
		Pattern pattern = Pattern.compile("0x([0-9a-fA-F]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String hexnumber = m.group(1);
			BigInteger val = new BigInteger(hexnumber, 16);
			return new PSSIntVal(val);
		}
		PSSMessage.Fatal("'" + text + "' is not hex number");
		return null;
	}
	public static PSSVal newBasedBinNumber(String text) {
		Pattern pattern = Pattern.compile("([0-9]+)?'([ss]?)[bB]([01]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String numBits = m.group(1);
			String sign= m.group(2);
			String binnumber = m.group(3);

			if (sign.equals("")) {
				BigInteger val = new BigInteger(binnumber, 2);
				return new PSSIntVal(val);
			}
			else {
				int val = new BigInteger(binnumber, 2).intValue();
				return new PSSIntVal(val);
			}

		}
		PSSMessage.Fatal("'" + text + "' is not base based oct number");
		return null;
	}
	public static PSSVal newBasedOctNumber(String text) {
		// 64'o1
		//   'o2
		Pattern pattern = Pattern.compile("([0-9]+)?'([ss]?)[oO]([0-7]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String numBits = m.group(1);
			String sign= m.group(2);
			String octnumber = m.group(3);

			if (sign.equals("")) {
				BigInteger val = new BigInteger(octnumber, 8);
				return new PSSIntVal(val);
			}
			else {
				int val = new BigInteger(octnumber, 8).intValue();
				return new PSSIntVal(val);
			}

		}
		PSSMessage.Fatal("'" + text + "' is not base based oct number");
		return null;
	}
	public static PSSVal newBasedDecNumber(String text) {
		// 64'd1
		//   'd2
		Pattern pattern = Pattern.compile("([0-9]+)?'([ss]?)[dD]([0-9]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String numBits = m.group(1);
			String sign= m.group(2);
			String decnumber = m.group(3);

			if (sign.equals("")) {
				BigInteger val = new BigInteger(decnumber, 10);
				return new PSSIntVal(val);
			}
			else {
				int val = new BigInteger(decnumber, 10).intValue();
				return new PSSIntVal(val);
			}

		}
		PSSMessage.Fatal("'" + text + "' is not base based decimal number");
		return null;
	}
	public static PSSVal newBasedHexNumber(String text) {
		// 64'h00000000E0500000
		//   'h00000000E0500000
		Pattern pattern = Pattern.compile("([0-9]+)?'([ss]?)[hH]([0-9a-fA-F]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String numBits = m.group(1);
			String sign= m.group(2);
			String hexnumber = m.group(3);

			if (sign.equals("")) {
				BigInteger val = new BigInteger(hexnumber, 16);
				return new PSSIntVal(val);
			}
			else {
				int val = new BigInteger(hexnumber, 16).intValue();
				return new PSSIntVal(val);
			}

		}
		PSSMessage.Fatal("'" + text + "' is not base based hex number");
		return null;
	}
}
